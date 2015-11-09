package signin.ez.ezsignin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
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
    public void onResume() {
        Log.v(TAG, "onResume");

        super.onResume();
        mRecordList = MainActivity.readRecords(getBaseContext());
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
        if (id == R.id.menu_delete_all) {
            /* Delete all */
            /* Get confirmation before removing */
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Confirm Delete All Records")
                    .setMessage("Are you sure you want to remove all " + mRecordList.size() + " records?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Iterator<Record> recordIter = mRecordList.iterator();
                            while (recordIter.hasNext()) {
                                Record r = recordIter.next();
                                recordIter.remove();
                            }
                            MainActivity.writeRecord(getBaseContext(), mRecordList);
                            RecordListAdapter adapter = new RecordListAdapter(getBaseContext(), android.R.layout.simple_list_item_1, mRecordList);
                            mListView.setAdapter(adapter);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
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

        final Record recordToRemove = (Record)v.getTag();

        /* Get confirmation before removing */
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm Delete Record")
                .setMessage("Are you sure you want to remove this record ("+recordToRemove.getName()+")?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRecordList.remove(recordToRemove);
                        MainActivity.writeRecord(getBaseContext(), mRecordList);
                        RecordListAdapter adapter = new RecordListAdapter(getBaseContext(), android.R.layout.simple_list_item_1, mRecordList);
                        mListView.setAdapter(adapter);
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v(TAG, "Clicked a record");
        Record selectedRecord = (Record)parent.getItemAtPosition(position);

        //Intent detailRecordIntent = new Intent(this, DetailRecordActivity.class);
        Intent detailRecordIntent = new Intent(this, MainActivity.class);
        detailRecordIntent.putExtra(MainActivity.KEY_RECORD, selectedRecord);
        detailRecordIntent.putExtra(MainActivity.KEY_MODIFY, true);
        startActivity(detailRecordIntent);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(R.id.listview_record);
        list.setEmptyView(empty);
    }
}
