
package ca.nickpresta.android.myguelph;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.TabActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MyGuelphInfoActivity extends TabActivity {

    private MyGuelphInfoCustomAdapter mCoreInfoAdapter;
    private MyGuelphInfoCustomAdapter mSecondaryInfoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        // Core info
        ListView coreListView = (ListView) findViewById(R.id.info_core_list);
        coreListView.setTextFilterEnabled(true);
        coreListView.setItemsCanFocus(true);

        ArrayList<MyGuelphWebsite> mainItems = new ArrayList<MyGuelphWebsite>();
        mCoreInfoAdapter = new MyGuelphInfoCustomAdapter(this, R.layout.info_item, mainItems);
        coreListView.setAdapter(mCoreInfoAdapter);

        // Secondary info
        ListView secondaryListView = (ListView) findViewById(R.id.info_secondary_list);
        secondaryListView.setTextFilterEnabled(true);
        secondaryListView.setItemsCanFocus(true);

        ArrayList<MyGuelphWebsite> secondaryItems = new ArrayList<MyGuelphWebsite>();
        mSecondaryInfoAdapter = new MyGuelphInfoCustomAdapter(this, R.layout.info_item,
                secondaryItems);
        secondaryListView.setAdapter(mSecondaryInfoAdapter);

        // Add to tabs
        TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("core_info").setIndicator(
                getString(R.string.info_tab_core)).setContent(R.id.info_core_list));
        tabHost.addTab(tabHost.newTabSpec("secondary_info").setIndicator(
                getString(R.string.info_tab_secondary)).setContent(R.id.info_secondary_list));

        parse(R.raw.info_core, mCoreInfoAdapter);
        parse(R.raw.info_secondary, mSecondaryInfoAdapter);
    }

    private void parse(int xmlResource, MyGuelphInfoCustomAdapter adapter) {
        InputStream rawInput = getResources().openRawResource(xmlResource);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        if (documentBuilder == null) {
            return;
        }
        Document document = null;
        try {
            document = documentBuilder.parse(rawInput, null);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (document == null) {
            return;
        }
        NodeList websites = document.getElementsByTagName("website");
        for (int i = 0; i < websites.getLength(); i++) {
            Node currentNode = websites.item(i);
            String address = currentNode.getChildNodes().item(1).getTextContent();
            String title = currentNode.getChildNodes().item(3).getTextContent();
            String description = currentNode.getChildNodes().item(5).getTextContent();
            MyGuelphWebsite myWebsite = new MyGuelphWebsite(address, title, description);
            adapter.add(myWebsite);
        }

    }

    public static class FlingableTabHost extends TabHost {
        GestureDetector mGestureDetector;

        Animation mRightInAnimation;
        Animation mRightOutAnimation;
        Animation mLeftInAnimation;
        Animation mLeftOutAnimation;

        public FlingableTabHost(Context context, AttributeSet attrs) {
            super(context, attrs);

            mRightInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
            mRightOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
            mLeftInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
            mLeftOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);

            final int minScaledFlingVelocity = ViewConfiguration.get(context)
                    .getScaledMinimumFlingVelocity() * 10; // 10 = fudge by
                                                           // experimentation

            mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                        float velocityY) {
                    int tabCount = getTabWidget().getTabCount();
                    int currentTab = getCurrentTab();
                    if (Math.abs(velocityX) > minScaledFlingVelocity &&
                            Math.abs(velocityY) < minScaledFlingVelocity) {

                        final boolean right = velocityX < 0;
                        final int newTab = MathUtils.constrain(currentTab + (right ? 1 : -1),
                                0, tabCount - 1);
                        if (newTab != currentTab) {
                            // Somewhat hacky, depends on current implementation
                            // of TabHost:
                            // http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;
                            // f=core/java/android/widget/TabHost.java
                            View currentView = getCurrentView();
                            setCurrentTab(newTab);
                            View newView = getCurrentView();

                            newView.startAnimation(right ? mRightInAnimation : mLeftInAnimation);
                            currentView.startAnimation(
                                    right ? mRightOutAnimation : mLeftOutAnimation);
                        }
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (mGestureDetector.onTouchEvent(ev)) {
                return true;
            }
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyGuelphMenu.onCreateOptionsMenu(menu, getMenuInflater());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyGuelphMenu.onOptionsItemSelected(item, this);
        return super.onOptionsItemSelected(item);
    }

    private class MyGuelphInfoCustomAdapter extends ArrayAdapter<MyGuelphWebsite> {
        private final ArrayList<MyGuelphWebsite> websites;

        public MyGuelphInfoCustomAdapter(Context context, int textViewResourceId,
                ArrayList<MyGuelphWebsite> websites) {
            super(context, textViewResourceId, websites);
            this.websites = websites;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.info_item, null);
            }

            MyGuelphWebsite website = websites.get(position);

            if (website != null) {
                TextView title = (TextView) view.findViewById(R.id.info_item_title);
                title.setMovementMethod(LinkMovementMethod.getInstance());
                TextView description = (TextView) view.findViewById(R.id.info_item_description);
                String link = "<a href='" + website.getAddress() + "'>" + website.getTitle()
                        + "</a>";
                title.setText(Html.fromHtml(link));
                description.setText(website.getDescription());
            }

            return view;
        }
    }
}
