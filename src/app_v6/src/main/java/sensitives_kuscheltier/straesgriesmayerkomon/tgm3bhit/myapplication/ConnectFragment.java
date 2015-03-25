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

    private BufferedWriter out;
    private EditText console;
    private ClientSocket socket;

    public ConnectFragment(ClientSocket socket){
        this.socket=socket;
    }


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
            new TCPClientThread(socket, ipString, port).start();

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
        private ClientSocket socket;

        public TCPClientThread(ClientSocket socket, String ip, int port) {
            this.ip = ip;
            this.port = port;
            this.socket = socket;
        }

        @Override
        public void run() {
            Log.i("app:: ","trying to connect to "+ip+"@"+port+":"+socket.connect(ip, port));
        }
    }
}
