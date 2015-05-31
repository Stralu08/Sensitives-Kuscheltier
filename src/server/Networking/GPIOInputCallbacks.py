__author__ = 'Patrick'

import RPi.GPIO as GPIO
import os
import logging
import subprocess
from random import randint
import time
import thread

#BOARD numbering!
taster = 18
gassensor = 22

# not used because not implemented
#servo = 32
#servo_pwm = None
#servo_freq = 50
#rgbLED = {'r': 11, 'g': 13, 'b': 15}

#min_rotation = 2.5
#max_rotation = 12.5
#rotation = 7.5

time_start = 0
taster_state = False


def setup():
    """
    method for initialising all gpio pins and setting up callbacks
    :return:
    """
    logging.info("Setting up GPIO pins...")
    GPIO.setmode(GPIO.BOARD)
    # Setup 'normal' GPIO pins
    GPIO.setup(gassensor, GPIO.IN, initial=GPIO.LOW, pull_up_down=GPIO.PUD_DOWN)
    GPIO.setup(taster, GPIO.IN, initial=GPIO.LOW, pull_up_down=GPIO.PUD_DOWN)
    # not used because not implemented
    #GPIO.setup(rgbLED['r'], GPIO.OUT, pull_up_down=GPIO.PUD_DOWN)
    #GPIO.setup(rgbLED['g'], GPIO.OUT, pull_up_down=GPIO.PUD_DOWN)
    #GPIO.setup(rgbLED['b'], GPIO.OUT, pull_up_down=GPIO.PUD_DOWN)
    #Setup PWM pin for servo
    #GPIO.setup(servo, GPIO.OUT)
    #global servo_pwm
    #servo_pwm = GPIO.PWM(servo, servo_freq)
    # Adding event detection and callbacks
    logging.info("Setup edge event detection ...")
    GPIO.add_event_detect(taster, GPIO.BOTH, callback=taster_callback)
    #GPIO.add_event_detect(taster, GPIO.BOTH, callback=test_callback)
    #GPIO.add_event_detect(taster, GPIO.FALLING, callback=taster_callback_falling)
    GPIO.add_event_detect(gassensor, GPIO.RISING, callback=gassensor_callback, bouncetime=3000)


def taster_callback(channel):
    """
    press for less than 10 sec: playing back random sound from sounds-directory
    (press for more than 10 sec: triggering shutdown)
    UPDATE: moved shutdown functionality to app
    :param channel:
    :return:
    """
    global taster_state, time_start
    if taster_state:
        logging.info("released hardware button!")
        time_elapsed = time.time() - time_start
        logging.info("time elapsed: "+str(time_elapsed))
        if time_elapsed > 10:
            pass
            logging.info("Pressed longer than 10sec, shutdown triggered")
            # os.system("shutdown -h now")
            # sometimes the RPi misses an edge and so we moved the shutdown functionality to the app
        else:
            pass
            # logging.info("Playback button pressed")
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
            thread.start_new_thread(os.system("mplayer /home/pi/projekt/sounds/" + sounds[index]))

    else:
        time_start = time.time()
        logging.info("pressed hardware button...")
    taster_state = not taster_state


def gassensor_callback(channel):
    """
    Plays back an alarm sound
    :param channel:
    :return:
    """
    logging.info("GAS!")
    #notification in der app!
    #alarm (als sound)!
    os.system("mplayer /home/pi/projekt/spezial/alarm.mp3")
    #RGB-LED ROT blinkend!


def cleanup():
    """
    Removes callbacks and clean up GPIO-pins
    :return:
    """
    GPIO.remove_event_detect(taster)
    GPIO.remove_event_detect(gassensor)
    GPIO.cleanup()


###### NOT USED
#def motor_control(direction):
#    global rotation
#    if direction is "r" and rotation >= 2.5:
#        rotation += 0.5
#    if direction is "l" and rotation <= 12.5:
#        rotation -= 0.5
    # servo_pwm.changeDutyCycle(rotation)