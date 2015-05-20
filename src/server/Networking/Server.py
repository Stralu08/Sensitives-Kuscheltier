__author__ = "Patrick Komon"

import socket
import logging
import Constants


class ServerSocket(object):
    """ ServerSocket is a wrapper-class for a socket.
        A socket represents an end-point of a TCP connection. This class is
        (like the name already said) for the server side of this connection.

        It provides methods for
            initializing and preparing for a connection,
            waiting for a client to connect and establish a connection
            sending data (as string)
            receiving data (as string)

        It does not provide
            multiple client connections
    """
    DEFAULT_PORT = 5555
    DEFAULT_BACKLOG = 0

    def __init__(self):
        """ Constructs a new socket with protocol family socket.AF_INIT
            and mode socket.SOCK_STREAM
        """
        logging.basicConfig(level=logging.DEBUG)
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        logging.info("Created server socket")
        self.connected_socket = None

    def prepare(self, hostname=socket.gethostname(), port=DEFAULT_PORT, backlog=DEFAULT_BACKLOG):
        """ Sets up the socket
        :param hostname: the ip or hostname of the server, default to the machines hostname
        :param port: the port, on which the server should run, defaults to 5555
        :param backlog: maximum number of queued connections, defaults to 0
        :return: nothing
        """
        self.server.bind((hostname, port))
        logging.info("Bound server socket to "+hostname+"@"+str(port))
        self.server.listen(backlog)
        logging.info("Backlog ('connection queue length)' set to "+str(backlog))

    def accept_connection(self):
        """ Accepts a connection of a client
        :return: the socket, representing the server-side endpoint of the connection
        """
        logging.info("Waiting for client...")
        (self.connected_socket, address) = self.server.accept()
        logging.info("Connected client: "+str(address[0])+"@"+str(address[1]))
        return self.connected_socket

    def receive(self, decoded=True, length=Constants.DEFAULT_DATA_PER_CHUNK):
        """ Receives a message from the connected client
        :param decoded: if the message should be decoded after receiving or not
        :param length: the length of the message in bytes
        :return: the message received
        """
        try:
            if decoded:
                msg = self.connected_socket.recv(length).decode()
                logging.info("Message received: "+msg)
            else:
                msg = self.connected_socket.recv(length)
        except socket.error:
            logging.warning("current connection closed!")
            return None
        return msg

    def send(self, msg, encoded=True):
        """ Sends a message
        :param msg: the message to send
        :param encoded: if the message should be encoded before sending or not
        :return: nothing
        """
        if encoded:
            self.connected_socket.sendall(msg.encode())
        else:
            self.connected_socket.sendall(msg)

    def destroy(self):
        """ Makes this socket unusable.
        Call, if object is not needed anymore
        :return: nothing
        """
        self.connected_socket.close()
        self.server.close()