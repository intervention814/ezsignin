package signin.ez.ezsignin;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;



public class SettingsActivity extends PreferenceActivity {

    public final static String KEY_PREF_INCOME_TABLE = "KEY_PREF_INCOME_TABLE";
    public final static String KEY_MODIFY = "KEY_MODIFY";
    public final static String KEY_RECORD = "KEY_RECORD";
    public final static String KEY_PREF_EMAIL = "pref_email";

    public static class SettingsFragmentImpl extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.preferences);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragmentImpl())
                .commit();
    }
}