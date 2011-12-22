
package ca.nickpresta.android.myguelph;

import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MyGuelphNewsAsyncTask extends AsyncTask<URL, Void, ArrayList<ArrayList<RssItem>>> {

    private final Activity mParentActivity;

    public MyGuelphNewsAsyncTask(Activity activity) {
        super();
        mParentActivity = activity;
    }

    @Override
    protected ArrayList<ArrayList<RssItem>> doInBackground(URL... urls) {
        ArrayList<ArrayList<RssItem>> results = new ArrayList<ArrayList<RssItem>>();

        for (URL url : urls) {
            ArrayList<RssItem> result = null;
            RssFeed feed = null;
            try {
                feed = RssReader.read(url);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            result = feed.getRssItems();
            if (result != null && result.size() > 1) {
                results.add(result);
            }
        }

        Log.i("MyGuelphNewsAsyncTask", "Finished fetching");
        return results;
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList<RssItem>> result) {
        // TODO: Implement filling list view, etc
        Log.i("MyGuelphNewsAsyncTask", "Finished");
        ((MyGuelphNewsActivity) mParentActivity).cancleDialog();
        for (ArrayList<RssItem> feed : result) {
            for (RssItem item : feed) {
                Log.i("MyGuelphNewsAsyncTask", item.getTitle());
            }
        }
    }
}
