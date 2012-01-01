
package ca.nickpresta.android.myguelph;

import ca.nickpresta.android.myguelph.MyGuelphRssFeed.FeedType;
import nl.matshofman.saxrssreader.RssItem;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        if (!application.isNetworkAvailable()) {
            application.redirectToIntentOrHome(this,
                    getString(R.string.missing_network_connection), new Intent(
                            Settings.ACTION_SETTINGS));
            return;
        }

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

    public static class FlingableTabHost extends TabHost {
        GestureDetector mGestureDetector;

        Animation mRightInAnimation;
        Animation mRightOutAnimation;
        Animation mLeftInAnimation;
        Animation mLeftOutAnimation;

        public FlingableTabHost(Context context, AttributeSet attrs) {
            super(context, attrs);

            mRightInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
            mRightOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
            mLeftInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
            mLeftOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);

            final int minScaledFlingVelocity = ViewConfiguration.get(context)
                    .getScaledMinimumFlingVelocity() * 10; // 10 = fudge by
                                                           // experimentation

            mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                        float velocityY) {
                    int tabCount = getTabWidget().getTabCount();
                    int currentTab = getCurrentTab();
                    if (Math.abs(velocityX) > minScaledFlingVelocity &&
                            Math.abs(velocityY) < minScaledFlingVelocity) {

                        final boolean right = velocityX < 0;
                        final int newTab = MathUtils.constrain(currentTab + (right ? 1 : -1),
                                0, tabCount - 1);
                        if (newTab != currentTab) {
                            // Somewhat hacky, depends on current implementation
                            // of TabHost:
                            // http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;
                            // f=core/java/android/widget/TabHost.java
                            View currentView = getCurrentView();
                            setCurrentTab(newTab);
                            View newView = getCurrentView();

                            newView.startAnimation(right ? mRightInAnimation : mLeftInAnimation);
                            currentView.startAnimation(
                                    right ? mRightOutAnimation : mLeftOutAnimation);
                        }
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (mGestureDetector.onTouchEvent(ev)) {
                return true;
            }
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        if (!application.isNetworkAvailable()) {
            application.redirectToIntentOrHome(this,
                    getString(R.string.missing_network_connection), new Intent(
                            Settings.ACTION_SETTINGS));
        }
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
                TextView title = (TextView) view.findViewById(R.id.news_item_title);
                title.setMovementMethod(LinkMovementMethod.getInstance());
                TextView date = (TextView) view.findViewById(R.id.news_item_date);
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
