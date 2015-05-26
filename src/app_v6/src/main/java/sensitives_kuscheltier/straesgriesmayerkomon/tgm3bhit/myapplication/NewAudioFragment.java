package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
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
 * Created by Jakob on 24.05.2015.
 *
 */
public class NewAudioFragment extends android.support.v4.app.Fragment{

    public static final String AUDIO_FILES_DIR = Environment.getExternalStorageDirectory() +"/audiofiles";
    public static final String TEMPORARY_OUTPUT_FILE = AUDIO_FILES_DIR+"/recording.temp";

    private ConnectFragment connection;
    private List<Sound> sounds;
    //private ArrayAdapter<Sound> arrayAdapter;
    private SoundArrayAdapter arrayAdapter;
    private int selectedIndex = -1;
    private Button record, playback, delete;
    private ListView listView;

    private MediaRecorder recorder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_audio, container, false);
        record = (Button) rootView.findViewById(R.id.recordButton);
        record.setOnTouchListener(new OnTouchHandler());
        playback = (Button) rootView.findViewById(R.id.playbackButton);
        playback.setOnClickListener(new OnClickHandler());
        delete = (Button) rootView.findViewById(R.id.deleteButton);
        delete.setOnClickListener(new OnClickHandler());
        sounds = new ArrayList<Sound>();
//      arrayAdapter = new ArrayAdapter<Sound>(getActivity(), android.R.layout.simple_list_item_1, sounds);
        arrayAdapter = new SoundArrayAdapter(getActivity(), sounds);
        listView =(ListView) rootView.findViewById(R.id.listView);
        listView.setOnItemClickListener(new ItemClickHandler());
        listView.setAdapter(arrayAdapter);
        createDirIfNotExists(new File(AUDIO_FILES_DIR));
        refreshList();
        return rootView;
    }

    /**
     * Method to create a directory if it does not exist at this point.
     */
    public void createDirIfNotExists(File dir) {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("APP: ", "could not create Audiofiles folder");
            }
        }
    }

    public void setConnection(ConnectFragment connection){
        this.connection = connection;
    }

    private void startRecording() throws IOException{
        Log.i("APP: ", "Start recording...");
        ditchMediaRecorder();
        File outFile = new File(TEMPORARY_OUTPUT_FILE);
        Log.i("APP: ", "created temporary output file...");
        if (outFile.exists())
            outFile.delete();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(TEMPORARY_OUTPUT_FILE);
        recorder.prepare();
        recorder.start();
    }

    private void stopRecording(){
        Log.i("APP: ", "Stop recording...");
        try {
            recorder.stop();
        } catch (RuntimeException e){
            Log.i("APP: ", "couldn't stop recorder: maybe it wasn't even started?");
        }
    }

    private void ditchMediaRecorder(){
        if(recorder != null)
            recorder.release();
    }

    private void refreshList(){
        for(File f: new File(AUDIO_FILES_DIR).listFiles()){
            if(!sounds.contains(new Sound(f))){
                sounds.add(new Sound(f));
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    public void dialogSetName() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.layout_audio_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(20);
        userInput.setFilters(filterArray);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogClickHandler(userInput))
                .setNegativeButton("Abbrechen", new DialogClickHandler(userInput));
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class ItemClickHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedIndex = position;
            Log.i("APP: ", "selected index: " + position);
            //View view = listView.getChildAt(position);
        }
    }

    private class OnClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.i("APP: ", "selected index: "+selectedIndex + ", list size: "+sounds.toArray().length);
            if(selectedIndex < 0 || selectedIndex >= sounds.size()) {
                Toast.makeText(getActivity(), "Auswahl ungültig! " +
                        "Bitte wähle einen Sound aus der Liste", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            if (v == playback) {
                sounds.get(selectedIndex).play();
            } else if (v == delete) {
                sounds.get(selectedIndex).delete();
                sounds.remove(selectedIndex);
                arrayAdapter.notifyDataSetChanged();
                if(sounds.isEmpty())
                    selectedIndex = -1;
            }
        }
    }

    private class DialogClickHandler implements DialogInterface.OnClickListener{

        private EditText fileName;

        public DialogClickHandler(EditText fileName){
            this.fileName = fileName;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String name = fileName.getText().toString();
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    if (!name.isEmpty()) {
                        Sound newSound = new Sound(TEMPORARY_OUTPUT_FILE);
                        newSound.rename(name);
                        if(sounds.contains(newSound))
                            sounds.remove(newSound);
                        sounds.add(newSound);
                        arrayAdapter.notifyDataSetChanged();
                        Log.i("APP: ", "Added sound '" + newSound + "' to list");
                        try {
                            connection.getClientSocket().sendFile(newSound.getSource(),
                                    newSound.toString());
                            newSound.setUploaded(true);
                        }catch (Exception e){
                            Toast.makeText(getActivity(), "Sound konnte nicht gesendet werden", Toast.LENGTH_SHORT).show();
                            newSound.setUploaded(false);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Bitte einen Namen eingeben", Toast.LENGTH_SHORT).show();
                        dialogSetName();
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    File tempRecording = new File(TEMPORARY_OUTPUT_FILE);
                    tempRecording.delete();
                    dialog.cancel();
                    Log.i("APP: ", "Canceled progress to add new sound");
                    break;
            }
        }
    }

    private class OnTouchHandler implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    startRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                stopRecording();
                dialogSetName();
            }
            return false;
        }
    }
}
