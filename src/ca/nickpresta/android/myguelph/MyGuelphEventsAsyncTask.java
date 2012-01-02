
package ca.nickpresta.android.myguelph;

import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MyGuelphEventsAsyncTask extends AsyncTask<URL, Void, ArrayList<RssItem>> {

    private final Activity mParentActivity;
    private ProgressDialog mProgressDialog;

    public MyGuelphEventsAsyncTask(Activity activity) {
        super();
        mParentActivity = activity;
        mProgressDialog = null;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mParentActivity, "Please wait...", "Fetching events",
                true, false);
    }

    @Override
    protected ArrayList<RssItem> doInBackground(URL... feedUrls) {
        ArrayList<RssItem> results = new ArrayList<RssItem>();

        for (URL feedUrl : feedUrls) {
            RssFeed f = null;
            try {
                f = RssReader.read(feedUrl, "ISO-8859-1");
            } catch (SAXException e) {
            } catch (IOException e) {
            }
            if (f != null) {
                results = f.getRssItems();
            }
        }

        return results;
    }

    @Override
    protected void onPostExecute(ArrayList<RssItem> result) {
        mProgressDialog.cancel();

        for (RssItem item : result) {
            ((MyGuelphEventsActivity) mParentActivity).addItem(item);
        }

    }
}
