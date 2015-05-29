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

public class ClientSocket {

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

    public void sendMessage(String s) throws IOException {
        string_out.write(s+";");
        string_out.flush();
    }

    public String receiveMessage() throws IOException {
        return string_in.readLine();
    }

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
    }

    public void sendFile(String fileToSend, String name) throws IOException {
        sendFile(new File(fileToSend), name);
    }

    public boolean existsFile(String path){
        try {
            sendMessage("exists " + path);
            String answer = receiveMessage();
            if(answer.equals("yes"))
                return true;
            else
                return false;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean isConnected() {
        return client.isConnected();
    }


    public void destroy() {
        try {
            client.shutdownInput();
            client.shutdownOutput();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getRaw_in() {
        return raw_in;
    }

    public OutputStream getRaw_out() {
        return raw_out;
    }
}
