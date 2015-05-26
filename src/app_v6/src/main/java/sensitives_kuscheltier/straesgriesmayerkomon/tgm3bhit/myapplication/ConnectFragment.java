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

import java.io.IOException;


/**
 * Created by Patrick, 17.02.2015.
 */
public class ConnectFragment extends Fragment {

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

    public void connect() {
        EditText connectToIP = (EditText) getView().findViewById(R.id.edit_ip);
        EditText connectToPort = (EditText) getView().findViewById(R.id.edit_port);
        String ipString = connectToIP.getText().toString();
        int port;
        try {
            if (socket != null && !socket.isConnected())
                socket.destroy();
            port = Integer.parseInt(connectToPort.getText().toString());
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
                Log.i("APP: ", "unable to write: IOException");
            }
        }
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
            Log.i("APP: ", "trying to connect to " + ip + "@" + port);
            int success = socket.connect(ip, port);
            Looper.prepare();
            if (success == ClientSocket.CONNECTED) {
                Log.i("APP: ", "connection successfully established");
                toastOnUiThread("Verbunden!", Toast.LENGTH_SHORT);
            } else {
                Log.i("APP: ", "connection failed");
                toastOnUiThread("Verbinden fehlgeschlagen!", Toast.LENGTH_SHORT);
            }
        }

        private void toastOnUiThread(final String msg, final int length){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), msg, length).show();
                }
            });
        }
    }

    public ClientSocket getClientSocket(){
        return socket;
    }
}
