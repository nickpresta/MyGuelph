package ca.nickpresta.android.myguelph;

import ca.nickpresta.android.myguelph.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MyGuelphMain extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    }
   
    public void displayNews(View view) {
    	Toast.makeText(getApplicationContext(), "News!", Toast.LENGTH_SHORT).show();
    }
    
    public void displayMap(View view) {
    	Intent mapIntent = new Intent(view.getContext(), MyGuelphMap.class);
    	startActivityForResult(mapIntent, 0);
    }

    public void displayDirectory(View view) {
    	Toast.makeText(getApplicationContext(), "Directory!", Toast.LENGTH_SHORT).show();
    }
    
    public void displayLinks(View view) {
    	Toast.makeText(getApplicationContext(), "Links!", Toast.LENGTH_SHORT).show();
    }

}
