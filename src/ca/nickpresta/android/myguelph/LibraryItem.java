
package ca.nickpresta.android.myguelph;

public class LibraryItem {
    private final String mItemName;
    private final String mItemType;
    private final String mItemStatus;
    private final String mRenewalStatus;

    public LibraryItem(String itemName, String itemType, String itemStatus, String renewalStatus) {
        mItemName = itemName;
        mItemType = itemType;
        mItemStatus = itemStatus;
        mRenewalStatus = renewalStatus;
    }

    public String getItemName() {
        return mItemName;
    }

    public String getItemType() {
        return mItemType;
    }

    public String getItemStatus() {
        return mItemStatus;
    }

    public String getRenewalStatus() {
        return mRenewalStatus;
    }
}
