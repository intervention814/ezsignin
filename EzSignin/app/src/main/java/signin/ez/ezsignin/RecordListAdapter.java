package signin.ez.ezsignin;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Freeman on 11/8/15.
 */
public class RecordListAdapter extends ArrayAdapter {
    public RecordListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public RecordListAdapter(Context context, int resource, List<Record> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.record_layout, null);
        }

        Record record = (Record)getItem(position);

        if (record != null) {
            TextView textViewName = (TextView) v.findViewById(R.id.textViewRecordName);
            TextView textViewId = (TextView) v.findViewById(R.id.textViewRecordId);
            TextView textViewDate = (TextView)v.findViewById(R.id.textViewRecordDate);
            ImageButton button = (ImageButton)v.findViewById(R.id.record_remove_button);

            button.setTag(record); /* Set the tag so we can pick it up in ondeleteclick */
            textViewId.setText("# " + record.getRecordId());
            textViewDate.setText(record.getDate());

            if (textViewName != null) {
                textViewName.setText(record.getName().length() == 0 ? "Name: <No Name>" : "Name: " + record.getName());
            }
        }

        return v;
    }

}
