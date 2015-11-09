package signin.ez.ezsignin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class RecordsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private static final String TAG = "RecordsActibity";

    private ListView mListView = null;
    private List<Record> mRecordList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        mRecordList = MainActivity.readRecords(getBaseContext());
        if (mRecordList == null) {
            Log.v(TAG, "Record list not read from file!");
        }

        mListView = (ListView)findViewById(R.id.listview_record);
        mListView.setOnItemSelectedListener(this);

        /* Can't pass null list to adapter */
        if (mRecordList == null) {
            mRecordList = new ArrayList<Record>();
        }
        RecordListAdapter adapter = new RecordListAdapter(this, android.R.layout.simple_list_item_1, mRecordList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_records, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.v(TAG, "Selected item!");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Handle removes clicks.
     */
    public void removeRecord(View v) {
        Record recordToRemove = (Record)v.getTag();
        mRecordList.remove(recordToRemove);
        MainActivity.writeRecord(getBaseContext(), mRecordList);
        RecordListAdapter adapter = new RecordListAdapter(this, android.R.layout.simple_list_item_1, mRecordList);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v(TAG, "Clicked a record");
        Record recordClicked = (Record)parent.getItemAtPosition(position);
        Log.v(TAG, "Record: " + recordClicked.getName());
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(R.id.listview_record);
        list.setEmptyView(empty);
    }
}
