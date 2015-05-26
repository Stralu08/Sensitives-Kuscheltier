package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jakob on 16.03.2015.
 * not in use anymore :(
 */
public class AudioFragment extends Fragment implements View.OnTouchListener,AdapterView.OnItemClickListener{

    private ConnectFragment connection;
    private MediaPlayer mediaPlayer;
    private MediaRecorder recorder;
    private String OUTPUT_FILE;
    private String rename="file";
    private String filename;
    private String selectedItem;
    public Context context;
    private ListView lv;
    private List<String> list;
    private File dir;
    private File to;
    private ArrayAdapter<String> arrayAdapter;

    public AudioFragment(Context context){
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_audio, container, false);
        createDirIfNotExists("Audiofiles");
        OUTPUT_FILE= dir+"/"+rename+".3gp";
        Button button = (Button)rootView.findViewById(R.id.recordButton);
        button.setOnTouchListener(this);
        lv =(ListView)rootView.findViewById(R.id.listView);
        lv.setOnItemClickListener(this);
        list = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,list );
        lv.setAdapter(arrayAdapter);
        addList();
        return rootView;
    }

    /**
     * Method to play a file.
     * @throws Exception
     */
    public void playRecording() throws Exception{
        try {
            ditchMediaPlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(dir + "/" + selectedItem);
            mediaPlayer.prepare();
            mediaPlayer.start();
        //    Toast.makeText(getActivity(), "ABSPIELEN", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toast.makeText(getActivity(), "Kein File ausgewählt!", Toast.LENGTH_SHORT).show();
        }
    }
    public void ditchMediaPlayer() {
        if(mediaPlayer != null){
            try{
                mediaPlayer.release();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to start recording.
     * @param rec
     * @throws Exception
     */
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
        //    Toast.makeText(getActivity(), "AUDIOAUFNAHME", Toast.LENGTH_SHORT).show();
        }
        else {
            recorder.stop();
            dialogSetName();
        //    Toast.makeText(getActivity(), "AUDIOENDE", Toast.LENGTH_SHORT).show();
        }
    }
    public void ditchMediaRecorder(){
        if(recorder != null)
            recorder.release();
    }

    /**
     * Method to create a directory if it does not exist at this point.
     * @param path
     */
    public void createDirIfNotExists(String path) {
        dir = new File(Environment.getExternalStorageDirectory(), path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
            }
        }
    }

    /**
     * Method to name the file.
     * @param input
     */
    public void renameFile(String input){
        rename=input;
        Log.i("Current file name", OUTPUT_FILE);
        File from = new File(OUTPUT_FILE);
        to   = new File(dir+ "/"+rename+ ".3gp");
        from.renameTo(to);

    //    Log.i("From path is", from.toString());
    //    Log.i("To path is", to.toString());
    //    Toast.makeText(getActivity(), "Rename", Toast.LENGTH_SHORT).show();
    }

    /**
     * Method to remove a file.
     */
    public void removeFile(){
        list.remove(selectedItem);
        arrayAdapter.notifyDataSetChanged();

        File outFile = new File(dir+"/"+selectedItem);
        if (outFile.exists())
            outFile.delete();
    }

    /**
     * Method to cancel to save the file.
     */
    public void cancelFile(){
        File outFile = new File(OUTPUT_FILE);
        if (outFile.exists())
            outFile.delete();
    }

    /**
     * OnTouch listener for the record Button.
     * @param v
     * @param event
     * @return
     */
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

    /**
     * Method to add the filename on the ListView.
     */
    public void addList(){
        for (File f : dir.listFiles()) {
            if (f.isFile())
                filename = f.getName();
            if(!list.contains(filename))
            list.add(filename);
        }
    }

    /**
     * Alert Dialog for setting the name of the File.
     */
    public void dialogSetName() {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.layout_audio_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(20);
        userInput.setFilters(filterArray);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String name = userInput.getText().toString();
                                if (list.contains(name + ".3gp")) {
                                    dialogExistingFile(name);
                                } else {
                                    renameFile(name);
                                    addList();
                                    try {
                                        connection.getClientSocket().sendFile(to, name);
                                    }catch (Exception e){
                                        Toast.makeText(getActivity(), "Could not send File", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                .setNegativeButton("Abbrechen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                cancelFile();
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Alert Dialog. Shows if the name of the file you want to save is already existing.
     */
    public void dialogExistingFile(final String name) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.layout_audio_dialog_v2, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                renameFile(name);
                                addList();
                            }
                        })
                .setNegativeButton("Abbrechen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                .setNeutralButton("Umbenennen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialogSetName();
                            }});
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * OnItemClick listener for the ListView.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         selectedItem = lv.getAdapter().getItem(position).toString();
    //     Toast.makeText(getActivity(), ""+selectedItem, Toast.LENGTH_SHORT).show();
    }

    public void setConnection(ConnectFragment connection){
        this.connection = connection;
    }
}

