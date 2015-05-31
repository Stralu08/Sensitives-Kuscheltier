__author__ = 'Lukas Mayer'
import audioop
import pyaudio
import os
import picamera
import time
import logging

#TEMP_FILENAME = "/home/pi/projekt/htdocs/aufnahmen/record.temp"
TEMP_FILENAME_SOUND = "/home/pi/projekt/htdocs/aufnahmen/record.wav"
TEMP_FILENAME_PHOTO = "/home/pi/projekt/htdocs/fotos"

DEFAULT_DURATION = 5
#DEFAULT_THRESHOLD = 1000
DEFAULT_THRESHOLD = 1500
babyfonStarted = False

##################not used :(##################

def getLautstaerke():
    chunk = 1024
    p = pyaudio.PyAudio()
    for i in range(p.get_device_count()):
        dev = p.get_device_info_by_index(i)
        print((i,dev['name'],dev['maxInputChannels']))
    stream = p.open(p, format=pyaudio.paInt16,
                    channels=1,
                    rate=48000,
                    input_device_index=2,
                    input=True)
    data = stream.read(chunk)
    return audioop.rms(data, 2)


def checkLautstaerke():
    while getLautstaerke() < DEFAULT_THRESHOLD:
        pass
    record()


def startBabyfon():
    logging.info("starting babyfone ...")
    global babyfonStarted
    babyfonStarted = True
    while babyfonStarted:
        checkLautstaerke()
        #Files.send_file(TEMP_FILENAME)
    logging.info("stopped babyfon")


def stopBabyfon():
    logging.info("stopping babyfon ...")
    global babyfonStarted
    babyfonStarted = False

##################not used :(##################

def record(filename=TEMP_FILENAME_SOUND, duration=DEFAULT_DURATION):
    """
    Records for specific amount of time
    :param filename: the filename under which to save the recorded sound
    :param duration: the time to record
    :return:
    """
    logging.info("started recording...")
    os.system("arecord " + "-D plughw:1,0 -f cd -d " + str(duration) + " " + str(filename))
    logging.info("finished recording!")


def take_photo():
    """
    Takes a photo and saves it under the subdirectory "fotos"
    as <timestamp>.jpg
    BUG: cam not working, crashes server ._.
    :return: path of the photo taken
    """
    logging.info("taking photo...")
    cam = picamera.PiCamera()
    name = str(time.time())+".jpg"
    path = TEMP_FILENAME_PHOTO + "/" + name
    time.sleep(2)   # giving cam time to prepare
    cam.capture(path)
    cam.close()
    logging.info("saved photo: "+path)
    return "fotos/" + name