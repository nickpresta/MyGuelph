
package ca.nickpresta.android.myguelph;

import ca.nickpresta.android.myguelph.MyGuelphRssFeed.FeedType;
import nl.matshofman.saxrssreader.RssItem;

import android.app.TabActivity;
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
import android.widget.TabHost;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MyGuelphNewsActivity extends TabActivity {

    private MyGuelphNewsCustomAdapter mMainNewsAdapter;
    private MyGuelphNewsCustomAdapter mAtGuelphNewsAdapter;
    private MyGuelphNewsCustomAdapter mGryphonsNewsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);

        // Main news
        ListView mainListView = (ListView) findViewById(R.id.main_list);
        mainListView.setTextFilterEnabled(true);
        mainListView.setItemsCanFocus(true);

        ArrayList<RssItem> mainItems = new ArrayList<RssItem>();
        mMainNewsAdapter = new MyGuelphNewsCustomAdapter(this, R.layout.news_item, mainItems);
        mainListView.setAdapter(mMainNewsAdapter);

        // At Guelph
        ListView atGuelphListView = (ListView) findViewById(R.id.at_guelph_list);
        atGuelphListView.setTextFilterEnabled(true);
        atGuelphListView.setItemsCanFocus(true);

        ArrayList<RssItem> atGuelphItems = new ArrayList<RssItem>();
        mAtGuelphNewsAdapter = new MyGuelphNewsCustomAdapter(this, R.layout.news_item,
                atGuelphItems);
        atGuelphListView.setAdapter(mAtGuelphNewsAdapter);

        // Gryphons
        ListView gryphonsListView = (ListView) findViewById(R.id.gryphons_list);
        gryphonsListView.setTextFilterEnabled(true);
        gryphonsListView.setItemsCanFocus(true);

        ArrayList<RssItem> gryphonItems = new ArrayList<RssItem>();
        mGryphonsNewsAdapter = new MyGuelphNewsCustomAdapter(this, R.layout.news_item, gryphonItems);
        gryphonsListView.setAdapter(mGryphonsNewsAdapter);

        // Add to tabs
        TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("main_news").setIndicator(
                getString(R.string.news_tab_main)).setContent(R.id.main_list));
        tabHost.addTab(tabHost.newTabSpec("atguelph_news").setIndicator(
                getString(R.string.news_tab_atguelph)).setContent(R.id.at_guelph_list));
        tabHost.addTab(tabHost.newTabSpec("gryphons_news").setIndicator(
                getString(R.string.news_tab_gryphons)).setContent(R.id.gryphons_list));

        MyGuelphNewsAsyncTask task = new MyGuelphNewsAsyncTask(this);
        try {
            task.execute(new MyGuelphRssFeed[] {
                    new MyGuelphRssFeed(FeedType.MAIN,
                            new URL(getString(R.string.guelph_news_main_feed_url))),
                    new MyGuelphRssFeed(FeedType.ATGUELPH, new URL(
                            getString(R.string.guelph_news_at_guelph_feed_url))),
                    new MyGuelphRssFeed(FeedType.GRYPHONS, new URL(
                            getString(R.string.guelph_news_gryphon_main_feed_url)))
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
                    if (i.getLink() != null) {
                        String link = "<a href='" + i.getLink() + "'>" + i.getTitle() + "</a>";
                        title.setText(Html.fromHtml(link));
                    } else {
                        title.setText(i.getTitle());
                    }
                }
                if (date != null) {
                    date.setText("Posted on " + i.getPubDate());
                }
            }

            return view;
        }
    }

    public void addItem(FeedType type, RssItem item) {
        switch (type) {
            case MAIN:
                mMainNewsAdapter.add(item);
                break;
            case ATGUELPH:
                mAtGuelphNewsAdapter.add(item);
                break;
            case GRYPHONS:
                mGryphonsNewsAdapter.add(item);
                break;
        }

    }
}
