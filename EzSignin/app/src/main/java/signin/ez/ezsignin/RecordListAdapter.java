package signin.ez.ezsignin;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
            TextView tt1 = (TextView) v.findViewById(R.id.textViewRecordName);

            if (tt1 != null) {
                tt1.setText(record.getName());
            }
        }

        return v;
    }

}
