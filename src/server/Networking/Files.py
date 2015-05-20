__author__ = 'Patrick'

import Server
import logging
import os
import Constants
import socket


DEFAULT_MODE = "wb+"


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
    #data_length = server_socket.receive()
    logging.info("expected length="+str(data_length))
    new_file = open(name, DEFAULT_MODE)
    logging.info("Opened file "+name)
    data_received = 0
    data_per_chunk = Constants.DEFAULT_DATA_PER_CHUNK
    logging.info("Starting transfer...")
    while data_received < data_length:
        data = server_socket.receive(decoded=False,length=data_per_chunk)
        new_file.write(data)
        data_received += data_per_chunk
        logging.info("received "+str(data_received)+" bytes (powers of 2)")
    logging.info("transfer completed!")
    new_file.close()


def list_files():
    """ At the moment this method is not really useful!
    Checks directory and returns the names of the files inside it.
    :return: al list of all files in the sound folder
    """
    dir_list = os.listdir("C:\Users\Patrick\PycharmProjects\Networking\sound")
    dir_list_string = ""
    for entry in dir_list:
        dir_list_string += entry + ";"
    print dir_list_string
    return dir_list_string+"\n"