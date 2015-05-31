__author__ = 'Patrick'

import Server
import Media
import Files
import logging
import thread
import GPIOInputCallbacks
import os


class Controller(object):
    """ Controller class:
            Sets up the Server, handles messages/commands sent by the client
    """
    AUDIO_PLAYBACK = "playback"
    STOP_AUDIO_PLAYBACK = "playback_stop"
    PUSH_FILE = "push"
    LIST_FILES = "list"
    TAKE_PHOTO = "takephoto"
    EXISTS = "exists"
    RECORD = "record"
    END_CONNECTION = "quit"
    DELETE = "delete"
    SHUTDOWN = "shutdown"

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
        # for receiving a file from client
        # syntax:
        # push <path on client> <path on server> <length of file>
        if parts[0] == Controller.PUSH_FILE:
            if len(parts) > 3:
                Files.receive_file(parts[2], self.server, int(parts[3]))
            else:
                logging.warning("push request: too less args")
        # for taking a photo and sending the URL to the client
        # (there is also a web server running...)
        elif parts[0] == Controller.TAKE_PHOTO:
            path = Media.take_photo()
            self.server.send(path+"\n")
        # for checking if a file exists within the sounds directory
        # syntax: exists <path on server>
        elif parts[0] == Controller.EXISTS and len(parts) > 1:
            if Files.exists(parts[1]):
                self.server.send("yes\n")
            else:
                self.server.send("no\n")
        elif parts[0] == Controller.DELETE and len(parts) > 1:
            Files.delete(parts[1])
        # for recording audio for a specific duration
        # syntax: record <duration in sec>
        elif parts[0] == Controller.RECORD and len(parts) > 1:
            Media.record(duration=parts[1])
        # for quitting the connection properly
        elif parts[0] == Controller.END_CONNECTION:
            raise StandardError()
        elif parts[0] == Controller.SHUTDOWN:
            logging.info("shutdown")
            Controller.shutdown()
        ####################################
        # Functions, not in the release :( #
        ####################################
        # for playing back an audio file, not implemented on server or client (nice to have)
        elif parts[0] == Controller.AUDIO_PLAYBACK:
            if len(parts) > 1:
                #Audio.playback(parts[1])
                pass
            else:
                logging.warning("too less args")
        # for stopping all current playbacks, not implemented
        elif parts[0] == Controller.STOP_AUDIO_PLAYBACK:
            #Audio.stop_playback()
            pass
        # sends back a list of the files within the sounds directory
        # (for synch with client, not implemented in app...)
        elif parts[0] == Controller.LIST_FILES:
            self.server.send(Files.list_files())
        # for starting the babyfon, not implemented in app because it did not work properly on RPi
        elif parts[0] == "start" and len(parts) > 1 and parts[1] == "babyfon":
            thread.start_new_thread(Media.startBabyfon(), [], [])
        # for stopping the babyfon, not implemented in app
        elif parts[0] == "stop" and len(parts) > 1 and parts[1] == "babyfon":
            Media.stopBabyfon()
        # for controlling the servo motor (not implemented in app or teddy)
        elif parts[0] == "rotate" and len(parts) > 1:
            GPIOInputCallbacks.motor_control(parts[1])

    @staticmethod
    def shutdown():
        """
        Shuts down
        :return:
        """
        logging.info("received shutdown msg...")
        os.system("shutdown -h now")

    def start(self):
        """ Starts listening for clients, receives messages
        and if the connection breaks up, it starts listening again
        :return:
        """
        while True:
            try:
                self.server.accept_connection()
                while True:
                    try:
                        command = self.server.receive(decoded=True)
                        if command is not None:
                            self.handle_command(command)
                        else:
                            break
                    except (KeyboardInterrupt, StandardError):
                        self.server.connected_socket.close()
                        break
            except KeyboardInterrupt:
                GPIOInputCallbacks.cleanup()
                self.server.destroy()
                break


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    GPIOInputCallbacks.setup()
    control = Controller()
    control.start()