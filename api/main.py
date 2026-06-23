import re
import io
import urllib.parse
import subprocess
from pathlib import Path

import mutagen
from bs4 import BeautifulSoup
from PIL import Image
from fastapi import FastAPI, Query
from fastapi.staticfiles import StaticFiles

IMAGES_DIR = Path(__file__).parent / "images"
STATIC_PREFIX = "/static/"
_DEST  = "org.mpris.MediaPlayer2.rhythmbox"
_OPATH = "/org/mpris/MediaPlayer2"
_IFACE = "org.mpris.MediaPlayer2.Player"

app = FastAPI(title="RhythmRoid API")
app.mount("/static", StaticFiles(directory=str(IMAGES_DIR)), name="static")


# ---------------------------------------------------------------------------
# DBus helpers
# ---------------------------------------------------------------------------

def _dbus_get(prop: str) -> str:
    out = subprocess.check_output([
        "gdbus", "call", "--session",
        "--dest", _DEST,
        "--object-path", _OPATH,
        "--method", "org.freedesktop.DBus.Properties.Get",
        _IFACE, prop,
    ])
    return out.decode("utf-8").strip()


def _dbus_call(method: str, *gvariant_args: str) -> None:
    subprocess.Popen([
        "gdbus", "call", "--session",
        "--dest", _DEST,
        "--object-path", _OPATH,
        "--method", f"{_IFACE}.{method}",
        *gvariant_args,
    ])


# ---------------------------------------------------------------------------
# GVariant output parsers
# ---------------------------------------------------------------------------

def _gv_str(raw: str, key: str) -> str:
    m = re.search(rf"'{re.escape(key)}': <'((?:[^'\\]|\\.)*)'>", raw)
    return re.sub(r"\\(.)", r"\1", m.group(1)) if m else ""


def _gv_double(raw: str) -> float:
    m = re.search(r"<double ([\d.]+)>", raw)
    return float(m.group(1)) if m else 0.0


def _gv_int64(raw: str) -> int:
    m = re.search(r"<int64 (\d+)>", raw)
    return int(m.group(1)) if m else 0


def _gv_artist(raw: str) -> str:
    m = re.search(r"'xesam:artist': <\[(.*?)\]>", raw)
    if not m:
        return ""
    artists = re.findall(r"'((?:[^'\\]|\\.)*)'", m.group(1))
    return ", ".join(re.sub(r"\\(.)", r"\1", a) for a in artists)


# ---------------------------------------------------------------------------
# Shared rhythmdb.xml enrichment (used by both approaches)
# ---------------------------------------------------------------------------

def _enrich_from_rhythmdb(result: dict, track_path: str | None, fallback_title: str) -> None:
    """Add album/genre/duration/image from rhythmdb.xml. Modifies result in-place.
    Matches by file path when available, falls back to matching by title."""
    try:
        rhythmdb_path = Path.home() / ".local/share/rhythmbox/rhythmdb.xml"
        soup = BeautifulSoup(rhythmdb_path.read_text(), features="xml")
    except Exception:
        return

    for song in soup.find_all("entry", {"type": "song"}):
        loc = urllib.parse.unquote(
            song.find("location").text.strip().replace("file://", "")
        )
        db_title = song.find("title").text.strip()

        if track_path:
            if loc != track_path:
                continue
        else:
            if db_title != fallback_title:
                continue

        duration   = int(song.find("duration").text.strip())
        song_title = db_title
        result["meta"].update({
            "title":         song_title,
            "singer":        song.find("artist").text.strip(),
            "album":         song.find("album").text.strip(),
            "genre":         song.find("genre").text.strip(),
            "timebyseconds": str(duration),
            "time":          f"{duration // 60:02d}:{duration % 60:02d}",
        })

        generated_image_path = IMAGES_DIR / (song_title + ".jpg")
        if not generated_image_path.is_file():
            audio_file = mutagen.File(track_path or loc, easy=False)
            try:
                if audio_file and audio_file.tags:
                    apic_key = next(
                        (k for k in audio_file.tags if k.startswith("APIC")), None
                    )
                    if apic_key:
                        Image.open(
                            io.BytesIO(audio_file.tags[apic_key].data)
                        ).save(str(generated_image_path))
            except Exception:
                pass

        if generated_image_path.is_file():
            result["meta"]["image"] = STATIC_PREFIX + urllib.parse.quote(song_title + ".jpg")
        else:
            result["meta"]["image"] = STATIC_PREFIX + "default.jpg"
        break


# ---------------------------------------------------------------------------
# Playing info — primary (MPRIS / gdbus) and legacy (rhythmbox-client)
# ---------------------------------------------------------------------------

def _mpris_info() -> dict:
    """Get playing info entirely via MPRIS properties over gdbus."""
    status_raw   = _dbus_get("PlaybackStatus")
    position_raw = _dbus_get("Position")
    volume_raw   = _dbus_get("Volume")
    metadata_raw = _dbus_get("Metadata")

    url_match = re.search(r"'xesam:url': <'(file://[^']+)'>", metadata_raw)
    if not url_match:
        return {"result": False, "description": "no track loaded"}

    track_path = urllib.parse.unquote(url_match.group(1).replace("file://", ""))

    result = {
        "result": True,
        "meta": {
            "title":  _gv_str(metadata_raw, "xesam:title"),
            "singer": _gv_artist(metadata_raw),
        },
        "volume":   str(int(_gv_double(volume_raw) * 100)),
        "playing":  "'Playing'" in status_raw,
        "position": _gv_int64(position_raw) / 1_000_000,
    }
    _enrich_from_rhythmdb(result, track_path, result["meta"]["title"])
    return result


def _legacy_info() -> dict:
    """Get playing info using rhythmbox-client (original approach).
    gdbus is still used for PlaybackStatus and Position as in the original code."""
    title_bytes  = subprocess.check_output(["rhythmbox-client", "--no-start", "--print-playing"])
    volume_bytes = subprocess.check_output(["rhythmbox-client", "--no-start", "--print-volume"])
    status_raw   = _dbus_get("PlaybackStatus")
    position_raw = _dbus_get("Position")

    music_parts = title_bytes.decode("utf-8").strip().split("-")
    song_title  = "-".join(music_parts[1:]).strip()
    singer      = music_parts[0].strip()

    result = {
        "result": True,
        "meta": {"title": song_title, "singer": singer},
        "volume":   volume_bytes.decode("utf-8").strip().split()[-1][:-1],
        "playing":  "'Playing'" in status_raw,
        "position": _gv_int64(position_raw) / 1_000_000,
    }
    # Legacy: match in rhythmdb.xml by title (no file path available)
    _enrich_from_rhythmdb(result, track_path=None, fallback_title=song_title)
    return result


def _get_playing_info() -> dict:
    try:
        return _mpris_info()
    except Exception:
        pass
    try:
        return _legacy_info()
    except Exception:
        return {"result": False, "description": "rhythmbox is not started playing"}


# ---------------------------------------------------------------------------
# Endpoints
# ---------------------------------------------------------------------------

@app.get("/")
def initial():
    return {"result": False, "description": "RhythmRoid api main page."}


@app.get("/playing_info")
def playing_info():
    return _get_playing_info()


_MPRIS_ACTIONS = {
    "play":     ("Play",     ["rhythmbox-client", "--no-start", "--play"],     "The music was played."),
    "pause":    ("Pause",    ["rhythmbox-client", "--no-start", "--pause"],    "The music was paused."),
    "stop":     ("Stop",     ["rhythmbox-client", "--no-start", "--stop"],     "The music was stopped."),
    "next":     ("Next",     ["rhythmbox-client", "--no-start", "--next"],     "The next music was played."),
    "previous": ("Previous", ["rhythmbox-client", "--no-start", "--previous"], "The previous music was played."),
}


def _run_action(mpris_method: str, legacy_cmd: list) -> None:
    """Send a control command. Tries gdbus first, falls back to rhythmbox-client."""
    try:
        _dbus_call(mpris_method)
    except (FileNotFoundError, OSError):
        subprocess.Popen(legacy_cmd)


@app.get("/play_actions")
def play_actions(action: str = Query(None), position: str = Query(None)):
    if not action:
        return {"result": False, "description": "No action received"}

    if action == "seek":
        if not position:
            return {"result": False, "description": "position parameter not received"}

        sought = False

        # Primary: MPRIS SetPosition
        try:
            metadata_raw = _dbus_get("Metadata")
            trackid_m = re.search(r"'mpris:trackid': <objectpath '([^']+)'>", metadata_raw)
            if trackid_m:
                position_us = int(float(position) * 1_000_000)
                _dbus_call("SetPosition", f"objectpath '{trackid_m.group(1)}'", f"@x {position_us}")
                sought = True
        except Exception:
            pass

        # Fallback: rhythmbox-client --seek
        if not sought:
            try:
                subprocess.Popen(["rhythmbox-client", "--no-start", "--seek", position])
                sought = True
            except (FileNotFoundError, OSError):
                pass

        if not sought:
            return {"result": False, "description": "seek not available"}

        result = {"result": True, "description": "The position of the music changed"}

    elif action in _MPRIS_ACTIONS:
        method, legacy_cmd, description = _MPRIS_ACTIONS[action]
        try:
            _run_action(method, legacy_cmd)
        except Exception:
            return {"result": False, "description": "an error occurred"}
        result = {"result": True, "description": description}

    else:
        return {"result": False, "description": "Unknown action received"}

    if action != "stop":
        result["status"] = _get_playing_info()

    return result
