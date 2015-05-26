__author__ = 'Patrick'

import Server
import Audio
import Files
import logging
import GPIOInputCallbacks


class Controller(object):
    """ Controller class:
            Sets up the Server, handles messages/commands sent by the client
    """
    AUDIO_PLAYBACK = "playback"
    STOP_AUDIO_PLAYBACK = "playback_stop"
    PUSH_FILE = "push"
    LIST_FILES = "list"


    def __init__(self):
        """ Creates a default Controller object
        :return: a default Controller object
        """
        self.server = Server.ServerSocket()
        self.server.prepare()

    def handle_command(self, msg):
        """ Reacts to a message
        :param msg: the message to react to
        :return:
        """
        parts = msg.split(" ")
        if parts[0] == Controller.AUDIO_PLAYBACK:
            if len(parts) > 1:
                Audio.playback(parts[1])
            else:
                logging.warning("too less args")
        elif parts[0] == Controller.STOP_AUDIO_PLAYBACK:
            Audio.stop_playback()
        elif parts[0] == Controller.PUSH_FILE:
            if len(parts) > 3:
                Files.receive_file(parts[2], self.server, int(parts[3]))
            else:
                logging.warning("too less args")
        elif parts[0] == Controller.LIST_FILES:
            self.server.send(Files.list_files())

    def start(self):
        """ Starts listening for clients, receives messages
        and if the connection breaks up, it starts listening again
        :return:
        """
        while True:
            self.server.accept_connection()
            while True:
                command = self.server.receive(decoded=True)
                if command is not None:
                    self.handle_command(command)
                else:
                    break

if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    GPIOInputCallbacks.setup()
    control = Controller()
    control.start()