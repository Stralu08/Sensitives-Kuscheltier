__author__ = 'Patrick'

import Server
import Media
import Files
import logging
import thread
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
        self.server.prepare(hostname="192.168.43.1")

    def handle_command(self, msg):
        """ Reacts to a message
        :param msg: the message to react to
        :return:
        """
        parts = msg.split(" ")
        if parts[0] == Controller.AUDIO_PLAYBACK:
            if len(parts) > 1:
                #Audio.playback(parts[1]) TODO
                pass
            else:
                logging.warning("too less args")
        elif parts[0] == Controller.STOP_AUDIO_PLAYBACK:
            #Audio.stop_playback() TODO
            pass
        elif parts[0] == Controller.PUSH_FILE:
            if len(parts) > 3:
                Files.receive_file(parts[2], self.server, int(parts[3]))
            else:
                logging.warning("push request: too less args")
        elif parts[0] == Controller.LIST_FILES:
            self.server.send(Files.list_files())
        elif parts[0] == "start" and len(parts) > 1 and parts[1] == "babyfon":
            thread.start_new_thread(Media.startBabyfon)
        elif parts[0] == "takephoto":
            path = Media.take_photo()
            self.server.send(path)
        elif parts[0] == "rotate" and len(parts) > 1:
            GPIOInputCallbacks.motor_control(parts[1])
        elif parts[0] == "exists" and len(parts) > 1:
            if Files.exists(parts[1]):
                self.server.send("yes\n")
            else:
                self.server.send("no\n")

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