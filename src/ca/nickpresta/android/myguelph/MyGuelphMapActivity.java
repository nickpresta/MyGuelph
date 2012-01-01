
package ca.nickpresta.android.myguelph;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyGuelphMapActivity extends MapActivity {

    private MapView mMap;
    private MapController mMapController;
    private MyLocationOverlay mMyLocationOverlay;
    private ArrayList<MyGuelphBuilding> mBuildingsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        if (!application.isGpsAvailable()) {
            application.redirectToIntentOrHome(this,
                    getString(R.string.missing_gps_connection), new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        mMap = (MapView) this.findViewById(R.id.mapview);
        mMap.setBuiltInZoomControls(true);
        mMapController = mMap.getController();

        initMap();

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
        textView.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v);
                    return true;
                }
                return false;
            }
        });
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

    private void initMap() {
        mMyLocationOverlay = new MyLocationOverlay(this, mMap);
        mMap.getOverlays().add(mMyLocationOverlay);
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.runOnFirstFix(new Runnable() {

            @Override
            public void run() {
                mMapController.animateTo(mMyLocationOverlay.getMyLocation());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMyLocationOverlay.disableMyLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        if (!application.isGpsAvailable()) {
            application.redirectToIntentOrHome(this,
                    getString(R.string.missing_gps_connection), new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }
        mMyLocationOverlay.enableMyLocation();
    }

    public void performSearch(View view) {
        // do search
        ProgressDialog progressDialog = ProgressDialog.show(MyGuelphMapActivity.this, "Title",
                "Finding building. Please wait...", false);
        progressDialog.show();
        AutoCompleteTextView searchField = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        String searchTerm = searchField.getText().toString().trim();
        if (searchTerm.isEmpty()) {
            searchTerm = "University Centre";
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(
                    searchTerm + ", Guelph, Ontario, Canada", 5);
            if (addresses.size() > 0) {
                GeoPoint point = new GeoPoint(
                        (int) (addresses.get(0).getLatitude() * 1E6),
                        (int) (addresses.get(0).getLongitude() * 1E6));
                MyGuelphItemizedOverlay overlay = new MyGuelphItemizedOverlay(getResources()
                        .getDrawable(android.R.drawable.star_on), this);
                OverlayItem overlayItem = new OverlayItem(point, searchTerm, "");
                overlay.addOverlay(overlayItem);
                mMap.getOverlays().add(overlay);

                int maxLatitude = Math.max(point.getLatitudeE6(), mMyLocationOverlay
                        .getMyLocation().getLatitudeE6());
                int minLatitude = Math.min(point.getLatitudeE6(), mMyLocationOverlay
                        .getMyLocation().getLatitudeE6());
                int maxLongitude = Math.max(point.getLongitudeE6(), mMyLocationOverlay
                        .getMyLocation().getLongitudeE6());
                int minLongitude = Math.min(point.getLongitudeE6(), mMyLocationOverlay
                        .getMyLocation().getLongitudeE6());

                mMapController.animateTo(new GeoPoint(
                        (maxLatitude + minLatitude) / 2,
                        (maxLongitude + minLongitude) / 2));
                mMapController.zoomToSpan(Math.abs(maxLatitude - minLatitude),
                        Math.abs(maxLongitude - minLongitude));

                mMap.invalidate();
            } else {
                Toast.makeText(this, "Could not find " + searchTerm + " in Guelph.",
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();
    }

    public void performClear() {
        for (Overlay overlay : mMap.getOverlays()) {
            if (overlay instanceof MyGuelphItemizedOverlay) {
                ((MyGuelphItemizedOverlay) overlay).clear();
            }
        }
        mMap.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapmenu, menu);
        MyGuelphMenu.onCreateOptionsMenu(menu, inflater);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyGuelphMenu.onOptionsItemSelected(item, this);
        int itemId = item.getItemId();
        if (itemId == R.id.menuMapClear) {
            performClear();
        }
        return super.onOptionsItemSelected(item);
    }

}
