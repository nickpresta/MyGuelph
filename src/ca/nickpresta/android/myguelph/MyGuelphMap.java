package ca.nickpresta.android.myguelph;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import ca.nickpresta.android.myguelph.R;

public class MyGuelphMap extends MapActivity implements LocationListener {
	
	private static final int DIALOG_ABOUT = 2;
	private MapView map;
	private MapController mapController;
	private ArrayList<Building> buildingsList;
	private ProgressDialog progressDialog;
	
	private LocationManager locationManager;
	private Location presentLocation;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        startLocationPoll();
        
        map = (MapView)this.findViewById(R.id.mapview);
        mapController = map.getController();
        
        buildingsList = new ArrayList<Building>();
		
		String[] buildings = getResources().getStringArray(R.array.buildings_array);
		String[] building_codes = getResources().getStringArray(R.array.buildings_short_array);
		for (int i = 0; i < buildings.length; i++) {
			buildingsList.add(new Building(building_codes[i], buildings[i]));
		}

        ArrayAdapter<Building> adapter = new ArrayAdapter<Building>(this,
        		android.R.layout.simple_dropdown_item_1line, buildingsList);
        final AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        textView.setAdapter(adapter);
        textView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View view, int position,
					long id) {
				Building building = (Building) listView.getItemAtPosition(position);
				textView.setText(building.getBuildingName());
			}
        
        });

        // Hard coded the min/max locations of central campus.
        int maxLatitude = (int)(43.5354 * 1000000);
        int minLatitude = (int)(43.5268 * 1000000);
        int maxLongitude = (int)(-80.2305 * 1000000);
        int minLongitude = (int)(-80.2230 * 1000000);
                
        mapController.animateTo(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
        mapController.zoomToSpan(Math.abs(maxLatitude - minLatitude), Math.abs(maxLongitude - minLongitude));
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	locationManager.removeUpdates(this);	
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	locationManager.removeUpdates(this);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
    }
    
    @Override
    protected boolean isRouteDisplayed() {
    	return false;
    }
    
    public class Building {
    	private String buildingCode;
    	private String buildingName;
    	
    	public Building(String code, String name) {
    		buildingCode = code;
    		buildingName = name;
    	}
    	
    	public String getBuildingName() {
    		return buildingName;
    	}
    	
    	@Override
    	public String toString() {
    		String outString = "";
    		if (!buildingCode.isEmpty()) {
    			outString += buildingCode + " - ";
    		}
    		outString += buildingName;
    		return outString;
    	}	
    }
    
    public void performSearch(View view) {
    	// do search
    	progressDialog = ProgressDialog.show(MyGuelphMap.this, "",
    			"Finding building. Please wait...", true);
        progressDialog.dismiss();
    }

    private void startLocationPoll() {
    	locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        locationManager.getProvider(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);

        locationManager.getProviders(true);
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    }
    
	@Override
	public void onLocationChanged(Location location) {
		presentLocation = location;
		Toast.makeText(getApplicationContext(),
				"Current location is " + presentLocation.getLatitude() + ", " +
						presentLocation.getLongitude(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		
		return super.onCreateOptionsMenu(menu);	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case R.id.menuAbout:
				this.showDialog(DIALOG_ABOUT);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch(id) {
		case DIALOG_ABOUT:
			dialog = getInstanceAlertDialog();
			break;
		default:
			dialog = null;
			break;
		}
		return dialog;
	}

	private Dialog getInstanceAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.about_contents);
		AlertDialog alert = builder.create();
		alert.setTitle(R.string.about_title);
		alert.setButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				
			}
		});
		return alert;
	}
}