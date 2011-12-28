
package ca.nickpresta.android.myguelph;

import org.apache.http.cookie.Cookie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class MyGuelphEventDetailsAsyncTask extends AsyncTask<String, Void, Document> {

    private final Activity mParentActivity;
    private ProgressDialog mProgressDialog;

    public MyGuelphEventDetailsAsyncTask(Activity activity) {
        super();
        mParentActivity = activity;
        mProgressDialog = null;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mParentActivity, "Please wait...",
                "Fetching event details", true, false);
    }

    @Override
    protected Document doInBackground(String... eventLinks) {
        Document result = null;

        String eventLink = eventLinks[0];

        MyGuelphApplication application = (MyGuelphApplication) ((MyGuelphEventDetailsActivity) mParentActivity)
                .getApplication();

        application.login(mParentActivity);

        List<Cookie> cookies = application.getDefaultHttpClient().getCookieStore().getCookies();

        try {
            Connection connection = Jsoup.connect(eventLink);
            for (Cookie cookie : cookies) {
                connection.cookie(cookie.getName(), cookie.getValue());
            }
            result = connection.get();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MyGuelphEventDetailsActivity", "Could not get event page using Jsoup.");
        }

        return result;
    }

    @Override
    protected void onPostExecute(Document result) {
        mProgressDialog.cancel();

        if (result != null) {
            ((MyGuelphEventDetailsActivity) mParentActivity).setRegistered(result.body().text()
                    .contains("Your have already signed up for this event"));

            Element department = result.getElementsByClass("text12").get(0)
                    .getElementsByClass("text11").get(0);
            ((MyGuelphEventDetailsActivity) mParentActivity).setDepartment(department.text());

            Element description = result.select("p.text11").first();
            ((MyGuelphEventDetailsActivity) mParentActivity).setDescription(description.html());

            Element dateRow = result.getElementsContainingOwnText("Date:").select("strong").first();
            Element date = dateRow.parent().parent().nextElementSibling();
            ((MyGuelphEventDetailsActivity) mParentActivity).setDate(date.text());

            Element timeRow = result.getElementsContainingOwnText("Time:").select("strong").first();
            Element time = timeRow.parent().parent().nextElementSibling();
            ((MyGuelphEventDetailsActivity) mParentActivity).setTime(time.text());
        }

    }
}
