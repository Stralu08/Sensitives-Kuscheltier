__author__ = 'Patrick'

import Server
import logging
import os

DEFAULT_MODE = "wb+"
DEFAULT_AUDIO_PATH = '/home/pi/projekt/sounds'
DEFAULT_PHOTO_PATH = '/home/pi/projekt/photos'


def receive_file(name, server_socket, data_length):
    """ Receives any file.
    :param name: the name of the file to write to
    :param server_socket: the connected Server.ServerSocket
    :param data_length: the length of the file in bytes
    :return:
    """
    if type(name) is not unicode:
        logging.error("parameter 'name' must be unicode, found: "+str(type(name)))
    if type(server_socket) is not Server.ServerSocket:
        logging.error("parameter 'server_socket' must be Server.ServerSocket, found: "+str(type(server_socket)))
    #
    # TODO - if file exists and so on...
    #
    logging.info("Starting file receiving routine...")
    logging.info("expected length="+str(data_length))
    new_file = open(name, DEFAULT_MODE)
    logging.info("opened file "+name)
    data_received = 0
    logging.info("Starting transfer...")
    while data_received < data_length:
        data = server_socket.receive(decoded=False)
        new_file.write(data)
        data_received += len(data)
        logging.info("received "+str(data_received)+" bytes")
    logging.info("transfer completed!")
    new_file.close()
    logging.info("closed file, transfer successful!")


def send_file(name, server_socket, dir=DEFAULT_PHOTO_PATH):
    to_send = open(name, DEFAULT_MODE)
    logging.info("Opened file")
    file_size = os.path.getsize(dir+"/"+name)
    server_socket.send(str(file_size), encoded=True)
    bytes_sent = 0
    logging.info("Size: "+str(file_size))
    while bytes_sent < file_size:
        data = to_send.read(Server.ServerSocket.DEFAULT_DATA_PER_CHUNK)
        logging.info("Current file position: "+str(to_send.tell()))
        server_socket.send(data, encoded=False)
        bytes_sent += len(data)
    logging.info("Transfer completed!")
    to_send.close()
    logging.info("Closed file")


def list_files():
    """ At the moment this method is not really useful!
    Checks directory and returns the names of the files inside it.
    :return: al list of all files in the sounds folder
    """
    dir_list = os.listdir("C:\Users\Patrick\PycharmProjects\Networking\sound")
    dir_list_string = ""
    for entry in dir_list:
        dir_list_string += entry + ";"
    print dir_list_string
    return dir_list_string+"\n"