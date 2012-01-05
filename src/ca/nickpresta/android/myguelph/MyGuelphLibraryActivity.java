
package ca.nickpresta.android.myguelph;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyGuelphLibraryActivity extends ListActivity {

    private MyGuelphLibraryCustomAdapter mLibraryAdapter;
    private Dialog mMainDialog;
    private Dialog mResumeDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events);

        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        mMainDialog = null;
        if (!application.isNetworkAvailable()) {
            mMainDialog = application.redirectToIntentOrHome(this,
                    getString(R.string.missing_network_connection), new Intent(
                            Settings.ACTION_SETTINGS));
            return;
        }

        ListView listView = getListView();

        ArrayList<LibraryItem> items = new ArrayList<LibraryItem>();
        mLibraryAdapter = new MyGuelphLibraryCustomAdapter(this, R.layout.library_item, items);
        listView.setAdapter(mLibraryAdapter);

        MyGuelphLibraryAsyncTask task = new MyGuelphLibraryAsyncTask(this);
        task.execute(new String[] {
                getString(R.string.library_account_overview_url)
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refreshmenu, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMainDialog != null) {
            mMainDialog.dismiss();
        }
        if (mResumeDialog != null) {
            mResumeDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyGuelphApplication application = (MyGuelphApplication) getApplication();
        mResumeDialog = null;
        if (!application.isNetworkAvailable()) {
            mResumeDialog = application.redirectToIntentOrHome(this,
                    getString(R.string.missing_network_connection),
                    new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private class MyGuelphLibraryCustomAdapter extends ArrayAdapter<LibraryItem> {
        private final ArrayList<LibraryItem> items;

        public MyGuelphLibraryCustomAdapter(Context context, int textViewResourceId,
                ArrayList<LibraryItem> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.events_item, null);
            }

            LibraryItem i = items.get(position);
            if (i != null) {
                TextView title = (TextView) view.findViewById(R.id.library_item_title);
                TextView type = (TextView) view.findViewById(R.id.library_item_type);
                TextView status = (TextView) view.findViewById(R.id.library_item_status);
                TextView renewalStatus = (TextView) view
                        .findViewById(R.id.library_item_renewal_status);
                if (title != null) {
                    title.setText(i.getItemName());
                }
                if (type != null) {
                    type.setText(i.getItemType());
                }
                if (status != null) {
                    status.setText(i.getItemStatus());
                }
                if (renewalStatus != null) {
                    renewalStatus.setText(i.getRenewalStatus());
                }
            }

            return view;
        }
    }

    public void addItem(LibraryItem item) {
        mLibraryAdapter.add(item);
    }
}
