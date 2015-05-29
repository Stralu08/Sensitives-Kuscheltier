package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;

/**
 * Created by Patrick on 24.05.2015.
 */
public class Sound {

    public static final int UNCHECKABLE = -1;
    public static final int NOT_SYNCHED = -2;
    public static final int SYNCHED = 1;


    private File source;
    private MediaPlayer mediaPlayer;
    private int synchState;

    public Sound(File source){
        this.source = source;
    }

    public Sound(String source){
        this.source = new File(source);
    }

    public void play(){
        Log.i("APP: ", "Playing sound "+this);
        try {
            ditchMediaPlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(source.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch(java.io.IOException e){
            Log.i("APP: ", "problem with playback!");
        }
    }

    private void ditchMediaPlayer() {
        if(mediaPlayer != null)
            mediaPlayer.release();
    }

    public void rename(String newName){
        File newFile = new File(source.getParent()+"/"+newName);
        Log.i("APP: ", "Rename sound file "+source+" to "+newFile);
        boolean success = source.renameTo(newFile);
        Log.i("APP: ", "Renaming successful: "+success);
        source = newFile;
    }

    public void setSynchState(int synchState){
        this.synchState = synchState;
    }

    public boolean delete(){
        return source.delete();
    }

    public File getSource(){
        return source;
    }

    public int getSynchState(){
        return synchState;
    }

    @Override
    public String toString() {
        return source.getName();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Sound && toString().equals(o.toString());
    }
}
