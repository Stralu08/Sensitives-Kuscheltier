package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Patrick; 26.05.2015.
 * Class for recording sound from the servers microphone
 * and playing back the last recorded sound
 */
public class BabyfonFragment extends android.support.v4.app.Fragment {

    private ImageButton record, start;
    private EditText duration;
    private final static String TEMP_FILE = "record_selector.wav";

    /**
     * Adds listener and sets attributes...
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_babyfon, container, false);
        record = (ImageButton) rootView.findViewById(R.id.start_record);
        record.setOnClickListener(new OnClickHandler());
        start = (ImageButton) rootView.findViewById(R.id.playback_record);
        start.setOnClickListener(new OnClickHandler());
        duration = (EditText) rootView.findViewById(R.id.record_duration);
        return rootView;
    }

    /**
     * Plays back a specific audio file from the servers web server
     * @param filename
     */
    public void playbackAudioFromUrl(String filename){
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource("http://192.168.43.1/aufnahmen/"+filename);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e("APP: ", "Error while playing back something from URI http://192.168.43.1/aufnahmen/"+filename, e);
        }
    }

    /**
     * Handles clicks on the record and playback button
     * record: sends a message for recording for a specific amount of time and saving it to a
     *         temporary file on the web-server
     * playback: plays back the temporary file on the web server
     */
    private class OnClickHandler implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Log.i("APP: ", "clicked 'record on pi' -button");
            if(v == record) {
                try {
                    String input = duration.getText().toString();
                    if(input.isEmpty())
                        throw new IllegalArgumentException("Keine Länge angegeben"); //move to strings.xml
                    else{
                        ClientSocket socket =  ((MainActivity)getActivity()).getClientSocket();
                        if(socket!=null && socket.isConnected())
                            socket.sendMessage("record "+Integer.parseInt(input));
                    }
                } catch(IOException e){
                    Log.e("APP: ", "could not send request to start recording on RPi", e);
                    toastOnUiThread("Fehler beim Senden der Anfrage! Nicht verbunden?", Toast.LENGTH_SHORT); //move to strings.xml
                } catch (IllegalArgumentException e){
                    Log.e("APP: ", "no length for record specified", e);
                    toastOnUiThread("Bitte Länge der Aufnahme eingeben", Toast.LENGTH_SHORT); //move to strings.xml
                }
            }else if (v == start){
                ClientSocket socket =  ((MainActivity)getActivity()).getClientSocket();
                if(socket!=null && socket.isConnected())
                    playbackAudioFromUrl("record.wav");
            }
        }

        /**
         * For posting toasts from another thread
         * @param msg message to display
         * @param length duration of display
         */
        private void toastOnUiThread(final String msg, final int length){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), msg, length).show();
                }
            });
        }
    }
}
