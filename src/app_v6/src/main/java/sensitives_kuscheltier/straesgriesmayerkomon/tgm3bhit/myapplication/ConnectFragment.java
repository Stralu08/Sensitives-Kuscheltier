package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.IOException;

/**
 * Created by Patrick on 17.02.2015.
 */
public class ConnectFragment extends Fragment {

    private EditText console;
    private ClientSocket socket;
    private TCPClientThread networkOperations;

    public ConnectFragment(){
        socket = new ClientSocket();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_connect, container, false);
        return rootView;
    }

    public void connect(){
        EditText connectToIP = (EditText) getView().findViewById(R.id.edit_ip);
        EditText connectToPort = (EditText) getView().findViewById(R.id.edit_port);
        String ipString = connectToIP.getText().toString();
        int port;
        try {
            port = Integer.parseInt(connectToPort.getText().toString());
            socket.destroy();
            socket = new ClientSocket();
            networkOperations = new TCPClientThread(socket, ipString, port);
            networkOperations.start();
        } catch (NumberFormatException e) {
            Log.e("APP:", e.getMessage());
        }
    }

    public void write() {
        if (socket.isConnected()) {
            EditText editText = (EditText) getActivity().findViewById(R.id.edit_command);
            String command = editText.getText().toString();
            try {
                socket.sendMessage(command);
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
        private ClientSocket socket;

        public TCPClientThread(ClientSocket socket, String ip, int port) {
            this.ip = ip;
            this.port = port;
            this.socket = socket;
        }

        @Override
        public void run() {
            Log.i("APP: ", "trying to connect to " + ip + "@" + port + ":" + socket.connect(ip, port));
        }
    }

    public ClientSocket getClientSocket(){
        return socket;
    }
}
