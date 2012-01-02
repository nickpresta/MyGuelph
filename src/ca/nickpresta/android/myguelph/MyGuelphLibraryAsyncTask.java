
package ca.nickpresta.android.myguelph;

import ca.nickpresta.android.myguelph.MyGuelphApplication.LoginType;

import org.apache.http.cookie.Cookie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

public class MyGuelphLibraryAsyncTask extends AsyncTask<String, Void, Document> {

    private final Activity mParentActivity;
    private ProgressDialog mProgressDialog;

    public MyGuelphLibraryAsyncTask(Activity activity) {
        super();
        mParentActivity = activity;
        mProgressDialog = null;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mParentActivity, "Please wait...",
                "Fetching library account details",
                true, false);
    }

    @Override
    protected Document doInBackground(String... siteUrls) {
        Document result = null;

        MyGuelphApplication application = (MyGuelphApplication) ((MyGuelphLibraryActivity) mParentActivity)
                .getApplication();

        application.login(mParentActivity, LoginType.LIBRARY);

        String accountOverview = siteUrls[0];
        List<Cookie> cookies = application.getDefaultHttpClient().getCookieStore().getCookies();
        try {
            Connection connection = Jsoup.connect(accountOverview);
            for (Cookie cookie : cookies) {
                connection.cookie(cookie.getName(), cookie.getValue());
            }
            result = connection.get();
        } catch (IOException e) {
        }

        return result;
    }

    @Override
    protected void onPostExecute(Document result) {
        mProgressDialog.cancel();

        if (result != null) {
            // ((MyGuelphLibraryActivity) mParentActivity).addItem(null);
        }

    }
}
