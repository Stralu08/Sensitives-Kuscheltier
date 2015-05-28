__author__ = 'Lukas Mayer'
import audioop
import pyaudio
import os
import picamera
import time
#import Files

#TEMP_FILENAME = "/home/pi/projekt/htdocs/aufnahmen/record.temp"
TEMP_FILENAME_SOUND = "lol.wav"
TEMP_FILENAME_PHOTO = "/home/pi/projekt/htdocs/fotos"

DEFAULT_DURATION = 5
#DEFAULT_THRESHOLD = 1000
DEFAULT_THRESHOLD = 1500
babyfonStarted = False


def getLautstaerke():
    chunk = 1024
    p = pyaudio.PyAudio()
    stream = p.open(format=pyaudio.paInt16,
                    channels=1,
                    rate=44100,
                    input=True,
                    frames_per_buffer=chunk)
    data = stream.read(chunk)
    return audioop.rms(data, 2)


def record(filename=TEMP_FILENAME_SOUND, duration=DEFAULT_DURATION):
    os.system("arecord " + str(filename) + " -d " + str(duration))


def checkLautstaerke():
    while getLautstaerke() < DEFAULT_THRESHOLD:
        pass
    record()


def startBabyfon():
    global babyfonStarted
    babyfonStarted = True
    while babyfonStarted:
        checkLautstaerke()
        #Files.send_file(TEMP_FILENAME)


def stopBabyfon():
    global babyfonStarted
    babyfonStarted = False


def take_photo():
    cam = picamera.PiCamera()
    cam.capture(TEMP_FILENAME_PHOTO+'/'+str(time.time())+'.jpg')
    return TEMP_FILENAME_PHOTO+'/'+str(time.time())+'.jpg'