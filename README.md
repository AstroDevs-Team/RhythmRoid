<div align='center'>

# <img width="180" src="https://github.com/AioFall/RhythmRoid/raw/main/assets/logo-blue.png" alt="RhythmRoid"> <br> [RhythmRoid](#)

<img src='https://img.shields.io/badge/Testing-passing-green?logo=github' alt='' />
<img src='https://img.shields.io/badge/Android Studio-blue?logo=Android' alt='' />
<img src='https://img.shields.io/badge/Django-092e20?logo=Django' alt='' />
<img src='https://img.shields.io/badge/Java-black?logo=java' alt='' />
<img src='https://img.shields.io/badge/Python-ffd343?logo=python' alt='' />

</div>
Rhythmroid is a remote android application for Rhythmbox on Linux. Rhythmroid helps you control the music playing in the rhythmbox on your Android phone.

## Using Rhythmroid
1. Download the Rhythmroid APK file from the latest release page and install it on your Android phone.
2. Find your computer's local IP through the ``hostname -I | awk '{print $1}'`` command in the terminal.
3. Clone the repository and open a terminal and cd in the **api** folder.
4. Run the api service using the ``python3 manange.py runserver 0.0.0.0:8000`` command.
5. Open Rhythmbox on your computer and Rhythmroid on Android and put your Local IP (IPv4) in Rhythmroid.
6. All done!

## Screenshots
<img style='border-radius: 5%;' src="https://raw.githubusercontent.com/AioFall/RhythmRoid/main/assets/screenshot1.png" alt="Screenshot-1">

<img style='border-radius: 5%;' src="https://raw.githubusercontent.com/AioFall/RhythmRoid/main/assets/screenshot2.png" alt="Screenshot-2">

## License
This project is under GNU GPLv3 license and be sure to pay attention to the conditions and rules mentioned when using this project.