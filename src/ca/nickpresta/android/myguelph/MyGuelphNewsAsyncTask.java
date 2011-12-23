
package ca.nickpresta.android.myguelph;

import ca.nickpresta.android.myguelph.MyGuelphRssFeed.FeedType;
import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyGuelphNewsAsyncTask extends
        AsyncTask<MyGuelphRssFeed, Void, HashMap<MyGuelphRssFeed.FeedType, ArrayList<RssItem>>> {

    private final Activity mParentActivity;
    private ProgressDialog mProgressDialog;

    public MyGuelphNewsAsyncTask(Activity activity) {
        super();
        mParentActivity = activity;
        mProgressDialog = null;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mParentActivity, "Please wait...", "Fetching news",
                true, false);
    }

    @Override
    protected HashMap<FeedType, ArrayList<RssItem>> doInBackground(
            MyGuelphRssFeed... feeds) {
        HashMap<MyGuelphRssFeed.FeedType, ArrayList<RssItem>> results =
                new HashMap<MyGuelphRssFeed.FeedType, ArrayList<RssItem>>();

        for (MyGuelphRssFeed feed : feeds) {
            ArrayList<RssItem> result = null;
            RssFeed f = null;
            try {
                f = RssReader.read(feed.getUrl());
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            result = f.getRssItems();
            if (result != null && result.size() > 1) {
                results.put(feed.getType(), result);
            }
        }

        return results;
    }

    @Override
    protected void onPostExecute(HashMap<MyGuelphRssFeed.FeedType, ArrayList<RssItem>> result) {
        mProgressDialog.cancel();

        for (Map.Entry<MyGuelphRssFeed.FeedType, ArrayList<RssItem>> feed : result.entrySet()) {
            MyGuelphRssFeed.FeedType type = feed.getKey();
            ArrayList<RssItem> item = feed.getValue();
            for (RssItem i : item) {
                ((MyGuelphNewsActivity) mParentActivity).addItem(type, i);
            }
        }

    }
}
