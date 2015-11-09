package signin.ez.ezsignin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public final static String KEY_INCOME_CONFIG = "KEY_INCOME_CONFIG";
    public final static String KEY_PREF_INCOME_TABLE = "KEY_PREF_INCOME_TABLE";
    public final static String KEY_RECORDS = "KEY_RECORDS";
    public static final String RECORDS_FILENAME = "RECORDS_FILE";

    private final static String TAG = "MainActivity";
    private List<Record> mRecords = new ArrayList<Record>();
    private final int NUM_RATES = 4;
    private boolean mIsAdapterSetSelectionLanguage = true;
    private final String KEY_LANG = "KEY_LANG";
    private final String VALUE_LANG_SPANISH = "VALUE_LANG_SPANISH";
    private final String VALUE_LANG_ENGLISH = "VALUE_LANG_ENGLISH";
    private String mEmail = "";

    /* Income table default configuration [# in household][rate] */
    private HashMap<Integer, List<Integer>> mIncomes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /* Retreive the cached email */
        mEmail = this.getPreferenceByKey(SettingsActivity.KEY_PREF_EMAIL);

        String incomeTable = this.getPreferenceByKey(MainActivity.KEY_PREF_INCOME_TABLE);
        if (incomeTable != null) {
            Log.v(TAG, "Attempting to use new income table!");
            Gson gson = new Gson();
            HashMapWrapper wrapper = gson.fromJson(incomeTable, HashMapWrapper.class);
            mIncomes = wrapper.hashMap;
            if (mIncomes == null) {
                throw new IllegalStateException("Income map not retreived from GSON object!");
            }
        } else {
            Log.v(TAG, "Using default incomes!");
            mIncomes = new HashMap<>();
            mIncomes.put(1, Arrays.asList(20000, 2000, 350, 75));
            mIncomes.put(2, Arrays.asList(30000, 3000, 450, 95));
            mIncomes.put(3, Arrays.asList(40000, 4000, 550, 105));
            mIncomes.put(4, Arrays.asList(50000, 5000, 650, 115));
            mIncomes.put(5, Arrays.asList(60000, 6000, 750, 125));
            mIncomes.put(6, Arrays.asList(70000, 7000, 850, 135));
            mIncomes.put(7, Arrays.asList(80000, 8000, 950, 145));
            mIncomes.put(8, Arrays.asList(90000, 9000, 1050, 155));
            mIncomes.put(9, Arrays.asList(100000, 10000, 1150, 165));
            mIncomes.put(10, Arrays.asList(110000, 11000, 1250, 175));
        }

        /* Configure the spinnerLanguage */
        Spinner spinnerLanguage = (Spinner) findViewById(R.id.spinnerLanguage);
        Spinner spinnerHousehold = (Spinner) findViewById(R.id.spinnerNumHousehold);

        this.configureLanguageSpinner(spinnerLanguage);
        this.configureHouseholdSpinner(spinnerHousehold);

        /* Read records from file since we might be onCreating again */
        mRecords = MainActivity.readRecords(getBaseContext());
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
            Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivityIntent);
            return true;
        }
        if (id == R.id.menu_records) {
            Intent recordsActivityIntent = new Intent(this, RecordsActivity.class);
            //Bundle recordsBundle = new Bundle();
            /* Serialize the records list... */
            //Gson gson = new Gson();
            //RecordListWrapper wrapper = new RecordListWrapper();
            //wrapper.recordList = mRecords;
            //String recordsListString = gson.toJson(wrapper);
            //recordsActivityIntent.putExtra(MainActivity.KEY_RECORDS, recordsListString);
            startActivity(recordsActivityIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle spinner changes
     *
     * @param parent
     * @param view
     * @param pos
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Log.v(TAG, "onItemSelected" + pos);

        Spinner spinner = (Spinner) parent;
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
                this.updateIncomeTable(pos + 1);
                break;
        }
    }

    /**
     * Handle nothing selected
     *
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.v(TAG, "onNothingSelected");
    }

    /* Custom methods */

    /**
     * Set the locale to language
     *
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
     *
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
     *
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

    /**
     * Configure the household spinner.
     *
     * @param spinnerHousehold the household spinner
     */
    private void configureHouseholdSpinner(Spinner spinnerHousehold) {
        String[] array = getResources().getStringArray(R.array.household_array);
        Integer[] householdInts = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            householdInts[i] = Integer.parseInt(array[i]);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, householdInts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (adapter == null) {
            Log.v(TAG, "Adapter is null!");
            return;
        }
        spinnerHousehold.setAdapter(adapter);
        spinnerHousehold.setOnItemSelectedListener(this);
    }

    /**
     * Update the table for minimum incomes.
     *
     * @param numInHousehold the number of people in the household.
     */
    private void updateIncomeTable(int numInHousehold) {
        TextView incomeAnnual = (TextView) findViewById(R.id.incomeTableIncomeAnnual);
        TextView incomeMonthly = (TextView) findViewById(R.id.incomeTableIncomeMonthly);
        TextView incomeWeek = (TextView) findViewById(R.id.incomeTableIncomeWeek);
        TextView incomeDay = (TextView) findViewById(R.id.incomeTableIncomeDay);

        List<Integer> incomeList = mIncomes.get(numInHousehold);
        if (incomeList == null && incomeList.size() < NUM_RATES) {
            Log.v(TAG, "Unable to get income list for household size " + numInHousehold);
            return;
        }

        Format formatter = NumberFormat.getCurrencyInstance();

        incomeAnnual.setText(formatter.format(incomeList.get(0)));
        incomeMonthly.setText(formatter.format(incomeList.get(1)));
        incomeWeek.setText(formatter.format(incomeList.get(2)));
        incomeDay.setText(formatter.format(incomeList.get(3)));
    }

    /**
     * Handles save button press.
     *
     * @param view
     */
    public void onSaveClick(View view) {
        Log.v(TAG, "Saving record...");
        Record newRecord = new Record();

        EditText editTextName = (EditText) findViewById(R.id.editTextName);
        EditText editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        EditText editTextCounty = (EditText) findViewById(R.id.editTextCounty);
        Spinner spinnerNumInHousehold = (Spinner) findViewById(R.id.spinnerNumHousehold);
        CheckBox checkBoxEligibleFS = (CheckBox) findViewById(R.id.checkBoxFoodstamps);
        CheckBox checkBoxEligibleIE = (CheckBox) findViewById(R.id.checkBoxIncomeEligibility);
        CheckBox checkBoxEligibleMC = (CheckBox) findViewById(R.id.checkBoxMedicaid);
        CheckBox checkBoxEligibleSSI = (CheckBox) findViewById(R.id.checkBoxSsi);
        CheckBox checkBoxEligibleTANF = (CheckBox) findViewById(R.id.checkBoxTanf);

        newRecord.setName(editTextName.getText().toString());
        newRecord.setAddress(editTextAddress.getText().toString());
        newRecord.setCounty(editTextCounty.getText().toString());
        newRecord.setNumInHousehold(spinnerNumInHousehold.getSelectedItemPosition() + 1); /* +1 since start at 0 */
        newRecord.setIsEligibleFS(checkBoxEligibleFS.isChecked());
        newRecord.setIsEligibleIE(checkBoxEligibleIE.isChecked());
        newRecord.setIsEligibleMC(checkBoxEligibleMC.isChecked());
        newRecord.setIsEligibleSS(checkBoxEligibleSSI.isChecked());
        newRecord.setIsEligibleTANF(checkBoxEligibleTANF.isChecked());

        mRecords.add(newRecord);

        /* Write all records to disk */
        MainActivity.writeRecord(getApplicationContext(), mRecords);


        // TODO nice confirmation screen please...

        /* Reset the UI for next person... */
        this.clearSigninPrompts();
    }

    /**
     * Write records to disk
     *
     * @param context context from which filedir is obtained
     * @param records the list of records to write
     */
    public static void writeRecord(Context context, List<Record> records) {
        ObjectOutputStream outputStream;
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(RECORDS_FILENAME, Context.MODE_PRIVATE);
            outputStream = new ObjectOutputStream(fos);
            outputStream.writeObject(records);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Record> readRecords(Context context) {
        ArrayList<Record> records = null;
        FileInputStream fin;
        ObjectInputStream ois = null;
        try {
            fin = context.openFileInput(RECORDS_FILENAME);
            ois = new ObjectInputStream(fin);
            records = (ArrayList<Record>) ois.readObject();
            ois.close();
            Log.v(TAG, "Records read successfully");
        } catch (Exception e) {
            Log.e(TAG, "Cant read saved records" + e.getMessage());
        } finally {
            if (ois != null)
                try {
                    ois.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error in closing stream while reading records" + e.getMessage());
                }
        }

        return records;
    }

    private void clearSigninPrompts() {
        EditText editTextName = (EditText) findViewById(R.id.editTextName);
        EditText editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        EditText editTextCounty = (EditText) findViewById(R.id.editTextCounty);
        Spinner spinnerNumInHousehold = (Spinner) findViewById(R.id.spinnerNumHousehold);
        CheckBox checkBoxEligibleFS = (CheckBox) findViewById(R.id.checkBoxFoodstamps);
        CheckBox checkBoxEligibleIE = (CheckBox) findViewById(R.id.checkBoxIncomeEligibility);
        CheckBox checkBoxEligibleMC = (CheckBox) findViewById(R.id.checkBoxMedicaid);
        CheckBox checkBoxEligibleSSI = (CheckBox) findViewById(R.id.checkBoxSsi);
        CheckBox checkBoxEligibleTANF = (CheckBox) findViewById(R.id.checkBoxTanf);

        editTextName.setText("");
        editTextAddress.setText("");
        editTextCounty.setText("");
        spinnerNumInHousehold.setSelection(0);
        checkBoxEligibleFS.setChecked(false);
        checkBoxEligibleIE.setChecked(false);
        checkBoxEligibleMC.setChecked(false);
        checkBoxEligibleSSI.setChecked(false);
        checkBoxEligibleTANF.setChecked(false);
    }

    /**
     * Get the preferences from settings activity
     */
    private String getPreferenceByKey(String key) {
        String ret = null;
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //sharedPref.edit().clear().commit(); // to fix corrupt serialization problem..
        try {
            ret = sharedPref.getString(key, "");
        } catch (NumberFormatException nfe) {
            Log.v(TAG, nfe.getMessage());
        }

        /* Don't return a 0 length value... */
        if (ret.length() == 0) {
            ret = null;
        }
        return ret;
    }
}
