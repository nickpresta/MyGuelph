
package ca.nickpresta.android.myguelph;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MyGuelphBalanceActivity extends Activity {

    private Dialog mMainDialog;
    private Dialog mResumeDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance);

        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        mMainDialog = null;
        if (!application.isNetworkAvailable()) {
            mMainDialog = application.redirectToIntentOrHome(this,
                    getString(R.string.missing_network_connection), new Intent(
                            Settings.ACTION_SETTINGS));
            return;
        }

        if (!application.hasCredentials(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.missing_credentials))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.positive_setup),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent prefsIntent = new Intent(getApplicationContext(),
                                            MyGuelphPrefsActivity.class);
                                    startActivity(prefsIntent);
                                    finish();
                                }
                            })
                    .setNegativeButton(getString(R.string.negative_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    finish();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
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
    protected void onPause() {
        super.onPause();
        if (mMainDialog != null) {
            mMainDialog.dismiss();
        }
        if (mResumeDialog != null) {
            mResumeDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        mResumeDialog = null;
        if (!application.isNetworkAvailable()) {
            mResumeDialog = application.redirectToIntentOrHome(this,
                    getString(R.string.missing_network_connection),
                    new Intent(Settings.ACTION_SETTINGS));
        }
    }

    public void setBalance(String theBalance) {
        TextView balance = (TextView) findViewById(R.id.currentMealCardBalance);
        balance.setText(theBalance);
    }
}
