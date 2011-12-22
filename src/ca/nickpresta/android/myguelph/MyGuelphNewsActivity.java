
package ca.nickpresta.android.myguelph;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class MyGuelphNewsActivity extends Activity {

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);

        mProgressDialog = ProgressDialog.show(this, "Please wait...", "Fetching news", true,
                false);

        MyGuelphNewsAsyncTask task = new MyGuelphNewsAsyncTask(this);
        try {
            task.execute(new URL[] {
                    new URL(getString(R.string.guelph_news_main_feed_url)),
                    new URL(getString(R.string.guelph_news_at_guelph_feed_url)),
                    new URL(getString(R.string.guelph_news_events_feed_url))
            });
        } catch (MalformedURLException e) {
            Log.e("MyGuelph", "MalformedURL when parsing main feed string.");
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "After Async!", Toast.LENGTH_SHORT).show();
    }

    public void cancleDialog() {
        mProgressDialog.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newsmenu, menu);
        MyGuelphMenu.onCreateOptionsMenu(menu, inflater);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyGuelphMenu.onOptionsItemSelected(item, this);
        int itemId = item.getItemId();
        if (itemId == R.id.menuRefresh) {
            Toast.makeText(getApplicationContext(), "Refresh!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
