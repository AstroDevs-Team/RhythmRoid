<div align='center'>

# <img width="180" src="https://github.com/AstroDevs-Team/RhythmRoid/raw/main/app/assets/images/logo.png" alt="RhythmRoid"> <br> RhythmRoid

Remote controller for Rhythmbox on Linux — built with Flutter.

<br>

[![My Skills](https://skillicons.dev/icons?i=flutter,dart,python,fastapi,docker,linux)](https://skillicons.dev)

<br>

<img src='https://img.shields.io/badge/Status-Rewriting_in_Flutter-A855F7?style=for-the-badge' alt='' />
<img src='https://img.shields.io/badge/Contributions-Welcome-4ADE80?style=for-the-badge' alt='' />
<img src='https://img.shields.io/badge/License-GPLv3-FACC15?style=for-the-badge' alt='' />

</div>

<br>

> 🚧 **This project is being rewritten in Flutter.**
> The original Java version is preserved on the [`legacy`](https://github.com/AstroDevs-Team/RhythmRoid/tree/legacy) branch.

---

## What is RhythmRoid?

RhythmRoid is a remote controller for [Rhythmbox](https://wiki.gnome.org/Apps/Rhythmbox) on Linux. It lets you control the music playing on your desktop directly from your Android phone over your local network.

Originally built 4 years ago in Java with messy unstructured code — now being rewritten from scratch in Flutter with a proper architecture, a design system, and community contributions.

## Subscribe To Our Youtube Channel:
<a href="https://www.youtube.com/@astrodevs_team">
 <img width="110" height="40" alt="youtube" src="https://custom-icon-badges.demolab.com/badge/Youtube-red.svg?logo=youtube&logoSource=feather&logoColor=white"/>
</a>


## Features

- 🎵 Player screen UI for play, pause, next, previous, album art, and progress
- 🌐 Connection screen UI for entering a local IP
- 🎨 Dark Flutter UI with centralized color, typography, and spacing tokens
- 🧭 GoRouter-based navigation between connection and player screens
- 🏗️ Feature-first folder structure
- 🤝 Open source — built with the community

## 📂 Folder Structure

```
lib/
├── core/
│   ├── config/
│   │   ├── theme/          # Colors, typography, spacing
│   │   └── router/         # GoRouter setup
├── features/
│   ├── connection/         # IP form UI
│   │   └── presentation/
│   │       └── connection_page.dart
│   └── player/             # Music controls and now playing UI
│       └── player_page.dart
└── main.dart
```

## 🏁 Getting Started

### Prerequisites
- A Linux machine with [Rhythmbox](https://wiki.gnome.org/Apps/Rhythmbox) installed and running
- Docker and Docker Compose v2
- Flutter installed on your development machine
- An Android phone on the same local network

### Backend (on your Linux machine)

<details open>
<summary><strong>Option A — Docker + systemd auto-start (recommended)</strong></summary>

Run once to build the image and register a systemd user service that starts automatically on login:

```bash
git clone https://github.com/AstroDevs-Team/RhythmRoid.git
cd RhythmRoid
./install.sh
```

The script prompts to enable the service on login. Once enabled, the API comes up automatically every time you log in — no manual action needed.

**Managing the service:**

```bash
systemctl --user start rhythmroid      # start
systemctl --user stop rhythmroid       # stop
systemctl --user restart rhythmroid    # restart
systemctl --user status rhythmroid     # status
journalctl --user -u rhythmroid -f     # follow logs
```

**Uninstall:**

```bash
systemctl --user disable --now rhythmroid
rm ~/.config/systemd/user/rhythmroid.service
systemctl --user daemon-reload
```

</details>

<details>
<summary><strong>Option B — Docker one-shot</strong></summary>

Start manually without systemd — useful for testing:

```bash
git clone https://github.com/AstroDevs-Team/RhythmRoid.git
cd RhythmRoid
./start.sh
```

To stop: `docker compose down`

</details>

<details>
<summary><strong>Option C — Direct Python (no Docker)</strong></summary>

**Requires:** Python 3.8+, `libglib2.0-bin` (provides `gdbus` — pre-installed on most GNOME desktops)

```bash
git clone https://github.com/AstroDevs-Team/RhythmRoid.git
cd RhythmRoid/api
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8000
```

</details>

The API is available at `http://<your-local-ip>:8000` once running.

### App (Flutter)
```bash
cd RhythmRoid/app
flutter pub get
flutter run
```

Then open the app and enter your machine's local IP. Real backend connection handling is still part of the rewrite work.

## Screenshots

*Coming soon — the Flutter rewrite is in progress.*

<details>
<summary>📸 Original Java Version Screenshots</summary>
<br>
<img style='border-radius: 5%;' src="https://raw.githubusercontent.com/AioFall/RhythmRoid/legacy/assets/screenshot1.png" alt="Screenshot-1">
<img style='border-radius: 5%;' src="https://raw.githubusercontent.com/AioFall/RhythmRoid/legacy/assets/screenshot2.png" alt="Screenshot-2">
</details>

## 🤝 Contributing

This project is open source and we welcome contributions from everyone — beginners and experienced developers alike.

### How to contribute
1. Fork the repo
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add: your feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

Check the [Issues](https://github.com/AstroDevs-Team/RhythmRoid/issues) tab for open tasks. Issues labeled `good first issue` are great starting points.

### Contribution Guidelines
- Follow the existing folder structure
- Use the design system tokens — no hardcoded colors, font sizes, or spacing
- Keep widgets small and focused
- Write descriptive commit messages

## 🗺️ Roadmap

- [x] Project setup — Flutter, feature-first structure, design system
- [x] Connection page UI — IP input screen
- [x] Player page UI — play, pause, next, previous, album art, progress
- [ ] Backend connection handling from the Flutter app
- [ ] Real playback controls through the API
- [ ] Volume control
- [ ] Now playing progress bar with seek
- [ ] Queue / playlist view
- [ ] Library browser
- [ ] Search
- [ ] Save last used IP
- [ ] Auto-reconnect
- [ ] Multiple server profiles

## 📦 Tech Stack

**Backend**

| Concern | Tool |
|---|---|
| API framework | `FastAPI` + `uvicorn` |
| Rhythmbox control | MPRIS2 over DBus (`gdbus`) |
| Metadata / album art | `mutagen`, `Pillow` |
| Library database | `beautifulsoup4` (reads `rhythmdb.xml`) |
| Containerization | Docker + Docker Compose + systemd user service |

**App (Flutter)**

| Concern | Package |
|---|---|
| Navigation | `go_router` |
| Responsive sizing | `flutter_screenutil` |
| Player progress UI | `audio_video_progress_bar` |

## Looking for the old Java version?

The original Java + Android Studio version is preserved on the [`legacy`](https://github.com/AstroDevs-Team/RhythmRoid/tree/legacy) branch.

## 📄 License

This project is under [GNU GPLv3](LICENSE) license.
