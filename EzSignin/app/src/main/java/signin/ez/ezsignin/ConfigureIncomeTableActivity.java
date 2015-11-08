package signin.ez.ezsignin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigureIncomeTableActivity extends Activity {

    private final static String TAG = "ConfigreIncomeTableActi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Read from preferences... */
        this.setContentView(R.layout.activity_configure_income_table);

        /* Manually and gruelingly load saved income table */
        this.loadIncomeTableFromPrefs();

    }

    private void loadIncomeTableFromPrefs() {
        EditText annualConfigHH1 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig1);
        EditText annualConfigHH2 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig2);
        EditText annualConfigHH3 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig3);
        EditText annualConfigHH4 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig4);
        EditText annualConfigHH5 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig5);
        EditText annualConfigHH6 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig6);
        EditText annualConfigHH7 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig7);
        EditText annualConfigHH8 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig8);
        EditText annualConfigHH9 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig9);
        EditText annualConfigHH10 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig10);

        EditText monthlyConfigHH1 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig1);
        EditText monthlyConfigHH2 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig2);
        EditText monthlyConfigHH3 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig3);
        EditText monthlyConfigHH4 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig4);
        EditText monthlyConfigHH5 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig5);
        EditText monthlyConfigHH6 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig6);
        EditText monthlyConfigHH7 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig7);
        EditText monthlyConfigHH8 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig8);
        EditText monthlyConfigHH9 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig9);
        EditText monthlyConfigHH10 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig10);

        EditText weekConfigHH1 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig1);
        EditText weekConfigHH2 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig2);
        EditText weekConfigHH3 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig3);
        EditText weekConfigHH4 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig4);
        EditText weekConfigHH5 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig5);
        EditText weekConfigHH6 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig6);
        EditText weekConfigHH7 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig7);
        EditText weekConfigHH8 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig8);
        EditText weekConfigHH9 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig9);
        EditText weekConfigHH10 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig10);

        EditText dayConfigHH1 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig1);
        EditText dayConfigHH2 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig2);
        EditText dayConfigHH3 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig3);
        EditText dayConfigHH4 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig4);
        EditText dayConfigHH5 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig5);
        EditText dayConfigHH6 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig6);
        EditText dayConfigHH7 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig7);
        EditText dayConfigHH8 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig8);
        EditText dayConfigHH9 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig9);
        EditText dayConfigHH10 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig10);

        String existingIncomeTableSerialized = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString(MainActivity.KEY_PREF_INCOME_TABLE, null);
        if (existingIncomeTableSerialized != null) {
            Log.v(TAG, "Attempting to use income table from prefs...");
            Gson gson = new Gson();
            HashMapWrapper wrapper = gson.fromJson(existingIncomeTableSerialized, HashMapWrapper.class);
            HashMap<Integer, List<Integer>> incomes = wrapper.hashMap;

            int numHouseholdSizes = incomes.size();
            for (int i = 0; i < numHouseholdSizes; i++) {
                List<Integer> incomeList = incomes.get(i+1);
                switch (i) {
                    case 0:
                        annualConfigHH1.setText(incomeList.get(0).toString());
                        monthlyConfigHH1.setText(incomeList.get(1).toString());
                        weekConfigHH1.setText(incomeList.get(2).toString());
                        dayConfigHH1.setText(incomeList.get(3).toString());
                        break;
                    case 1:
                        annualConfigHH2.setText(incomeList.get(0).toString());
                        monthlyConfigHH2.setText(incomeList.get(1).toString());
                        weekConfigHH2.setText(incomeList.get(2).toString());
                        dayConfigHH2.setText(incomeList.get(3).toString());
                        break;
                    case 2:
                        annualConfigHH3.setText(incomeList.get(0).toString());
                        monthlyConfigHH3.setText(incomeList.get(1).toString());
                        weekConfigHH3.setText(incomeList.get(2).toString());
                        dayConfigHH3.setText(incomeList.get(3).toString());
                        break;
                    case 3:
                        annualConfigHH4.setText(incomeList.get(0).toString());
                        monthlyConfigHH4.setText(incomeList.get(1).toString());
                        weekConfigHH4.setText(incomeList.get(2).toString());
                        dayConfigHH4.setText(incomeList.get(3).toString());
                        break;
                    case 4:
                        annualConfigHH5.setText(incomeList.get(0).toString());
                        monthlyConfigHH5.setText(incomeList.get(1).toString());
                        weekConfigHH5.setText(incomeList.get(2).toString());
                        dayConfigHH5.setText(incomeList.get(3).toString());
                        break;
                    case 5:
                        annualConfigHH6.setText(incomeList.get(0).toString());
                        monthlyConfigHH6.setText(incomeList.get(1).toString());
                        weekConfigHH6.setText(incomeList.get(2).toString());
                        dayConfigHH6.setText(incomeList.get(3).toString());
                        break;
                    case 6:
                        annualConfigHH7.setText(incomeList.get(0).toString());
                        monthlyConfigHH7.setText(incomeList.get(1).toString());
                        weekConfigHH7.setText(incomeList.get(2).toString());
                        dayConfigHH7.setText(incomeList.get(3).toString());
                        break;
                    case 7:
                        annualConfigHH8.setText(incomeList.get(0).toString());
                        monthlyConfigHH8.setText(incomeList.get(1).toString());
                        weekConfigHH8.setText(incomeList.get(2).toString());
                        dayConfigHH8.setText(incomeList.get(3).toString());
                        break;
                    case 8:
                        annualConfigHH9.setText(incomeList.get(0).toString());
                        monthlyConfigHH9.setText(incomeList.get(1).toString());
                        weekConfigHH9.setText(incomeList.get(2).toString());
                        dayConfigHH9.setText(incomeList.get(3).toString());
                        break;
                    case 9:
                        annualConfigHH10.setText(incomeList.get(0).toString());
                        monthlyConfigHH10.setText(incomeList.get(1).toString());
                        weekConfigHH10.setText(incomeList.get(2).toString());
                        dayConfigHH10.setText(incomeList.get(3).toString());
                        break;
                }
            }
        }
    }

    public void onIncomeTableSaveClick(View v) {
        HashMap<Integer, List<Integer>> incomeConfig = new HashMap<>();
        EditText annualConfigHH1 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig1);
        EditText annualConfigHH2 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig2);
        EditText annualConfigHH3 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig3);
        EditText annualConfigHH4 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig4);
        EditText annualConfigHH5 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig5);
        EditText annualConfigHH6 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig6);
        EditText annualConfigHH7 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig7);
        EditText annualConfigHH8 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig8);
        EditText annualConfigHH9 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig9);
        EditText annualConfigHH10 = (EditText)findViewById(R.id.incomeTableIncomeAnnualConfig10);

        EditText monthlyConfigHH1 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig1);
        EditText monthlyConfigHH2 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig2);
        EditText monthlyConfigHH3 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig3);
        EditText monthlyConfigHH4 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig4);
        EditText monthlyConfigHH5 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig5);
        EditText monthlyConfigHH6 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig6);
        EditText monthlyConfigHH7 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig7);
        EditText monthlyConfigHH8 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig8);
        EditText monthlyConfigHH9 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig9);
        EditText monthlyConfigHH10 = (EditText)findViewById(R.id.incomeTableIncomeMonthlyConfig10);

        EditText weekConfigHH1 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig1);
        EditText weekConfigHH2 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig2);
        EditText weekConfigHH3 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig3);
        EditText weekConfigHH4 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig4);
        EditText weekConfigHH5 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig5);
        EditText weekConfigHH6 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig6);
        EditText weekConfigHH7 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig7);
        EditText weekConfigHH8 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig8);
        EditText weekConfigHH9 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig9);
        EditText weekConfigHH10 = (EditText)findViewById(R.id.incomeTableIncomeWeekConfig10);

        EditText dayConfigHH1 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig1);
        EditText dayConfigHH2 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig2);
        EditText dayConfigHH3 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig3);
        EditText dayConfigHH4 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig4);
        EditText dayConfigHH5 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig5);
        EditText dayConfigHH6 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig6);
        EditText dayConfigHH7 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig7);
        EditText dayConfigHH8 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig8);
        EditText dayConfigHH9 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig9);
        EditText dayConfigHH10 = (EditText)findViewById(R.id.incomeTableIncomeDayConfig10);

        incomeConfig.put(1, Arrays.asList(Integer.parseInt(annualConfigHH1.getText().toString().replace(",", "")), Integer.parseInt(monthlyConfigHH1.getText().toString().replace(",", "")), Integer.parseInt(weekConfigHH1.getText().toString().replace(",", "")), Integer.parseInt(dayConfigHH1.getText().toString().replace(",", ""))));
        incomeConfig.put(2, Arrays.asList(Integer.parseInt(annualConfigHH2.getText().toString().replace(",", "")), Integer.parseInt(monthlyConfigHH2.getText().toString().replace(",", "")), Integer.parseInt(weekConfigHH2.getText().toString().replace(",", "")), Integer.parseInt(dayConfigHH2.getText().toString().replace(",", ""))));
        incomeConfig.put(3, Arrays.asList(Integer.parseInt(annualConfigHH3.getText().toString().replace(",", "")), Integer.parseInt(monthlyConfigHH3.getText().toString().replace(",", "")), Integer.parseInt(weekConfigHH3.getText().toString().replace(",", "")), Integer.parseInt(dayConfigHH3.getText().toString().replace(",", ""))));
        incomeConfig.put(4, Arrays.asList(Integer.parseInt(annualConfigHH4.getText().toString().replace(",", "")), Integer.parseInt(monthlyConfigHH4.getText().toString().replace(",", "")), Integer.parseInt(weekConfigHH4.getText().toString().replace(",", "")), Integer.parseInt(dayConfigHH4.getText().toString().replace(",", ""))));
        incomeConfig.put(5, Arrays.asList(Integer.parseInt(annualConfigHH5.getText().toString().replace(",", "")), Integer.parseInt(monthlyConfigHH5.getText().toString().replace(",", "")), Integer.parseInt(weekConfigHH5.getText().toString().replace(",", "")), Integer.parseInt(dayConfigHH5.getText().toString().replace(",", ""))));
        incomeConfig.put(6, Arrays.asList(Integer.parseInt(annualConfigHH6.getText().toString().replace(",", "")), Integer.parseInt(monthlyConfigHH6.getText().toString().replace(",", "")), Integer.parseInt(weekConfigHH6.getText().toString().replace(",", "")), Integer.parseInt(dayConfigHH6.getText().toString().replace(",", ""))));
        incomeConfig.put(7, Arrays.asList(Integer.parseInt(annualConfigHH7.getText().toString().replace(",", "")), Integer.parseInt(monthlyConfigHH7.getText().toString().replace(",", "")), Integer.parseInt(weekConfigHH7.getText().toString().replace(",", "")), Integer.parseInt(dayConfigHH7.getText().toString().replace(",", ""))));
        incomeConfig.put(8, Arrays.asList(Integer.parseInt(annualConfigHH8.getText().toString().replace(",", "")), Integer.parseInt(monthlyConfigHH8.getText().toString().replace(",", "")), Integer.parseInt(weekConfigHH8.getText().toString().replace(",", "")), Integer.parseInt(dayConfigHH8.getText().toString().replace(",", ""))));
        incomeConfig.put(9, Arrays.asList(Integer.parseInt(annualConfigHH9.getText().toString().replace(",", "")), Integer.parseInt(monthlyConfigHH9.getText().toString().replace(",", "")), Integer.parseInt(weekConfigHH9.getText().toString().replace(",", "")), Integer.parseInt(dayConfigHH9.getText().toString().replace(",", ""))));
        incomeConfig.put(10, Arrays.asList(Integer.parseInt(annualConfigHH10.getText().toString().replace(",", "")), Integer.parseInt(monthlyConfigHH10.getText().toString().replace(",", "")), Integer.parseInt(weekConfigHH10.getText().toString().replace(",", "")), Integer.parseInt(dayConfigHH10.getText().toString().replace(",", ""))));

        /* Wrap the table and serialize the wrapper in GSON */
        Gson gson = new Gson();
        HashMapWrapper wrapper = new HashMapWrapper();
        wrapper.hashMap = incomeConfig;
        String serializedMap = gson.toJson(wrapper);

        /* Write the new serialized income table to prefs */
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString(MainActivity.KEY_PREF_INCOME_TABLE, serializedMap).commit();

        /* Put the mainactiviy back on top */
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
