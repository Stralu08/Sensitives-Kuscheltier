__author__ = 'Patrick Komon'

import logging
import pygame.mixer


def playback(sound_file):
    """ Plays back an audio file using pygame
    (supported type - look at the pygame website)
    :param sound_file: the audio file
    :return:
    """
    pygame.mixer.init(32000, -16, 2)  #TODO
    channel = pygame.mixer.Channel(1)
    try:
        sound = pygame.mixer.Sound(sound_file)
    except IOError:
        logging.error("Error: file not found! aborting function")
        return
    logging.info("Starting playback...")
    channel.play(sound)


def stop_playback():
    """ Stops any audio playback (using pygame)
    :return:
    """
    logging.info("Stopping playback...")
    pygame.mixer.stop()