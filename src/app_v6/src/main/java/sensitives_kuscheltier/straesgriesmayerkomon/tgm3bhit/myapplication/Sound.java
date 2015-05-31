package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;

/**
 * Created by Patrick on 24.05.2015.
 * Class representing a Sound file.
 */
public class Sound {

    /*state of sound file:
    SYNCHED if file exists on smartphone and server
    NOT_SYNCHED if file only exists on smartphone
    UNCHECKABLE if it could not be determined if the file exists on the server
     */
    public static final int UNCHECKABLE = -1;
    public static final int NOT_SYNCHED = -2;
    public static final int SYNCHED = 1;

    private File source;
    private MediaPlayer mediaPlayer;
    private int synchState;

    /**
     * creates a new Sound instance with the given File as data source
     * @param source data source
     */
    public Sound(File source){
        this.source = source;
    }

    /**
     * creates a new Sound instance with the given path as data source
     * @param source path of file used as data source
     */
    public Sound(String source){
        this.source = new File(source);
    }

    /**
     * plays back the sound file
     */
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

    /**
     * resets media player
     */
    private void ditchMediaPlayer() {
        if(mediaPlayer != null)
            mediaPlayer.release();
    }

    /**
     * Renames the data source file of a sound
     * @param newName the new name of the sound
     */
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
