
package ca.nickpresta.android.myguelph;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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

    public void displayMap(View view) {
        Intent mapIntent = new Intent(view.getContext(), MyGuelphMapActivity.class);
        startActivity(mapIntent);
    }

    public void displayDirectory(View view) {
        Toast.makeText(getApplicationContext(), "Directory!", Toast.LENGTH_SHORT).show();
    }

    public void displayLinks(View view) {
        Toast.makeText(getApplicationContext(), "Links!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        MyGuelphMenu.onCreateOptionsMenu(menu, inflater);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyGuelphMenu.onOptionsItemSelected(item, this);
        int itemId = item.getItemId();
        if (itemId == R.id.menuPreferences) {
            Toast.makeText(getApplicationContext(), "Preferences!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
