
package ca.nickpresta.android.myguelph;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MyGuelphPrefsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.prefs);
    }
}
