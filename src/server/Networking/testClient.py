
import socket

client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client.connect((socket.gethostname(), 5555))

#client.sendall("LOL\n\r".encode())

#client.sendall("LOL2".encode())

while True:
    client.sendall(input("message: ").encode())