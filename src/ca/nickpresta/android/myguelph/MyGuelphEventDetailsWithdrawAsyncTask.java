
package ca.nickpresta.android.myguelph;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyGuelphEventDetailsWithdrawAsyncTask extends AsyncTask<String, Void, HttpResponse> {

    private final Activity mParentActivity;
    private ProgressDialog mProgressDialog;

    public MyGuelphEventDetailsWithdrawAsyncTask(Activity activity) {
        super();
        mParentActivity = activity;
        mProgressDialog = null;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mParentActivity, "Please wait...",
                "Withdrawing you from the event", true, false);
    }

    @Override
    protected HttpResponse doInBackground(String... registerUrls) {
        HttpResponse result = null;

        String registerUrl = registerUrls[0];
        String id = registerUrls[1];

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("event_id", id));
        nameValuePair.add(new BasicNameValuePair("act", "withdraw"));
        String params = URLEncodedUtils.format(nameValuePair, "UTF-8");
        registerUrl += "?" + params;

        HttpGet httpGet = new HttpGet(registerUrl);

        try {
            MyGuelphApplication application = (MyGuelphApplication) ((MyGuelphEventDetailsActivity) mParentActivity)
                    .getApplication();
            result = application.getDefaultHttpClient().execute(httpGet);
            result.getEntity().consumeContent();
        } catch (ClientProtocolException e) {
            // TODO: Log this
        } catch (IOException e) {
            // TODO: Log this
        }

        return result;
    }

    @Override
    protected void onPostExecute(HttpResponse result) {
        mProgressDialog.cancel();
        if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            ((MyGuelphEventDetailsActivity) mParentActivity).setRegistered(false);
        }
    }
}
