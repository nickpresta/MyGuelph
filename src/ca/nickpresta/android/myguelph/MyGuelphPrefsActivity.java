
package ca.nickpresta.android.myguelph;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

public class MyGuelphPrefsActivity extends PreferenceActivity {

    private EditTextPreference mLoginStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.prefs);

        mLoginStatus = (EditTextPreference) getPreferenceScreen().findPreference(
                "prefs_login_status");
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyGuelphApplication application = (MyGuelphApplication) this.getApplication();

        if (application.getLoggedIn()) {
            mLoginStatus.setSummary("You are currently logged in.");
        } else {
            mLoginStatus.setSummary("You are currently logged out.");
        }
    }
}
