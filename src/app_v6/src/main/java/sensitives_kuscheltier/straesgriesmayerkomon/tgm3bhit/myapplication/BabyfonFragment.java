package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Patrick; 26.05.2015.
 */
public class BabyfonFragment extends android.support.v4.app.Fragment {

    private Button start, stop;
    private ConnectFragment connection;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_babyfon, container, false);
        start = (Button) rootView.findViewById(R.id.babyfon_start);
        start.setOnClickListener(new OnClickHandler());
        stop = (Button) rootView.findViewById(R.id.babyfon_stop);
        stop.setOnClickListener(new OnClickHandler());
        context = getActivity();
        return rootView;
    }

    public void setConnection(ConnectFragment connection){
        this.connection = connection;
    }

    private class OnClickHandler implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v == start) {
                Log.i("APP: ", "Babyfon started");
                try {
                    connection.getClientSocket().sendMessage("start babyfon");

                } catch(IOException e){
                    Log.i("APP: ", "could not send message to start stream");
                }
            } else if(v == stop) {
                Log.i("APP: ", "Babyfon stopped");
            }
        }
    }
}
