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

    public static final int DEFAULT_TIMEOUT = 10000;
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
            client.connect(new InetSocketAddress(host, port), 10000);
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
        string_out.write(s);
        string_out.flush();
    }

    public String receiveMessage() throws IOException {
        return string_in.readLine();
    }

    public void sendFile(File fileToSend, String name) throws IOException {
        int length = (int) fileToSend.length();
        sendMessage("push " + fileToSend + " " + name + " " + length);
        int dataPerChunk = 4096;
        byte[] buf = new byte[length];
        BufferedInputStream fileInputStream = new BufferedInputStream(
                new FileInputStream(fileToSend));
        for (int sent = 0; sent <= length; sent += dataPerChunk) {
            if (length - sent < dataPerChunk) {
                fileInputStream.read(buf, sent, length - sent);
                raw_out.write(buf, sent, length - sent);
            } else {
                fileInputStream.read(buf, sent, dataPerChunk);
                raw_out.write(buf, sent, dataPerChunk);
            }

            Log.d("APP", "sent: " + sent + "; data per chunk: " + dataPerChunk + "; length: " + length);

            raw_out.flush();
        }
        fileInputStream.close();
    }

    public void sendFile(String fileToSend, String name) throws IOException {
        sendFile(new File(fileToSend), name);
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public boolean isClosed() {
        return client.isClosed();
    }

    public void destroy() {
        try {
            raw_in.close();
            raw_out.close();
            string_in.close();
            string_out.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
