
package ca.nickpresta.android.myguelph;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyGuelphApplication extends Application {

    private final DefaultHttpClient mHttpClient = new DefaultHttpClient();
    private final BasicCookieStore mCookieStore = new BasicCookieStore();
    private boolean mLoggedIn;

    public MyGuelphApplication() {
        mHttpClient.setCookieStore(mCookieStore);
        mLoggedIn = false;
    }

    public boolean login(Context context) {
        HttpResponse result = null;

        String loginUrl = context.getString(R.string.app_login_url);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        HttpPost httpPost = new HttpPost(loginUrl);

        String username = prefs.getString("prefs_username", "NULL");
        String password = prefs.getString("prefs_password", "NULL");

        // User hasn't entered credentials yet
        if (username.equals("NULL")) {
            mLoggedIn = false;
            return false;
        }

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("clogin", username));
        nameValuePair.add(new BasicNameValuePair("password", password));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            result = getDefaultHttpClient().execute(httpPost);
            result.getEntity().consumeContent();
        } catch (ClientProtocolException e) {
            // TODO: Log this
        } catch (IOException e) {
            // TODO: Log this
        }

        if (result != null && result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            mLoggedIn = true;
            return true;
        } else {
            mLoggedIn = false;
            return false;
        }

    }

    public DefaultHttpClient getDefaultHttpClient() {
        return mHttpClient;
    }

    public BasicCookieStore getBasicCookieStore() {
        return mCookieStore;
    }

    public boolean getLoggedIn() {
        return mLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
    }
}
