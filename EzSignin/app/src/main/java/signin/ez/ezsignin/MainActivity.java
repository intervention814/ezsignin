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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    public static final String RECORDS_FILENAME = "RECORDS_FILE";


    private final static String TAG = "MainActivity";
    private boolean isModifyingRecord = false;
    private List<Record> mRecords = new ArrayList<Record>();
    private final int NUM_RATES = 4;
    private boolean mIsAdapterSetSelectionLanguage = true;
    private final static String VALUE_LANG_SPANISH = "VALUE_LANG_SPANISH";
    private final static String VALUE_LANG_ENGLISH = "VALUE_LANG_ENGLISH";

    private static String CURRENT_LANGUAGE = VALUE_LANG_ENGLISH;

    /* Income table default configuration [# in household][rate] */
    private HashMap<Integer, List<Integer>> mIncomes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.initIncomeTable();

        /* Configure the spinnerLanguage */
        this.configureLanguageSpinner((Spinner) findViewById(R.id.spinnerLanguage));
        this.configureHouseholdSpinner((Spinner) findViewById(R.id.spinnerNumHousehold));

        /* See if we're restoring a record */
        if (savedInstanceState != null) {
            Record r = (Record)savedInstanceState.get(SettingsActivity.KEY_RECORD);
            if (r != null) {
                Log.v(TAG, "Restoring a record from saved instance state.");
                this.populateFieldsWithRecord(r);
            }
        }

        /* Read records from file since we might be onCreating again */
        mRecords = MainActivity.readRecords(getBaseContext());

        /* Determine if we'e been called on to modify an existing record */
        this.checkForModifyLaunch();
    }

    private static int SPINNER_VALUE = -1;
    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        this.initIncomeTable();
        Spinner spinner = (Spinner)findViewById(R.id.spinnerNumHousehold);
        configureHouseholdSpinner(spinner);
        if (SPINNER_VALUE >= 0) {
            Log.v(TAG, "Restoring " + SPINNER_VALUE);
            spinner.setSelection(SPINNER_VALUE);
        }
        this.checkForModifyLaunch();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.v(TAG, "onPause");
        Spinner spinner = (Spinner)findViewById(R.id.spinnerNumHousehold);
        SPINNER_VALUE = spinner.getSelectedItemPosition();
        Log.v(TAG, "Saving " + SPINNER_VALUE);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        Record currentRecord = new Record();
        this.writeFieldsToRecord(currentRecord);
        b.putSerializable(SettingsActivity.KEY_RECORD, currentRecord);
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
            recordsActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(recordsActivityIntent);

            /* Don't want this to stay on stack when going to menu if we were modding */
            if (isModifyingRecord) {
                finish();
            }
        }
        if (id == R.id.menu_summary) {
            Intent summaryActivityIntent = new Intent(this, SummaryActivity.class);
            summaryActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(summaryActivityIntent);
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
        CURRENT_LANGUAGE = language;
        Intent refresh = getIntent();
        Record.recordId--; /* TODO this is awful, needed now to avoid id mismatch on refresh */
        Record record = new Record();
        this.writeFieldsToRecord(record);
        refresh.putExtra(SettingsActivity.KEY_RECORD, record);
        if (isModifyingRecord) {
            refresh.putExtra(SettingsActivity.KEY_MODIFY, true);
        }
        finish();
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
        if (CURRENT_LANGUAGE.compareTo(VALUE_LANG_ENGLISH) == 0) {
            spinnerLanguage.setSelection(0);
        }
        if (CURRENT_LANGUAGE.compareTo(VALUE_LANG_SPANISH) == 0) {
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
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, R.layout.household_spinner, householdInts);
        adapter.setDropDownViewResource(R.layout.house_spinner_dropdown);
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

        final Button b = (Button)findViewById(R.id.saveRecordButton);
        b.setText("Saving...");
        b.setEnabled(false);

        Record newRecord = new Record();
        this.writeFieldsToRecord(newRecord);
        mRecords.add(newRecord);

        /* Write all records to disk */
        MainActivity.writeRecord(getApplicationContext(), mRecords);
        b.setText("Save");
        b.setEnabled(true);

        Toast.makeText(getBaseContext(), "Saved record!", Toast.LENGTH_SHORT).show();

        /* Reset the UI for next person... */
        this.clearSigninPrompts();
    }

    /**
     * Write fields to record
     * @param recordToWriteTo the record to write to
     */
    private void writeFieldsToRecord(Record recordToWriteTo) {
        EditText editTextName = (EditText) findViewById(R.id.editTextName);
        EditText editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        EditText editTextCounty = (EditText) findViewById(R.id.editTextCounty);
        Spinner spinnerNumInHousehold = (Spinner) findViewById(R.id.spinnerNumHousehold);
        CheckBox checkBoxEligibleFS = (CheckBox) findViewById(R.id.checkBoxFoodstamps);
        CheckBox checkBoxEligibleIE = (CheckBox) findViewById(R.id.checkBoxIncomeEligibility);
        CheckBox checkBoxEligibleMC = (CheckBox) findViewById(R.id.checkBoxMedicaid);
        CheckBox checkBoxEligibleSSI = (CheckBox) findViewById(R.id.checkBoxSsi);
        CheckBox checkBoxEligibleTANF = (CheckBox) findViewById(R.id.checkBoxTanf);

        DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy hh:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        String s = dateFormatter.format(today);

        recordToWriteTo.setName(editTextName.getText().toString());
        recordToWriteTo.setAddress(editTextAddress.getText().toString());
        recordToWriteTo.setCounty(editTextCounty.getText().toString());
        recordToWriteTo.setNumInHousehold(spinnerNumInHousehold.getSelectedItemPosition() + 1); /* +1 since start at 0 */
        recordToWriteTo.setIsEligibleFS(checkBoxEligibleFS.isChecked());
        recordToWriteTo.setIsEligibleIE(checkBoxEligibleIE.isChecked());
        recordToWriteTo.setIsEligibleMC(checkBoxEligibleMC.isChecked());
        recordToWriteTo.setIsEligibleSS(checkBoxEligibleSSI.isChecked());
        recordToWriteTo.setIsEligibleTANF(checkBoxEligibleTANF.isChecked());
        recordToWriteTo.setDate(s);
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

    /**
     * Reads records from the serialized records file
     *
     * @param context the context from which to read
     * @return the list of records from the file.
     */
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

        /* Set record static ID */
        int curId = 0;
        for (Record r:records) {
            curId = r.getRecordId();
        }

        Record.recordId = curId + 1;

        return records;
    }

    /**
     * Zero out the prompts, after a save
     */
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

    /**
     * Populate the fields with this record.
     * @param record the record from which fields will be populated
     */
    private void populateFieldsWithRecord(Record record) {
        Log.v(TAG, "Populating field with existing record.");
        EditText editTextName = (EditText) findViewById(R.id.editTextName);
        EditText editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        EditText editTextCounty = (EditText) findViewById(R.id.editTextCounty);
        Spinner spinnerNumInHousehold = (Spinner) findViewById(R.id.spinnerNumHousehold);
        CheckBox checkBoxEligibleFS = (CheckBox) findViewById(R.id.checkBoxFoodstamps);
        CheckBox checkBoxEligibleIE = (CheckBox) findViewById(R.id.checkBoxIncomeEligibility);
        CheckBox checkBoxEligibleMC = (CheckBox) findViewById(R.id.checkBoxMedicaid);
        CheckBox checkBoxEligibleSSI = (CheckBox) findViewById(R.id.checkBoxSsi);
        CheckBox checkBoxEligibleTANF = (CheckBox) findViewById(R.id.checkBoxTanf);

        editTextName.setText(record.getName());
        editTextAddress.setText(record.getAddress());
        editTextCounty.setText(record.getCounty());
        spinnerNumInHousehold.setSelection(record.getNumInHousehold() - 1);
        SPINNER_VALUE = record.getNumInHousehold() - 1;
        Log.v(TAG, "Saving from record in spinner: " + SPINNER_VALUE);
        checkBoxEligibleFS.setChecked(record.isEligibleFS());
        checkBoxEligibleIE.setChecked(record.isEligibleIE());
        checkBoxEligibleMC.setChecked(record.isEligibleMC());
        checkBoxEligibleSSI.setChecked(record.isEligibleSS());
        checkBoxEligibleTANF.setChecked(record.isEligibleTANF());
    }

    /**
     * Check to see if this intent was started to modify instead of save a new record.
     */
    private void checkForModifyLaunch() {
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null && b.getBoolean(SettingsActivity.KEY_MODIFY)) {
            Log.v(TAG, "Modifying!");
            isModifyingRecord = true;
            final Record recordToModify = (Record)b.get(SettingsActivity.KEY_RECORD);
            this.populateFieldsWithRecord(recordToModify);
            Button saveButton = (Button)findViewById(R.id.saveRecordButton);
            saveButton.setText("Modify Record: " + recordToModify.getRecordId());
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Modify onClick");
                    for (Record r : mRecords) {
                        if (r.getRecordId() == recordToModify.getRecordId()) {
                            Log.v(TAG, "Record found!");
                            Log.v(TAG, "Modifying record " + r.getRecordId());
                            writeFieldsToRecord(recordToModify);
                            mRecords.set(mRecords.indexOf(r), recordToModify);
                            MainActivity.writeRecord(getBaseContext(), mRecords);
                            Toast.makeText(getBaseContext(), "Record modified.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
            });
        } else if (b != null && b.get(SettingsActivity.KEY_RECORD) != null) {
            /* If we get here without modify, then we're just restoring a record, maybe from a language change */
            Record restorationRecord = (Record)b.get(SettingsActivity.KEY_RECORD);
            this.populateFieldsWithRecord(restorationRecord);
        } else {
            /* Not modifying AND not saved record, so reset everything */
            this.clearSigninPrompts();
        }
    }

    /**
     * Initialize the income table from preferences or from hardcoded defaults
     */
    private void initIncomeTable() {
        String incomeTable = this.getPreferenceByKey(SettingsActivity.KEY_PREF_INCOME_TABLE);
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
    }
}
