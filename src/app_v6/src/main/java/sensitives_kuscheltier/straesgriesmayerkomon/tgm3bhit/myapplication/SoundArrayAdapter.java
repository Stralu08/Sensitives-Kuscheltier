package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Patrick, 25.05.2015.
 * ArrayAdapter for ListView of the Sounds.
 */
public class SoundArrayAdapter extends ArrayAdapter<Sound> {

    private final Context context;
    private List<Sound> values;

    /**
     * Creates a new SoundArrayAdapter with the given context and list of values
     * @param context the context to use
     * @param values list of sounds
     */
    public SoundArrayAdapter(Context context, List<Sound> values){
        super(context, -1, values);
        this.values = values;
        this.context = context;
    }

    /**
     * Shows the name of a sound and its conneciton state in one row
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.layout_audio_row, parent, false);
        TextView name = (TextView) row.findViewById(R.id.audio_row_text);
        name.setText(values.get(position).toString());
        TextView state = (TextView) row.findViewById(R.id.audio_row_status_text);
        int synchState = values.get(position).getSynchState();
        if(synchState == Sound.SYNCHED) {
            state.setText("[hochgeladen]"); // move to strings.xml
            state.setTextColor(Color.GREEN);
        } else if(synchState == Sound.NOT_SYNCHED){
            state.setText("[ausständig]"); // move to strings.xml
            state.setTextColor(Color.RED);
        } else if(synchState == Sound.UNCHECKABLE) {
            state.setText("[nicht überprüfbar]"); // move to strings.xml
            state.setTextColor(Color.YELLOW);
        }
        //row.setBackgroundColor(Color.rgb((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
        //maybe?
        return row;
    }
}
