__author__ = 'Patrick'

import RPi.GPIO as GPIO
import os
import logging
import subprocess
from random import randint

taster_talk = 16
taster_shutdown = 18
servo = 32
servo_pwm = None
servo_freq = 50
gassensor = 22
rgbLED = {'r': 11, 'g': 13, 'b': 15}


def setup():
    logging.info("Setting up GPIO pins...")
    GPIO.setmode(GPIO.BOARD)
    #Setup 'normal' GPIO pins
    GPIO.setup(taster_talk, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
    GPIO.setup(taster_shutdown, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
    GPIO.setup(gassensor, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
    #GPIO.setup(rgbLED['r'], GPIO.OUT, pull_up_down=GPIO.PUD_DOWN)
    #GPIO.setup(rgbLED['g'], GPIO.OUT, pull_up_down=GPIO.PUD_DOWN)
    #GPIO.setup(rgbLED['b'], GPIO.OUT, pull_up_down=GPIO.PUD_DOWN)
    #Setup PWM pin for servo
    #GPIO.setup(servo, GPIO.OUT)
    #global servo_pwm
    #servo_pwm = GPIO.PWM(servo, servo_freq)
    #Adding event detection and callbacks
    logging.info("Adding event detection ...")
    GPIO.add_event_detect(taster_talk, GPIO.RISING, callback=taster_talk_callback, bouncetime=1000)
    GPIO.add_event_detect(taster_shutdown, GPIO.RISING, callback=taster_shutdown_callback)
    GPIO.add_event_detect(gassensor, GPIO.RISING, callback=gassensor_callback, bouncetime=3000)


def taster_talk_callback(channel):
    logging.info("Playback button pressed")
    sounds = []
    for file in os.listdir("/home/pi/projekt/sounds"):
        sounds.append(file)
    logging.info("sound folder contains: "+str(sounds))
    index = randint(0, len(sounds)-1)
    logging.info("Selected file '" + sounds[index]+"'")
    p = subprocess.Popen(["ps", "-a"], stdout=subprocess.PIPE)
    out = p.communicate()
    if "mplayer" in str(out[0]):
        logging.info("player already running... killing player process")
        os.system("pkill mplayer")
    os.system("mplayer /home/pi/projekt/sounds/" + sounds[index])


def gassensor_callback(channel):
    logging.info("GAS!")
    #funktion
    #notification in der app!
    #alarm (als sound)!
    #RGB-LED ROT blinkend!


def taster_shutdown_callback(channel):
    logging.info("Shutdown button pressed")
    os.system("shutdown -h now")
