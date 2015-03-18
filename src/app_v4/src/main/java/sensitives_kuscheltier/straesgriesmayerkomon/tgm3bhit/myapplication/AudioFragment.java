package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Jakob on 16.03.2015.
 */
public class AudioFragment extends Fragment implements View.OnTouchListener{
    private MediaPlayer mediaPlayer;
    private MediaRecorder recorder;
    private int rec=0;
    private String OUTPUT_FILE;
    private String rename="file";
    private String toname;
    public Context context;

    public AudioFragment(Context context){
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_audio, container, false);
        OUTPUT_FILE= Environment.getExternalStorageDirectory()+"/"+rename+".3ggp";
        Button button = (Button)rootView.findViewById(R.id.button2);
        button.setOnTouchListener(this);
        return rootView;
    }

    public void playRecording() throws Exception{
        ditchMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(toname);
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
            dialogExample();
            Toast.makeText(getActivity(), "AUDIOENDE", Toast.LENGTH_SHORT).show();
        }
    }
    public void ditchMediaRecorder(){
        if(recorder != null)
            recorder.release();
    }

    public void renameFile(String input){
        rename=input;
        Log.i("Current file name", OUTPUT_FILE);
        File from = new File(OUTPUT_FILE);
        File to   = new File(Environment.getExternalStorageDirectory()+ "/"+rename+ ".3ggp");
        from.renameTo(to);
        toname = to.toString();
        Log.i("From path is", from.toString());
        Log.i("To path is", to.toString());
        Toast.makeText(getActivity(), "Rename", Toast.LENGTH_SHORT).show();
    }

    public void removeFile(){
        File outFile = new File(toname);
        if (outFile.exists())
            outFile.delete();
    }

    public void cancelFile(){
        File outFile = new File(OUTPUT_FILE);
        if (outFile.exists())
            outFile.delete();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            try {
                beginRecording(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            try {
                beginRecording(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void dialogExample() {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.layout_audio_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                rename = userInput.getText().toString();
                                renameFile(rename);
                                Toast.makeText(getActivity(), ""+rename, Toast.LENGTH_SHORT).show();

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                cancelFile();
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}

