
package ca.nickpresta.android.myguelph;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MyGuelphEventDetailsActivity extends Activity {

    private String mEventLink;
    private TextView mDepartment;
    private TextView mDescription;
    private TextView mDate;
    private TextView mTime;
    private TextView mLocation;
    private Button mRegistered;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        Bundle extras = getIntent().getExtras();
        String eventTitle = extras.getString("eventTitle");
        mEventLink = extras.getString("eventLink");

        mDepartment = (TextView) findViewById(R.id.eventDepartment);
        mDescription = (TextView) findViewById(R.id.eventDescription);
        mDate = (TextView) findViewById(R.id.eventDate);
        mTime = (TextView) findViewById(R.id.eventTime);
        mLocation = (TextView) findViewById(R.id.eventLocation);
        mRegistered = (Button) findViewById(R.id.eventRegisterButton);

        TextView title = (TextView) findViewById(R.id.eventTitle);
        title.setText(eventTitle);

        MyGuelphEventDetailsAsyncTask task = new MyGuelphEventDetailsAsyncTask(this);
        task.execute(new String[] {
                mEventLink
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.openinbrowsermenu, menu);
        MyGuelphMenu.onCreateOptionsMenu(menu, inflater);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyGuelphMenu.onOptionsItemSelected(item, this);
        int itemId = item.getItemId();
        if (itemId == R.id.menuRefresh) {
            startActivity(getIntent());
            finish();
            return true;
        } else if (itemId == R.id.menuOpenInBrowser) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(mEventLink));
            startActivity(browserIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void registerEvent(View view) {
        checkLogin();
    }

    private void checkLogin() {
        MyGuelphApplication application = (MyGuelphApplication) this.getApplication();
        if (!application.hasCredentials(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.missing_credentials))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.positive_setup),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent prefsIntent = new Intent(getApplicationContext(),
                                            MyGuelphPrefsActivity.class);
                                    startActivity(prefsIntent);
                                    promptRegister();
                                }
                            })
                    .setNegativeButton(getString(R.string.negative_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            promptRegister();
        }
    }

    private void promptRegister() {
        new AlertDialog.Builder(MyGuelphEventDetailsActivity.this)
                .setTitle(getString(R.string.register_confirmation))
                .setMessage(getString(R.string.want_to_register))
                .setPositiveButton(getString(R.string.positive_register),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                registerEvent();
                            }
                        })
                .setNegativeButton(getString(R.string.negative_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            }
                        }).create().show();
    }

    private void registerEvent() {
        String ids[] = mEventLink.split("=");
        String id = ids[ids.length - 1];

        MyGuelphEventDetailsRegisterAsyncTask task = new MyGuelphEventDetailsRegisterAsyncTask(this);
        task.execute(new String[] {
                getString(R.string.event_details_register_link),
                id
        });
    }

    public void setDepartment(String department) {
        mDepartment.setText(Html.fromHtml(department));
    }

    public void setDescription(String description) {
        mDescription.setText(Html.fromHtml(description));
    }

    public void setDate(String date) {
        mDate.setText("Date: " + date);
    }

    public void setTime(String time) {
        mTime.setText("Time: " + time);
    }

    public void setLocation(String location) {
        mLocation.setText("Location: " + location);
    }

    public void setRegistered(boolean registered) {
        if (registered) {
            mRegistered.setText(getString(R.string.event_details_register_true));
            mRegistered.setEnabled(false);
            mRegistered.setTextColor(Color.GREEN);
        } else {
            mRegistered.setText(getString(R.string.event_details_register_false));
            mRegistered.setEnabled(true);
            mRegistered.setTextColor(Color.WHITE);
        }
    }
}
