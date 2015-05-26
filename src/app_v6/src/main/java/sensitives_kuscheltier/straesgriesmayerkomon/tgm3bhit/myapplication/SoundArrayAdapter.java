package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick on 25.05.2015.
 */
public class SoundArrayAdapter extends ArrayAdapter<Sound> {

    private final Context context;
    private List<Sound> values;
    private ArrayList<Integer> colors;

    public SoundArrayAdapter(Context context, List<Sound> values){
        super(context, -1, values);
        this.values = values;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.layout_audio_row, parent, false);
        TextView name = (TextView) row.findViewById(R.id.audio_row_text);
        name.setText(values.get(position).toString());
        TextView state = (TextView) row.findViewById(R.id.audio_row_status_text);
        if(values.get(position).isUploaded()) {
            state.setText("[hochgeladen]");
            state.setTextColor(Color.GREEN);
        } else {
            state.setText("[ausständig]");
            state.setTextColor(Color.RED);
        }
        row.setBackgroundColor(Color.rgb((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
        return row;
    }
}
