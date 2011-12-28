
package ca.nickpresta.android.myguelph;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MyGuelphMainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
        Toast.makeText(getApplicationContext(), "Links!", Toast.LENGTH_SHORT).show();
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
