package signin.ez.ezsignin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class RecordsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "RecordsActibity";

    private ListView mListView = null;
    private List<Record> mRecordList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

//        Intent intent = getIntent();
//        Bundle records = intent.getExtras();
//        if (records != null) {
//            String recordListString = (String)records.get(MainActivity.KEY_RECORDS);
//            Gson gson = new Gson();
//            RecordListWrapper wrapper = gson.fromJson(recordListString, RecordListWrapper.class);
//            mRecordList = wrapper.recordList;
//        }

        mRecordList = MainActivity.readRecords(getBaseContext());
        if (mRecordList == null) {
            Log.v(TAG, "Record list not read from file!");
        }

        mListView = (ListView)findViewById(R.id.list);
        mListView.setOnItemSelectedListener(this);

        /* Can't pass null list to adapter */
        if (mRecordList == null) {
            mRecordList = new ArrayList<Record>();
        }
        RecordListAdapter adapter = new RecordListAdapter(this, android.R.layout.simple_list_item_1, mRecordList);
        mListView.setAdapter(adapter);
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

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Handle removes clicks.
     */
    public void removeRecord(View v) {
        Record recordToRemove = (Record)v.getTag();
        Log.v(TAG, "Removing record " + recordToRemove.getRecordId());
    }
}
