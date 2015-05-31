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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jakob Grieshofer and Patrick Komon on 24.05.2015.
 * Fragment for managing sound files.
 * All "planned: ..."-features are not implemented! (bad time management) :(
 * Lets the user
 *  - record and name a new file, file will be uploaded to RPi (if connected)
 *    the recorded file will be stored locally inside the "audiofiles" directory
 *    and uploaded to the RPi and stored inside the "sounds" directory.
 *  - playback a sound file from the list (locally, speakers of the smartphone)
 *    planned: playing back sound either on smartphone or on raspberry
 *  - delete a file (also from RPi (if connected))
 *  - planned: upload files, that are recorded while the RPi was not connected
 *  - planned: download files from RPi
 */
public class NewAudioFragment extends android.support.v4.app.Fragment{

    public static final String AUDIO_FILES_DIR = Environment.getExternalStorageDirectory() +"/audiofiles";
    public static final String TEMPORARY_OUTPUT_FILE = AUDIO_FILES_DIR+"/recording.temp";
    public static final String REMOTE_SOUND_PATH = "/home/pi/projekt/sounds";

// list of files in the smartphones local storage ("audiofiles"-dir)
    private List<Sound> sounds;

    private SoundArrayAdapter arrayAdapter;
    private int selectedIndex = -1;
    private Button record, playback, delete;
    private ListView listView;

    private MediaRecorder recorder;

    /**
     * Sets the layout_audio.xml file as root view, sets the OnClickListener of the buttons and list view
     * creates the audiofiles-dir if it does not exist already (maybe move to constructor)
     * Checks the files inside the audiofiles-dir and adds them to the list
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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
        arrayAdapter = new SoundArrayAdapter(getActivity(), sounds);
        listView =(ListView) rootView.findViewById(R.id.listView);
        listView.setOnItemClickListener(new ItemClickHandler());
        listView.setAdapter(arrayAdapter);
        createDirIfNotExists(new File(AUDIO_FILES_DIR));
        refreshList();
        checkFilesOnPi();
        return rootView;
    }

    /**
     * Method to create the audiofiles-directory if it does not exist at this point.
     */
    public void createDirIfNotExists(File dir) {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("APP: ", "could not create audiofiles folder");
            }
        }
    }

    /**
     * Starts recording. All recording are saved to the same temporary file, which will be renamed.
     * Process of adding a new sound file:
     *  1. press button, recording starts, saved to temp. file
     *  2. release button, recording ends
     *  3. dialog for naming the record shows up
     *      3.1 button "Okay": renaming the temporary file
     *      3.2 button "Abbrechen": delete temporary file
     * @throws IOException
     */
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

    /**
     * Stops recording
     */
    private void stopRecording(){
        Log.i("APP: ", "Stop recording...");
        try {
            recorder.stop();
        } catch (RuntimeException e){
            Log.i("APP: ", "couldn't stop recorder: maybe it wasn't even started?");
        }
    }

    /**
     * Resets media recorder for future use
     */
    private void ditchMediaRecorder(){
        if(recorder != null)
            recorder.release();
    }

    /**
     * "Refreshes" the list of sound files:
     * Loop through ALL files of the audiofiles directory, creates new Sound instance for
     * every file and adds it to list.
     * So: DO NOT add any file to this directory manually
     */
    private void refreshList(){
        for(File f: new File(AUDIO_FILES_DIR).listFiles()){
            if(!sounds.contains(new Sound(f))){
                sounds.add(new Sound(f));
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Shows up a dialog for naming the recently recorded sound file
     */
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

    private void checkFilesOnPi(){
/*      planned method for synchronizing:
        get a list of all files from raspberry, check what files are on the smartphone and
        building a list, each sound has an attribute if it is on RPi, smartphone or both
        and user can choose which file to delete, upload to RPi (
        and download to smartphone, completely left out)
        if(((MainActivity)getActivity()).getClientSocket()==null || !((MainActivity)getActivity()).getClientSocket().isConnected()){
            for(Sound s: sounds) {
                s.setSynchState(Sound.UNCHECKABLE);
            }
        }else {
            new Thread(){
                @Override
                 public void run() {
                    try {
                        ClientSocket socket = ((MainActivity) getActivity()).getClientSocket();
                        socket.sendMessage("list");
                        String answer = socket.receiveMessage();
                        Log.i("APP: ", "list: " + answer);
                        String[] parts = answer.split(";");

                    } catch (IOException e){

                    }
                }
            }.start();
        }*/
        new Thread(){ //new thread for network operations
            @Override
            public void run() {
                if(((MainActivity)getActivity()).getClientSocket()==null || !((MainActivity)getActivity()).getClientSocket().isConnected()){
                    for(Sound s: sounds) {
                        s.setSynchState(Sound.UNCHECKABLE);
                    }
                }else {
                    for(Sound s: sounds){
                        int exists = ((MainActivity)getActivity()).getClientSocket().existsFile(REMOTE_SOUND_PATH+"/"+s);
                        if(exists == ClientSocket.FILE_EXISTS){
                            s.setSynchState(Sound.SYNCHED);
                        } else if (exists == ClientSocket.FILE_DOES_NOT_EXIST){
                            s.setSynchState(Sound.NOT_SYNCHED);
                        } else {
                            s.setSynchState(Sound.UNCHECKABLE);
                        }
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        arrayAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();
    }

    /**
     * Handles clicks on list items within the list view that holds the sound files.
     * Only changes the selcetedIndex attribute
     */
    private class ItemClickHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedIndex = position;
            Log.i("APP: ", "selected index: " + position);
        }
    }

    /**
     * Handles clicks on the playback, record and delete button
     * - playback: plays back the selected sound file
     * - delete: deletes the file from the local storage and sends (if connected) a request
     *   for deleting the file on the RPi; BUG: if a file is only stored locally, and the user
     *   tries to delete that file, he will be disconnected from the server
     * - record: while pressed: records a sound
     *           if released: stops recording and shows up a dialog for naming
     *           also tries to send the file to the RPi (only works - u got it - when connected)
     */
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
                Toast.makeText(getActivity(), sounds.get(selectedIndex) + " gelöscht!", Toast.LENGTH_SHORT).show();
                // delete also from RPi
                ClientSocket socket = ((MainActivity)getActivity()).getClientSocket();
                try {
                    socket.deleteFile(REMOTE_SOUND_PATH+"/"+sounds.get(selectedIndex));
                    Toast.makeText(getActivity(), sounds.get(selectedIndex) + " auch vom Teddy gelöscht!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getActivity(), sounds.get(selectedIndex) + " konnte nicht vom Teddy gelöscht werden!", Toast.LENGTH_SHORT).show();
                    Log.e("APP: ", "Sending delete-request failed", e);
                }
                sounds.get(selectedIndex).delete();
                sounds.remove(selectedIndex);
                arrayAdapter.notifyDataSetChanged();
                if(sounds.isEmpty())
                    selectedIndex = -1;
            }
        }
    }

    /**
     * Handles clicks on the button of the dialog for naming a sound file.
     */
    private class DialogClickHandler implements DialogInterface.OnClickListener{

        private EditText fileName;

        /**
         * Creates a new DialogClickHandler instance
         * @param fileName
         */
        public DialogClickHandler(EditText fileName){
            this.fileName = fileName;
        }

        /**
         * If the positive button within the dialog is clicked, the sound will be renamed to the
         * entered string and it will be tried to send the file to the RPi.
         * If the negative button within the dialog is clicked, the temporary sound file will be
         * deleted.
         * @param dialog
         * @param which
         */
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
                            ((MainActivity)getActivity()).getClientSocket().sendFile(newSound.getSource(),
                                    REMOTE_SOUND_PATH+"/"+newSound.toString());//design mistake...
                            newSound.setSynchState(Sound.SYNCHED);
                        }catch (IOException e){
                            Toast.makeText(getActivity(), "Sound konnte nicht gesendet werden", Toast.LENGTH_SHORT).show();
                            newSound.setSynchState(Sound.NOT_SYNCHED);
                            e.printStackTrace();
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

    /**
     * Handles the input for the record button:
     * if pressed: app starts to record sound and stores in temporary file
     * if released: app stops to record and asks user to name file, user can also cancel
     * and discard his record.
     */
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
