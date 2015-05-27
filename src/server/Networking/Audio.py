__author__ = 'Lukas Mayer'
import audioop
import pyaudio
import os
#import Files

#TEMP_FILENAME = "/home/pi/projekt/aufnahmen/record.temp"
TEMP_FILENAME = "lol.wav"

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


def record(filename=TEMP_FILENAME, duration=DEFAULT_DURATION):
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


#Testing am RPi

checkLautstaerke()