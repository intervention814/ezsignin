package signin.ez.ezsignin;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final static String TAG = "MainActivity";
    private boolean mIsAdapterSetSelection = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Configure the spinner */
        Spinner spinner = (Spinner)findViewById(R.id.spinnerLanguage);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages_array, android.R.layout.simple_spinner_item);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /* Handle language spinner selections */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Log.v(TAG, "onItemSelected" + pos);

        /* Don't respond to adapter setting selections */
        if (mIsAdapterSetSelection) {
            mIsAdapterSetSelection = false;
            Log.v(TAG, "Adapter set selection!");
            return;
        }

        if (pos == 0) {
            /* English */
            Log.v(TAG, "English");
            refresh();
        }
        if (pos == 1) {
            /* Spanish */
            Log.v(TAG, "Spanish");
            refresh();
        }
    }

    private void refresh() {
        Intent refresh = getIntent();
        finish();
        startActivity(refresh);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.v(TAG, "onNothingSelected");
    }
}
