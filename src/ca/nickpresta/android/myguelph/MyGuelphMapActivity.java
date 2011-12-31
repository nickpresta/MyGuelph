
package ca.nickpresta.android.myguelph;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyGuelphMapActivity extends MapActivity implements LocationListener {

    private MapView mMap;
    private MapController mMapController;
    private ArrayList<MyGuelphBuilding> mBuildingsList;
    private ProgressDialog mProgressDialog;

    private LocationManager mLocationManager;
    private Location mPresentLocation;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        startLocationPoll();

        mMap = (MapView) this.findViewById(R.id.mapview);
        mMapController = mMap.getController();

        mBuildingsList = new ArrayList<MyGuelphBuilding>();

        String[] buildings = getResources().getStringArray(R.array.buildings_array);
        String[] building_codes = getResources().getStringArray(R.array.buildings_short_array);
        for (int i = 0; i < buildings.length; i++) {
            mBuildingsList.add(new MyGuelphBuilding(building_codes[i], buildings[i]));
        }

        ArrayAdapter<MyGuelphBuilding> adapter = new ArrayAdapter<MyGuelphBuilding>(this,
                android.R.layout.simple_dropdown_item_1line, mBuildingsList);
        final AutoCompleteTextView textView =
                (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        textView.setAdapter(adapter);
        textView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                MyGuelphBuilding building = (MyGuelphBuilding) listView.getItemAtPosition(position);
                textView.setText(building.getBuildingName());
            }

        });

        // Hard coded the min/max locations of central campus.
        int maxLatitude = (int) (43.5354 * 1000000);
        int minLatitude = (int) (43.5268 * 1000000);
        int maxLongitude = (int) (-80.2305 * 1000000);
        int minLongitude = (int) (-80.2230 * 1000000);

        mMapController.animateTo(new GeoPoint((maxLatitude + minLatitude) / 2,
                (maxLongitude + minLongitude) / 2));
        mMapController.zoomToSpan(Math.abs(maxLatitude - minLatitude),
                Math.abs(maxLongitude - minLongitude));
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
    }

    public void performSearch(View view) {
        // do search
        mProgressDialog = ProgressDialog.show(MyGuelphMapActivity.this, "",
                "Finding building. Please wait...", true);
        mProgressDialog.dismiss();
    }

    private void startLocationPoll() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);

        mLocationManager.getProviders(true);
        mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {
        mPresentLocation = location;
        Toast.makeText(getApplicationContext(),
                "Current location is " + mPresentLocation.getLatitude() + ", "
                        + mPresentLocation.getLongitude(), Toast.LENGTH_LONG).show();
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
        MyGuelphMenu.onCreateOptionsMenu(menu, getMenuInflater());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MyGuelphMenu.onOptionsItemSelected(item, this);
    }

}
