
package ca.nickpresta.android.myguelph;

import nl.matshofman.saxrssreader.RssItem;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MyGuelphEventsActivity extends ListActivity {

    private MyGuelphEventsCustomAdapter mEventsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events);

        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        if (!application.isNetworkAvailable()) {
            application.redirectToIntentOrHome(this,
                    getString(R.string.missing_network_connection), new Intent(
                            Settings.ACTION_SETTINGS));
            return;
        }

        ListView listView = getListView();

        ArrayList<RssItem> events = new ArrayList<RssItem>();
        mEventsAdapter = new MyGuelphEventsCustomAdapter(this, R.layout.events_item, events);
        listView.setAdapter(mEventsAdapter);

        MyGuelphEventsAsyncTask task = new MyGuelphEventsAsyncTask(this);
        try {
            task.execute(new URL[] {
                    new URL(getString(R.string.guelph_events_main_feed_url))
            });
        } catch (MalformedURLException e) {
            Log.e("MyGuelphEventsActivity", "MalformedURL when parsing feed string.");
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.events);
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
    protected void onResume() {
        super.onResume();
        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        if (!application.isNetworkAvailable()) {
            application.redirectToIntentOrHome(this,
                    getString(R.string.missing_network_connection),
                    new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private class MyGuelphEventsCustomAdapter extends ArrayAdapter<RssItem> {
        private final ArrayList<RssItem> items;

        public MyGuelphEventsCustomAdapter(Context context, int textViewResourceId,
                ArrayList<RssItem> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.events_item, null);
            }

            RssItem i = items.get(position);
            if (i != null) {
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView date = (TextView) view.findViewById(R.id.date);
                if (title != null) {
                    title.setText(Html.fromHtml("<u>" + i.getTitle() + "</u>"));
                }
                if (date != null) {
                    date.setText("Posted on " + i.getPubDate());
                }
            }

            return view;
        }
    }

    public void addItem(RssItem item) {
        mEventsAdapter.add(item);
    }

    @Override
    protected void onListItemClick(ListView l, View view, int position, long id) {
        super.onListItemClick(l, view, position, id);
        RssItem selection = (RssItem) getListView().getItemAtPosition(position);
        Intent eventDetailsIntent = new Intent(view.getContext(),
                MyGuelphEventDetailsActivity.class);
        eventDetailsIntent.putExtra("eventTitle", selection.getTitle());
        eventDetailsIntent.putExtra("eventLink", selection.getLink());
        startActivity(eventDetailsIntent);
    }
}
