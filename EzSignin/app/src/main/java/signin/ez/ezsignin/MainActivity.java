package signin.ez.ezsignin;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
    private boolean mIsAdapterSetSelectionLanguage = true;
    private final String KEY_LANG = "KEY_LANG";
    private final String VALUE_LANG_SPANISH = "VALUE_LANG_SPANISH";
    private final String VALUE_LANG_ENGLISH = "VALUE_LANG_ENGLISH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Configure the spinnerLanguage */
        Spinner spinnerLanguage = (Spinner)findViewById(R.id.spinnerLanguage);
        Spinner spinnerHousehold = (Spinner)findViewById(R.id.spinnerNumHousehold);

        this.configureLanguageSpinner(spinnerLanguage);
        this.configureHouseholdSpinner(spinnerHousehold);
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

        Spinner spinner = (Spinner)parent;
        switch (spinner.getId()) {
            case R.id.spinnerLanguage:
                /* Don't respond to adapter setting selections */
                if (mIsAdapterSetSelectionLanguage) {
                    mIsAdapterSetSelectionLanguage = false;
                    Log.v(TAG, "Adapter set selection!");
                    return;
                }
                if (pos == 0) {
                /* English */
                    Log.v(TAG, "English");
                    this.setLanguage("en");
                    refresh(VALUE_LANG_ENGLISH);
                }
                if (pos == 1) {
                /* Spanish */
                    Log.v(TAG, "Spanish");
                    this.setLanguage("es");
                    refresh(VALUE_LANG_SPANISH);
                }
                break;
            case R.id.spinnerNumHousehold:
                Log.v(TAG, "Household: " + (pos + 1));
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.v(TAG, "onNothingSelected");
    }

    /* Custom methods */

    /**
     * Set the locale to language
     * @param language the language code to set
     */
    private void setLanguage(String language) {
        Locale myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    /**
     * Refresh this activity
     * @param language the language that is should start in
     */
    private void refresh(String language) {
        Intent refresh = getIntent();
        finish();
        refresh.putExtra(KEY_LANG, language);
        startActivity(refresh);
    }

    /**
     * Configure the language spinner
     * @param spinnerLanguage the language spinner
     */
    private void configureLanguageSpinner(Spinner spinnerLanguage) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerLanguage.setAdapter(adapter);

        /* Handle language change */
        Intent intent = getIntent();
        String languange = intent.getStringExtra(KEY_LANG);
        if (languange != null && languange.compareTo(VALUE_LANG_ENGLISH) == 0) {
            spinnerLanguage.setSelection(0);
        }
        if (languange != null && languange.compareTo(VALUE_LANG_SPANISH) == 0) {
            spinnerLanguage.setSelection(1);
        }
        spinnerLanguage.setOnItemSelectedListener(this);
    }

    private void configureHouseholdSpinner(Spinner spinnerHousehold) {
        String[] array = getResources().getStringArray(R.array.household_array);
        Integer[] householdInts = new Integer[array.length];
        for(int i = 0; i < array.length; i++) {
            householdInts[i] = Integer.parseInt(array[i]);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, householdInts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (adapter == null) {
            Log.v(TAG, "Adapter is null!");
            return;
        }
        spinnerHousehold.setAdapter(adapter);
        spinnerHousehold.setOnItemSelectedListener(this);
    }
}
