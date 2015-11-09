package signin.ez.ezsignin;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Freeman on 11/8/15.
 */
public class Record implements Serializable {
    private String mName;
    private String mDate;
    private String mCounty;
    private String mAddress;
    private int mNumInHousehold;
    private boolean isEligibleFS = false;
    private boolean isEligibleMC = false;
    private boolean isEligibleSS = false;
    private boolean isEligibleTANF = false;
    private boolean isEligibleIE = false;
    public static int recordId = 0;
    private int mRecordId = 0;

    public Record() {
        mRecordId = recordId++;
    }

    public void setName(String name) {
        mName = name;
    }
    public void setCounty(String county) {
        mCounty = county;
    }
    public void setDate(String date) {
        mDate = date;
    }
    public void setAddress(String address) {
        mAddress = address;
    }
    public void setNumInHousehold(int numInHousehold) {
        mNumInHousehold = numInHousehold;
    }
    public String getName() {
        return mName;
    }
    public String getCounty() {
        return mCounty;
    }
    public String getDate() {
        return mDate;
    }
    public String getAddress() {
        return mAddress;
    }
    public int getNumInHousehold() {
        return mNumInHousehold;
    }

    public boolean isEligibleFS() {
        return isEligibleFS;
    }

    public void setIsEligibleFS(boolean isEligibleFS) {
        this.isEligibleFS = isEligibleFS;
    }

    public boolean isEligibleMC() {
        return isEligibleMC;
    }

    public void setIsEligibleMC(boolean isEligibleMC) {
        this.isEligibleMC = isEligibleMC;
    }

    public boolean isEligibleSS() {
        return isEligibleSS;
    }

    public void setIsEligibleSS(boolean isEligibleSS) {
        this.isEligibleSS = isEligibleSS;
    }

    public boolean isEligibleTANF() {
        return isEligibleTANF;
    }

    public void setIsEligibleTANF(boolean isEligibleTANF) {
        this.isEligibleTANF = isEligibleTANF;
    }

    public boolean isEligibleIE() {
        return isEligibleIE;
    }

    public void setIsEligibleIE(boolean isEligibleIE) {
        this.isEligibleIE = isEligibleIE;
    }

    public int getRecordId() {
        return mRecordId;
    }
}
