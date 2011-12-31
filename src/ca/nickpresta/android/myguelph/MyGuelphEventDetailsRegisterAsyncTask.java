
package ca.nickpresta.android.myguelph;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyGuelphEventDetailsRegisterAsyncTask extends AsyncTask<String, Void, HttpResponse> {

    private final Activity mParentActivity;
    private ProgressDialog mProgressDialog;

    public MyGuelphEventDetailsRegisterAsyncTask(Activity activity) {
        super();
        mParentActivity = activity;
        mProgressDialog = null;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mParentActivity, "Please wait...",
                "Registering for event", true, false);
    }

    @Override
    protected HttpResponse doInBackground(String... registerUrls) {
        HttpResponse result = null;

        String registerUrl = registerUrls[0];
        String id = registerUrls[1];

        MyGuelphApplication application = (MyGuelphApplication) ((MyGuelphEventDetailsActivity) mParentActivity)
                .getApplication();
        boolean loggedIn = application.login(mParentActivity);
        if (!loggedIn) {
            return result;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mParentActivity);

        HttpPost httpPost = new HttpPost(registerUrl);

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("event_id", id));
        nameValuePair.add(new BasicNameValuePair("act", "register"));
        nameValuePair.add(new BasicNameValuePair("clogin", prefs
                .getString("prefs_username", "NULL")));
        nameValuePair.add(new BasicNameValuePair("password", prefs.getString("prefs_password",
                "NULL")));
        nameValuePair.add(new BasicNameValuePair("discover_id", "0"));
        nameValuePair.add(new BasicNameValuePair("imageField", ""));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            result = application.getDefaultHttpClient().execute(httpPost);
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
        Log.i("Register", "Status = " + result.getStatusLine().getStatusCode());
        if (result != null && result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            ((MyGuelphEventDetailsActivity) mParentActivity).setRegistered(true);
        }
    }
}
