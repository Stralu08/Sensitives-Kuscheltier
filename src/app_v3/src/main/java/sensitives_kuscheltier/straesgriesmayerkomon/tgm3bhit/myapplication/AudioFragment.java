package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Jakob on 16.03.2015.
 */
public class AudioFragment extends Fragment{
    private MediaPlayer mediaPlayer;
    private MediaRecorder recorder;
    private int rec=0;
    private String OUTPUT_FILE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_audio, container, false);
        OUTPUT_FILE= Environment.getExternalStorageDirectory()+"/file.3ggp";
        return rootView;
    }

    public void playRecording() throws Exception{
        ditchMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(OUTPUT_FILE);
        mediaPlayer.prepare();
        mediaPlayer.start();
        Toast.makeText(getActivity(), "ABSPIELEN", Toast.LENGTH_SHORT).show();
    }

    public void ditchMediaPlayer() {
        if(mediaPlayer != null){
            try{
                mediaPlayer.release();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void beginRecording(int rec) throws Exception{
        if(rec==1) {
            ditchMediaRecorder();
            File outFile = new File(OUTPUT_FILE);

            if (outFile.exists())
                outFile.delete();

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(OUTPUT_FILE);
            recorder.prepare();
            recorder.start();
            Toast.makeText(getActivity(), "AUDIOAUFNAHME", Toast.LENGTH_SHORT).show();
        }
        else {
            recorder.stop();
            Toast.makeText(getActivity(), "AUDIOENDE", Toast.LENGTH_SHORT).show();
        }
    }
    public void ditchMediaRecorder(){
        if(recorder != null)
            recorder.release();
    }
}
