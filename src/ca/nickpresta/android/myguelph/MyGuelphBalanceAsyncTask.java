
package ca.nickpresta.android.myguelph;

import ca.nickpresta.android.myguelph.MyGuelphApplication.LoginType;

import org.apache.http.cookie.Cookie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class MyGuelphBalanceAsyncTask extends AsyncTask<String, Void, Document> {

    private final Activity mParentActivity;
    private ProgressDialog mProgressDialog;

    public MyGuelphBalanceAsyncTask(Activity activity) {
        super();
        mParentActivity = activity;
        mProgressDialog = null;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mParentActivity, "Please wait...",
                "Fetching meal card balance details",
                true, false);
    }

    @Override
    protected Document doInBackground(String... siteUrls) {
        Document result = null;

        MyGuelphApplication application = (MyGuelphApplication) ((MyGuelphBalanceActivity) mParentActivity)
                .getApplication();

        boolean loggedIn = application.login(mParentActivity, LoginType.BALANCE);
        if (!loggedIn) {
            return result;
        }

        List<Cookie> cookies = application.getDefaultHttpClient().getCookieStore().getCookies();
        try {
            Connection connection = Jsoup.connect(mParentActivity
                    .getString(R.string.balance_details_url));
            for (Cookie cookie : cookies) {
                connection.cookie(cookie.getName(), cookie.getValue());
            }
            result = connection.get();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MyGuelphBalanceDetailsActivity", "Could not get balance page using Jsoup.");
        }

        return result;
    }

    @Override
    protected void onPostExecute(Document result) {
        mProgressDialog.cancel();

        if (result != null) {
            // ((MyGuelphBalanceActivity) mParentActivity).setBalance(null);
            Log.i("MyGuelphBalanceAsyncTask", result.text());
            Elements style2Column = result.select("td.style2");
            if (style2Column.size() > 0) {
                Element balanceAmountColumn = style2Column.get(0).nextElementSibling();
                if (balanceAmountColumn != null) {
                    ((MyGuelphBalanceActivity) mParentActivity).setBalance(balanceAmountColumn
                            .text());
                }
            }
        }

    }
}
