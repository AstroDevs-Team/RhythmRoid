<div align='center'>

# <img width="180" src="https://github.com/AioFall/RhythmRoid/raw/main/assets/logo.png" alt="RhythmRoid"> <br> RhythmRoid

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

- 🎵 Play, pause, next, previous — control Rhythmbox from your phone
- 🌐 Connect via local IP — no internet needed
- 🎨 Clean minimal UI with a proper design system
- 🌙 Light & dark mode support
- 🏗️ Feature-first folder structure
- 🤝 Open source — built with the community

## 📂 Folder Structure

```
lib/
├── core/
│   ├── config/
│   │   ├── theme/          # Colors, typography, spacing
│   │   └── router/         # GoRouter setup
│   ├── network/            # Dio client, API handling
│   └── widgets/            # Shared reusable widgets
├── features/
│   ├── connection/         # IP form, connect/disconnect logic
│   │   ├── connection_page.dart
│   │   ├── connection_cubit.dart
│   │   └── connection_state.dart
│   └── player/             # Music controls, now playing UI
│       ├── player_page.dart
│       ├── player_cubit.dart
│       ├── player_state.dart
│       └── widgets/
└── main.dart
```

## 🏁 Getting Started

### Prerequisites
- A Linux machine with [Rhythmbox](https://wiki.gnome.org/Apps/Rhythmbox) installed and running
- Flutter installed on your development machine
- An Android phone on the same local network

### Backend (on your Linux machine)

You can run the backend with Docker (recommended) or directly with Python.

<details open>
<summary><strong>Option A — Docker (recommended)</strong></summary>

**Requires:** Docker and Docker Compose v2

```bash
git clone https://github.com/AstroDevs-Team/RhythmRoid.git
cd RhythmRoid
chmod +x start.sh
./start.sh
```

The script automatically detects your user ID so the container can reach Rhythmbox over DBus. The API will be available at `http://localhost:8000`.

To stop: `docker compose down`

</details>

<details>
<summary><strong>Option B — Direct (Python only)</strong></summary>

**Requires:** Python 3.8+, `libglib2.0-bin` (provides `gdbus` — pre-installed on most GNOME desktops)

```bash
git clone https://github.com/AstroDevs-Team/RhythmRoid.git
cd RhythmRoid/api
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8000
```

</details>

### App (Flutter)
```bash
cd RhythmRoid
flutter pub get
flutter run
```

Then open the app, enter your machine's local IP, and you're connected.

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

- [ ] Project setup — Flutter, feature-first structure, design system
- [ ] Connection page — IP input, connect/disconnect
- [ ] Player page — play, pause, next, previous, album art
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
| Containerization | Docker + Docker Compose |

**App (Flutter)**

| Concern | Package |
|---|---|
| Navigation | `go_router` |
| State Management | `flutter_bloc` |
| Networking | `dio` |
| Dependency Injection | `get_it` |

## Looking for the old Java version?

The original Java + Android Studio version is preserved on the [`legacy`](https://github.com/AstroDevs-Team/RhythmRoid/tree/legacy) branch.

## 📄 License

This project is under [GNU GPLv3](LICENSE) license.
