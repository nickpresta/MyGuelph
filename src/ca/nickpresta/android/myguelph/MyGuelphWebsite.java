
package ca.nickpresta.android.myguelph;

public class MyGuelphWebsite {
    private final String mAddress;
    private final String mTitle;
    private final String mDescription;

    public MyGuelphWebsite(String address, String title, String description) {
        mAddress = address;
        mTitle = title;
        mDescription = description;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    public String toString() {
        return mAddress + " " + mTitle + " " + mDescription;
    }
}
