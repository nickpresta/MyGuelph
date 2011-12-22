
package ca.nickpresta.android.myguelph;

public class Building {
    private final String mBuildingCode;
    private final String mBuildingName;

    public Building(String code, String name) {
        mBuildingCode = code;
        mBuildingName = name;
    }

    public String getBuildingName() {
        return mBuildingName;
    }

    @Override
    public String toString() {
        String outString = "";
        if (!mBuildingCode.isEmpty()) {
            outString += mBuildingCode + " - ";
        }
        outString += mBuildingName;
        return outString;
    }
}
