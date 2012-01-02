
package ca.nickpresta.android.myguelph;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MyGuelphBalanceActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance);

        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        if (!application.isNetworkAvailable()) {
            application.redirectToIntentOrHome(this,
                    getString(R.string.missing_network_connection), new Intent(
                            Settings.ACTION_SETTINGS));
            return;
        }

        MyGuelphBalanceAsyncTask task = new MyGuelphBalanceAsyncTask(this);
        task.execute(new String[] {
                getString(R.string.balance_login_url)
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refreshmenu, menu);
        MyGuelphMenu.onCreateOptionsMenu(menu, inflater);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyGuelphMenu.onOptionsItemSelected(item, this);
        int itemId = item.getItemId();
        if (itemId == R.id.menuRefresh) {
            startActivity(getIntent());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        if (!application.isNetworkAvailable()) {
            application.redirectToIntentOrHome(this,
                    getString(R.string.missing_network_connection),
                    new Intent(Settings.ACTION_SETTINGS));
        }
    }

    public void setBalance(String theBalance) {
        TextView balance = (TextView) findViewById(R.id.currentMealCardBalance);
        balance.setText(theBalance);
    }
}
