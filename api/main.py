import io
import urllib.parse
import subprocess
from pathlib import Path

import mutagen
from bs4 import BeautifulSoup
from mutagen.id3 import APIC
from PIL import Image
from fastapi import FastAPI, Query
from fastapi.staticfiles import StaticFiles

IMAGES_DIR = Path(__file__).parent / "images"
STATIC_PREFIX = "/static/"

app = FastAPI(title="RhythmRoid API")
app.mount("/static", StaticFiles(directory=str(IMAGES_DIR)), name="static")


@app.get("/")
def initial():
    return {"result": False, "description": "RhythmRoid api main page."}


def _gdbus_get(prop: str) -> str:
    return (
        f"gdbus call --session "
        f"--dest org.mpris.MediaPlayer2.rhythmbox "
        f"--object-path /org/mpris/MediaPlayer2 "
        f"--method org.freedesktop.DBus.Properties.Get "
        f"org.mpris.MediaPlayer2.Player {prop}"
    )


def _get_playing_info() -> dict:
    rhythmdb_path = Path.home() / ".local/share/rhythmbox/rhythmdb.xml"
    soup = BeautifulSoup(rhythmdb_path.read_text(), features="xml")
    songs = soup.find_all("entry", {"type": "song"})

    title_bytes = subprocess.check_output(
        ["rhythmbox-client", "--no-start", "--print-playing"]
    )
    volume_bytes = subprocess.check_output(
        ["rhythmbox-client", "--no-start", "--print-volume"]
    )

    try:
        play_status = subprocess.check_output(_gdbus_get("PlaybackStatus"), shell=True)
        play_position = subprocess.check_output(_gdbus_get("Position"), shell=True)
    except subprocess.CalledProcessError:
        return {"result": False, "description": "rhythmbox is not started playing"}

    music_parts = title_bytes.decode("utf-8").strip().split("-")
    song_title = "-".join(music_parts[1:]).strip()
    singer = music_parts[0].strip()

    result = {
        "result": True,
        "meta": {"title": song_title, "singer": singer},
        "volume": volume_bytes.decode("utf-8").strip().split()[-1][:-1],
        "playing": "playing" in str(play_status).lower(),
        "position": int(
            play_position.decode("utf-8").strip().split()[1].split(">")[0]
        ) / 1_000_000,
    }

    for song in songs:
        if song.find("title").text.strip() != song_title:
            continue

        duration = int(song.find("duration").text.strip())
        result["meta"].update({
            "album": song.find("album").text.strip(),
            "genre": song.find("genre").text.strip(),
            "timebyseconds": str(duration),
            "time": f"{duration // 60:02d}:{duration % 60:02d}",
        })

        generated_image_path = IMAGES_DIR / (song_title + ".jpg")
        if not generated_image_path.is_file():
            audio_path = urllib.parse.unquote(
                song.find("location").text.strip().replace("file://", "")
            )
            audio_file = mutagen.File(audio_path, easy=False)
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

    return result


@app.get("/playing_info")
def playing_info():
    return _get_playing_info()


_ACTION_COMMANDS = {
    "play":     ["rhythmbox-client", "--no-start", "--play"],
    "pause":    ["rhythmbox-client", "--no-start", "--pause"],
    "stop":     ["rhythmbox-client", "--no-start", "--stop"],
    "next":     ["rhythmbox-client", "--no-start", "--next"],
    "previous": ["rhythmbox-client", "--no-start", "--previous"],
}

_ACTION_DESCRIPTIONS = {
    "play":     "The music was played.",
    "pause":    "The music was paused.",
    "stop":     "The music was stopped.",
    "next":     "The next music was played.",
    "previous": "The previous music was played.",
}


@app.get("/play_actions")
def play_actions(action: str = Query(None), position: str = Query(None)):
    if not action:
        return {"result": False, "description": "No action received"}

    if action == "seek":
        if not position:
            return {"result": False, "description": "position parameter not received"}
        try:
            subprocess.Popen(["rhythmbox-client", "--no-start", "--seek", position])
            result = {"result": True, "description": "The position of the music changed"}
        except Exception:
            return {"result": False, "description": "an error occurred"}

    elif action in _ACTION_COMMANDS:
        subprocess.Popen(_ACTION_COMMANDS[action])
        result = {"result": True, "description": _ACTION_DESCRIPTIONS[action]}

    else:
        return {"result": False, "description": "Unknown action received"}

    if action != "stop":
        result["status"] = _get_playing_info()

    return result
