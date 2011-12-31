
package ca.nickpresta.android.myguelph;

import com.bugsense.trace.BugSenseHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MyGuelphMainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        BugSenseHandler.setup(this, "19b97c33");
    }

    public void displayNews(View view) {
        Intent newsIntent = new Intent(view.getContext(), MyGuelphNewsActivity.class);
        startActivity(newsIntent);
    }

    public void displayEvents(View view) {
        Intent eventsIntent = new Intent(view.getContext(), MyGuelphEventsActivity.class);
        startActivity(eventsIntent);
    }

    public void displayMap(View view) {
        Intent mapIntent = new Intent(view.getContext(), MyGuelphMapActivity.class);
        startActivity(mapIntent);
    }

    public void displayLinks(View view) {
        Intent infoIntent = new Intent(view.getContext(), MyGuelphInfoActivity.class);
        startActivity(infoIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyGuelphMenu.onCreateOptionsMenu(menu, getMenuInflater());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyGuelphMenu.onOptionsItemSelected(item, this);
        return super.onOptionsItemSelected(item);
    }
}
