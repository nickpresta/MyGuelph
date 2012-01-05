
package ca.nickpresta.android.myguelph;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
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
    private Dialog mMainDialog;
    private Dialog mResumeDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        mMainDialog = null;
        if (!application.isGpsAvailable()) {
            mMainDialog = application.redirectToIntentOrHome(this,
                    getString(R.string.missing_gps_connection), new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        mMap = (MapView) this.findViewById(R.id.mapview);
        mMap.setBuiltInZoomControls(true);
        mMap.setSatellite(true);
        mMapController = mMap.getController();

        mMyLocationOverlay = new MyLocationOverlay(this, mMap);
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
        if (mMainDialog != null) {
            mMainDialog.dismiss();
        }
        if (mResumeDialog != null) {
            mResumeDialog.dismiss();
        }
        if (mMyLocationOverlay != null) {
            mMyLocationOverlay.disableMyLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        mResumeDialog = null;
        if (!application.isGpsAvailable()) {
            mResumeDialog = application.redirectToIntentOrHome(this,
                    getString(R.string.missing_gps_connection), new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }
        if (mMyLocationOverlay != null) {
            mMyLocationOverlay.enableMyLocation();
        }
    }

    public void performSearch(View view) {
        // do search
        ProgressDialog progressDialog = ProgressDialog.show(MyGuelphMapActivity.this, "Title",
                "Finding building. Please wait...", false);
        progressDialog.show();
        AutoCompleteTextView searchField = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        String searchTerm = searchField.getText().toString().trim();
        if (TextUtils.isEmpty(searchTerm)) {
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

    private void performClear() {
        for (Overlay overlay : mMap.getOverlays()) {
            if (overlay instanceof MyGuelphItemizedOverlay) {
                ((MyGuelphItemizedOverlay) overlay).clear();
            }
        }
        mMap.invalidate();
    }

    private void toggleView(MenuItem item) {
        if (item == null) {
            return;
        }

        boolean state = !item.isChecked();

        item.setChecked(state);
        mMap.setSatellite(state);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT < 11) {
            // On anything less than Honeycomb, menuItems don't show a checkbox.
            // So we have to change the title
            MenuItem item = menu.findItem(R.id.menuMapSatelliteView);
            if (item.isChecked()) {
                item.setTitle(getString(R.string.menu_map_map_view));
            } else {
                item.setTitle(getString(R.string.menu_map_satellite_view));
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyGuelphMenu.onOptionsItemSelected(item, this);
        int itemId = item.getItemId();
        if (itemId == R.id.menuMapClear) {
            performClear();
        } else if (itemId == R.id.menuMapSatelliteView) {
            toggleView(item);
        }
        return super.onOptionsItemSelected(item);
    }

}
