
package ca.nickpresta.android.myguelph;

import java.net.URL;

public class MyGuelphRssFeed {

    public enum FeedType {
        MAIN, ATGUELPH, GRYPHONS
    };

    private final FeedType type;
    private final URL url;

    public MyGuelphRssFeed(FeedType feedType, URL feedUrl) {
        type = feedType;
        url = feedUrl;
    }

    public FeedType getType() {
        return type;
    }

    public URL getUrl() {
        return url;
    }

}
