package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;

/**
 * Created by Patrick on 24.05.2015.
 */
public class Sound {

    private File source;
    private MediaPlayer mediaPlayer;
    private boolean uploaded;

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

    public void setUploaded(boolean b){
        this.uploaded = b;
    }

    public boolean delete(){
        return source.delete();
    }

    public File getSource(){
        return source;
    }

    public boolean isUploaded(){
        return uploaded;
    }

    @Override
    public String toString() {
        return source.getName();
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Sound))
            return false;
        return toString().equals(o.toString());
    }
}
