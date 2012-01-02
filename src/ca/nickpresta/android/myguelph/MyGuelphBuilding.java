
package ca.nickpresta.android.myguelph;

import android.text.TextUtils;

public class MyGuelphBuilding {
    private final String mBuildingCode;
    private final String mBuildingName;

    public MyGuelphBuilding(String code, String name) {
        mBuildingCode = code;
        mBuildingName = name;
    }

    public String getBuildingName() {
        return mBuildingName;
    }

    @Override
    public String toString() {
        String outString = "";
        if (!TextUtils.isEmpty(mBuildingCode)) {
            outString += mBuildingCode + " - ";
        }
        outString += mBuildingName;
        return outString;
    }
}
