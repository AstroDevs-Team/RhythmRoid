import io
import os
import mutagen
import urllib.parse
import subprocess
from PIL import Image
from bs4 import BeautifulSoup
from django.http import JsonResponse
from rhythmroid.settings import STATIC_URL
from mutagen.id3 import ID3, APIC

def initial(request):
    return JsonResponse({'result': False, 'description': 'RhythmRoid api main page.'})

def playing_status(request, manual_run=False):

    def gdbus_get(prop):
        return ' '.join(
            ['gdbus', 'call'] +
            ['--session'] +
            ['--dest', 'org.mpris.MediaPlayer2.rhythmbox'] +
            ['--object-path', '/org/mpris/MediaPlayer2'] +
            ['--method', 'org.freedesktop.DBus.Properties.Get'] +
            ['org.mpris.MediaPlayer2.Player', prop]
        )

    with open(os.path.sep.join([
            '', 'home', os.getlogin(), '.local', 'share', 'rhythmbox', 'rhythmdb.xml'
            ]), 'r+') as rhythmbox_db:
        rhythmbox_db_xml = rhythmbox_db.read()
        rhythmbox_db.close()

    soup = BeautifulSoup(rhythmbox_db_xml, features='xml')

    songs = soup.find_all('entry', {'type': 'song'})

    title = subprocess.check_output(
        'rhythmbox-client --no-start --print-playing '.split()
    )
    volume = subprocess.check_output(
        'rhythmbox-client --no-start --print-volume'.split()
    )

    try:
        play_status = subprocess.check_output(
            gdbus_get('PlaybackStatus'),
            shell=True
        )

        play_position = subprocess.check_output(
            gdbus_get('Position'),
            shell=True
        )
    except:
        META['result'] = False
        META['description'] = 'rythmbox is not started playing'

    music_meta = title.decode('UTF-8').strip().split('-')

    META = {
        'result': True,
        'meta': {
            'title': '-'.join(music_meta[1:]).strip(),
            'singer': music_meta[0].strip()
        },
        'volume': volume.decode('UTF-8').strip().split()[-1][:-1],
        'playing': True if 'playing' in str(play_status).lower() else False,
        'position': int(
            str(play_position.decode('utf-8')).strip().split()[1].split('>')[0]
        ) / 1000000
    }

    for song in songs:
        if not META['result']:
            break
        if song.find('title').text.strip() == music_meta[1].strip():
            META['meta']['album'] = song.find('album').text.strip()
            META['meta']['genre'] = song.find('genre').text.strip()
            META['meta']['timebyseconds'] = song.find('duration').text.strip()

            time = int(song.find('duration').text.strip())

            META['meta']['time'] = '{}:{}'.format(
                '0'+str(time // 60) if time // 60 < 10 else str(time // 60),
                '0'+str(time % 60) if time % 60 < 10 else str(time % 60)
            )
            images_path = __file__.split(os.sep)[:-2]+['images']
            generated_image_path = os.path.sep.join(images_path+[META['meta']['title']+'.jpg'])
            if not os.path.isfile(generated_image_path):
                audio_file = mutagen.File(
                    urllib.parse.unquote(song.find('location').text.strip().replace('file://', '')),
                    easy=False
                )
                try:
                    if audio_file.tags and APIC in audio_file.tags:
                        apic = audio_file.tags[APIC][0]  # Get the first APIC frame
                        mp3_data = apic.data
                        Image.open(io.BytesIO(mp3_data)).save(generated_image_path)
                except KeyError as key_error:
                    META['meta']['image'] = '/{}{}'.format(STATIC_URL, 'default.jpg')
            if 'image' not in META['meta']:
                META['meta']['image'] = '/{}{}'.format(
                    STATIC_URL,
                    urllib.parse.quote(META['meta']['title']+'.jpg')
                )        

            break

    return JsonResponse(META) if not manual_run else META

def play_actions(request):
    action = request.GET.get('action')
    META = {'result': True, 'description': str()}
    if not action:
        META['result'], META['description'] = False, 'No action received'
        return JsonResponse(META)

    if action == 'play':
        subprocess.Popen('rhythmbox-client --no-start --play'.split())
        META['description'] = 'The music was played.'
    
    elif action == 'pause':
        subprocess.Popen('rhythmbox-client --no-start --pause'.split())
        META['description'] = 'The music was paused.'
    
    elif action == 'stop':
        subprocess.Popen('rhythmbox-client --no-start --stop'.split())
        META['description'] = 'The music was stopped.'
    
    elif action == 'next':
        subprocess.Popen('rhythmbox-client --no-start --next'.split())
        META['description'] = 'The next music was played.'
    
    elif action == 'previous':
        subprocess.Popen('rhythmbox-client --no-start --previous'.split())
        META['description'] = 'The previous music was played.'

    elif action == 'seek':
        if not request.GET.get('position'):
            META['result'], META['description'] = False, 'position parameter not recived'
            return JsonResponse(META)
        try:
            subprocess.Popen(str('rhythmbox-client --no-start --seek {}'.format(
                request.GET.get('position')
            )).split())
            META['description'] = 'The position of the music changed'
        except:
            META['result'], META['description'] = False, 'an error occurred'
            return JsonResponse(META) 
    else:
        META['result'], META['description'] = False, 'Unknown action received'
        return JsonResponse(META)
    
    if action not in ['stop']:
        META ['status'] = playing_status(request, manual_run=True)
    return JsonResponse(META)
