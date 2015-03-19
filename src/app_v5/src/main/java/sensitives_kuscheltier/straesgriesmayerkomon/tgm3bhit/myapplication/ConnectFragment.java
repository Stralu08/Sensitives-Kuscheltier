package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Patrick on 17.02.2015.
 */
public class ConnectFragment extends Fragment {

    private Socket socket;
    private BufferedWriter out;
    private EditText console;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_connect, container, false);
        console = (EditText) rootView.findViewById(R.id.edit_console_log);
        return rootView;
    }

    public void connect(){
        EditText connectToIP = (EditText) getView().findViewById(R.id.edit_ip);
        EditText connectToPort = (EditText) getView().findViewById(R.id.edit_port);
        String ipString = connectToIP.getText().toString();
        int port;
        try {
            port = Integer.parseInt(connectToPort.getText().toString());
            new TCPClientThread(ipString, port).start();
        } catch (NumberFormatException e) {
            Log.e("MEINE APP:", e.getMessage());
        }
    }

    public void write() {
        if (socket.isConnected()) {
            EditText editText = (EditText) getActivity().findViewById(R.id.edit_command);
            String command = editText.getText().toString();
            try {
                out.write(command + "\n");
                out.flush();
            } catch (IOException e) {
                consoleOut("IOException in TCP ConnectionActivity, write");
            }
        }
    }

    private void consoleOut(String s) {
        console.getText().append(s + "\n");
    }

    public class TCPClientThread extends Thread {

        private String ip;
        private int port;

        public TCPClientThread(String ip, int port) {
            this.ip = ip;
            this.port = port;

        }

        @Override
        public void run() {
            Looper.prepare();
            try {
                socket = new Socket();
                Log.e("MEINE APP:", "IP: "+ip+"; Port: "+port);
                SocketAddress adr = new InetSocketAddress(ip, port);
                socket.connect(adr, 10000);
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                Log.e("MEINE APP:", "Connection etablished successfully");
                ((MainActivity)getActivity()).showToast("Connection etablished successfully!", Toast.LENGTH_SHORT);
            } catch (IOException e) {
                Log.e("MEINE APP:", e.getMessage());
                ((MainActivity)getActivity()).showToast(e.getMessage(), Toast.LENGTH_SHORT);
            } catch (IllegalArgumentException e) {
                Log.e("MEINE APP:", "IllegalArgumentException, invalid address!");
                ((MainActivity)getActivity()).showToast("Illegal ip/port entered", Toast.LENGTH_SHORT);
            }
        }
    }
}
