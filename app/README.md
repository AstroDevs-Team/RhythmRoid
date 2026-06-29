# RhythmRoid Flutter App

Flutter client for RhythmRoid, a local-network remote controller for Rhythmbox on Linux.

This app is part of the Flutter rewrite. The current implementation includes the app shell, dark theme tokens, routing, a connection screen UI, and a player screen UI. Backend API integration and real playback control are still in progress.

## Requirements

- Flutter SDK
- Dart SDK compatible with `sdk: ^3.10.0`
- Android device or emulator
- RhythmRoid backend running on the same local network when testing real control features

## Run

From this directory:

```bash
flutter pub get
flutter run
```

From the repository root:

```bash
cd app
flutter pub get
flutter run
```

## Project Structure

```text
lib/
├── core/
│   └── config/
│       ├── router/     # GoRouter routes
│       └── theme/      # AppColors, spacing, text styles, ThemeData
├── features/
│   ├── connection/     # Connection screen UI
│   └── player/         # Player screen UI
└── main.dart
```

## Assets

Image assets live in `assets/images/` and are registered in `pubspec.yaml`.

## Current Packages

- `go_router`
- `flutter_screenutil`
- `audio_video_progress_bar`
