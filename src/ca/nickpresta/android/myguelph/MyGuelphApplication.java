
package ca.nickpresta.android.myguelph;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyGuelphApplication extends Application {

    private final DefaultHttpClient mHttpClient = new DefaultHttpClient();
    private final BasicCookieStore mCookieStore = new BasicCookieStore();
    private boolean mLoggedIn;
    private final String mBalanceDetailsUrl;

    public enum LoginType {
        EVENTS, LIBRARY, BALANCE
    };

    public MyGuelphApplication() {
        mHttpClient.setCookieStore(mCookieStore);
        mLoggedIn = false;
        mBalanceDetailsUrl = "";
    }

    public boolean login(Context context, LoginType type) {
        switch (type) {
            case EVENTS:
                return loginToEvents(context);
            case LIBRARY:
                return loginToLibrary(context);
            case BALANCE:
                return loginToBalance(context);
        }
        return false;
    }

    private boolean loginToEvents(Context context) {
        HttpResponse result = null;

        String loginUrl = context.getString(R.string.events_login_url);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        HttpPost httpPost = new HttpPost(loginUrl);

        String username = prefs.getString("prefs_username", "NULL");
        String password = prefs.getString("prefs_password", "NULL");

        // User hasn't entered credentials yet
        if (username.equals("NULL") || username.isEmpty()) {
            mLoggedIn = false;
            return false;
        }

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("clogin", username));
        nameValuePair.add(new BasicNameValuePair("password", password));

        InputStream inputStream = null;
        String content = "";
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            result = getDefaultHttpClient().execute(httpPost);
            inputStream = result.getEntity().getContent();
        } catch (ClientProtocolException e) {
            // TODO: Log this
        } catch (IOException e) {
            // TODO: Log this
        }

        if (inputStream != null) {
            content = convertStreamToString(inputStream);
        }

        if (content.contains("You specified the incorrect password") ||
                content.contains("Specified user not found")) {
            mLoggedIn = false;
            return false;
        } else if (content.contains("My Event Registrations")) {
            mLoggedIn = true;
            return true;
        }

        return false;
    }

    private boolean loginToLibrary(Context context) {
        HttpResponse result = null;

        String loginUrl = context.getString(R.string.library_login_url);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        HttpPost httpPost = new HttpPost(loginUrl);

        String username = prefs.getString("prefs_username", "NULL");
        String password = prefs.getString("prefs_password", "NULL");

        // User hasn't entered credentials yet
        if (username.equals("NULL") || username.isEmpty()) {
            mLoggedIn = false;
            return false;
        }

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("campusId", username));
        nameValuePair.add(new BasicNameValuePair("password", password));
        nameValuePair.add(new BasicNameValuePair("myParams",
                "PAGE=pbPatron&PID=myguelph&SEQ=20120101000000&proxy=false"));
        nameValuePair.add(new BasicNameValuePair("PID", "myguelph"));
        nameValuePair.add(new BasicNameValuePair("SEQ", "20120101000000"));
        nameValuePair.add(new BasicNameValuePair("PAGE", "pbPatron"));
        nameValuePair.add(new BasicNameValuePair("LN", ""));
        nameValuePair.add(new BasicNameValuePair("BC", ""));

        InputStream inputStream = null;
        String content = "";
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            result = getDefaultHttpClient().execute(httpPost);
            inputStream = result.getEntity().getContent();
        } catch (ClientProtocolException e) {
            // TODO: Log this
        } catch (IOException e) {
            // TODO: Log this
        }

        if (inputStream != null) {
            content = convertStreamToString(inputStream);
        }

        if (content.contains("Your user ID / password combination was not found")) {
            mLoggedIn = false;
            return false;
        } else if (content.contains("Problem(s) that are blocking your library account")) {
            mLoggedIn = true;
            return true;
        }

        return false;
    }

    private boolean loginToBalance(Context context) {
        HttpResponse result = null;

        String loginUrl = context.getString(R.string.balance_login_url);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        HttpPost httpPost = new HttpPost(loginUrl);

        String username = prefs.getString("prefs_username", "NULL");
        String password = prefs.getString("prefs_password", "NULL");

        // User hasn't entered credentials yet
        if (username.equals("NULL") || username.isEmpty()) {
            mLoggedIn = false;
            return false;
        }

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("j_username", username));
        nameValuePair.add(new BasicNameValuePair("j_password", password));
        nameValuePair.add(new BasicNameValuePair("args", "action=balance"));
        nameValuePair.add(new BasicNameValuePair("redirect", "/accountservices/chooseaccount.cfm"));

        InputStream inputStream = null;
        String content = "";
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            result = getDefaultHttpClient().execute(httpPost);
            inputStream = result.getEntity().getContent();
        } catch (ClientProtocolException e) {
            // TODO: Log this
        } catch (IOException e) {
            // TODO: Log this
        }

        if (inputStream != null) {
            content = convertStreamToString(inputStream);
        }

        if (content.contains("Your login information is not valid")) {
            mLoggedIn = false;
            return false;
        } else if (content.contains("Your Current Balance")) {
            mLoggedIn = true;
            return true;
        }

        return false;
    }

    public void redirectToIntentOrHome(final Context context, String message, final Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Configure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ((Activity) context).finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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

    public String getBalanceDetailsUrl() {
        return mBalanceDetailsUrl;
    }

    public void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
    }

    public boolean hasCredentials(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String username = prefs.getString("prefs_username", "NULL");
        String password = prefs.getString("prefs_password", "NULL");
        if (username.equals("NULL") || username.isEmpty() || password.isEmpty()) {
            return false;
        }
        return true;

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public boolean isGpsAvailable() {
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private String convertStreamToString(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\A").next();
    }
}
