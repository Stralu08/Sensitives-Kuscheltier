package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Patrick, 14.04.2015.
 * Wraps a socket for easier writing and reading operations from and to socket
 */
public class ClientSocket {

    public static final int FILE_EXISTS = 2;
    public static final int FILE_DOES_NOT_EXIST = -2;
    public static final int FILE_NOT_CHECKABLE = -4;

    private static final int DATA_PER_CHUNK = 4096;
    private static final int DEFAULT_TIMEOUT = 5000;
    public static final int CONNECTED = 0;
    public static final int CONNECTION_FAILED = 1;

    private Socket client;
    private InputStream raw_in;
    private OutputStream raw_out;
    private BufferedReader string_in;
    private BufferedWriter string_out;

    public ClientSocket() {
        client = new Socket();
    }

    /**
     * Connects the wrapped socket to a specific host and port
     * @param host hostname or ip as string
     * @param port port as integer
     * @return ClientSocket.CONNECTED if the connection could be successfully established
     *         ClientSocket.CONNECTION_FAILED if not
     */
    public int connect(String host, int port) {
        try {
            client.connect(new InetSocketAddress(host, port), DEFAULT_TIMEOUT);
            raw_in = client.getInputStream();
            raw_out = client.getOutputStream();
            string_in = new BufferedReader(new InputStreamReader(raw_in));
            string_out = new BufferedWriter(new OutputStreamWriter(raw_out));
            return CONNECTED;
        } catch (IOException e) {
            return CONNECTION_FAILED;
        }
    }

    /**
     * Sends a message, adding a ';'
     * (server uses this character as delimeter for messages) to end of message
     * @param s message to send
     * @throws IOException if the output stream was null
     */
    public void sendMessage(String s) throws IOException {
        if(string_out != null) {
            string_out.write(s + ";");
            string_out.flush();
            Log.i("APP: ", "msg sent: "+s);
        } else
            throw new IOException("output stream was null");
    }

    /**
     * Receives a message, reads the input stream until a '\n' appears and return the string read
     * blocks until '\n' is found
     * @return the message received
     * @throws IOException if the end of the stream was reached (broken connection)
     */
    public String receiveMessage() throws IOException {
        String msg = string_in.readLine();
        if(msg==null)
            throw new IOException("message was null, end of input stream");
        return msg;
    }

    /**
     * Sends a file to the server
     * @param fileToSend the file to send
     * @param name the name under which to save to file on the server
     * @throws IOException if the connection breaks or the file could not be read
     */
    public void sendFile(File fileToSend, String name) throws IOException {
        int length = (int) fileToSend.length();
        sendMessage("push " + fileToSend + " " + name + " " + length);
        byte[] buf = new byte[length];
        BufferedInputStream fileInputStream = new BufferedInputStream(
                new FileInputStream(fileToSend));
        for (int sent = 0; sent <= length; sent += DATA_PER_CHUNK) {
            if (length - sent < DATA_PER_CHUNK) {
                fileInputStream.read(buf, sent, length - sent);
                raw_out.write(buf, sent, length - sent);
            } else {
                fileInputStream.read(buf, sent, DATA_PER_CHUNK);
                raw_out.write(buf, sent, DATA_PER_CHUNK);
            }
            Log.d("APP", "sent: " + sent + "; data per chunk: " + DATA_PER_CHUNK + "; length: " + length);
            raw_out.flush();
        }
        fileInputStream.close();
        sendMessage("");
    }

    /**
     * Sends a file to the server
     * @param fileToSend path file to send
     * @param name the name under which to save to file on the server
     * @throws IOException if the connection breaks or the file could not be read
     */
    public void sendFile(String fileToSend, String name) throws IOException {
        sendFile(new File(fileToSend), name);
    }

    /**
     * Asks the server if a specific file exists
     * @param path the path of the file on the server
     * @return ClientSocket.FILE_EXISTS if it exists
     *         ClientSocket.FILE_DOES_NOT_EXIST if it does not exist
     *         ClientSocket.FILE_NOT_CHECKABLE if not connected
     */
    public int existsFile(String path){
        try {
            sendMessage("exists " + path);
            String answer = receiveMessage();
            if(answer.equals("yes"))
                return FILE_EXISTS;
            else
                return FILE_DOES_NOT_EXIST;
        } catch (IOException e){
            Log.e("APP: ", "failed to check if file "+path+" exists", e);
            return FILE_NOT_CHECKABLE;
        }
    }

    /**
     * Sends request to delete a file
     * @param path path of file to be deleted on server
     * @throws IOException if the output stream was null
     */
    public void deleteFile(String path) throws IOException{
        sendMessage("delete "+path);
    }
    /* not implemented...
    public String[] listFilesInDir(){
        sendMessage("list");

        return null;
    }*/

    /**
     * wrapper-method for socket.isConnected()
     * @return the result for the method socket.isConnected() on the wrapped socket
     */
    public boolean isConnected() {
        return client.isConnected();
    }

    /**
     * Sends a request to quit the connection from server and closes socket.
     * @throws IOException if socket could not be closed or the message could not be sent
     */
    public void destroy() throws IOException{
        sendMessage("quit");
        client.shutdownInput();
        client.shutdownOutput();
        client.close();
    }
}
