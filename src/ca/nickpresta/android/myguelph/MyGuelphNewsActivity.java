
package ca.nickpresta.android.myguelph;

import nl.matshofman.saxrssreader.RssItem;

import android.app.ListActivity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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

public class MyGuelphNewsActivity extends ListActivity {

    private MyGuelphNewsCustomAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        listView.setItemsCanFocus(true);

        ArrayList<RssItem> items = new ArrayList<RssItem>();
        mAdapter = new MyGuelphNewsCustomAdapter(this, R.layout.news_item, items);
        listView.setAdapter(mAdapter);

        MyGuelphNewsAsyncTask task = new MyGuelphNewsAsyncTask(this);
        try {
            task.execute(new URL[] {
                    new URL(getString(R.string.guelph_news_main_feed_url)),
                    new URL(getString(R.string.guelph_news_at_guelph_feed_url)),
                    new URL(getString(R.string.guelph_news_events_feed_url)),
                    new URL(getString(R.string.guelph_news_gryphon_main_feed_url))
            });
        } catch (MalformedURLException e) {
            Log.e("MyGuelph", "MalformedURL when parsing main feed string.");
            e.printStackTrace();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.news);
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
            startActivity(getIntent());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyGuelphNewsCustomAdapter extends ArrayAdapter<RssItem> {
        private final ArrayList<RssItem> items;

        public MyGuelphNewsCustomAdapter(Context context, int textViewResourceId,
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
                view = vi.inflate(R.layout.news_item, null);
            }

            RssItem i = items.get(position);
            if (i != null) {
                TextView title = (TextView) view.findViewById(R.id.title);
                title.setMovementMethod(LinkMovementMethod.getInstance());
                TextView date = (TextView) view.findViewById(R.id.date);
                if (title != null) {
                    String link = "<a href='" + i.getLink() + "'>" + i.getTitle() + "</a>";
                    title.setText(Html.fromHtml(link));
                }
                if (date != null) {
                    date.setText("Posted on " + i.getPubDate());
                }
            }

            return view;
        }
    }

    public void addItem(RssItem item) {
        mAdapter.add(item);
    }
}
