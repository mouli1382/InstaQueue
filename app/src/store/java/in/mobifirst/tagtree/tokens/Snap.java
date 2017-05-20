package in.mobifirst.tagtree.tokens;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import in.mobifirst.tagtree.model.Token;

public class Snap implements Parcelable {

    private String timeRange;
    private List<Token> tokenList;

    public Snap(String time, List<Token> tokens) {
        this.timeRange = time;
        tokenList = tokens;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<Token> tokenList) {
        this.tokenList = tokenList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(timeRange);
        parcel.writeTypedList(tokenList);
    }

    protected Snap(Parcel in) {
        timeRange = in.readString();
        in.readTypedList(tokenList, Token.CREATOR);
    }

    public static final Creator<Snap> CREATOR = new Creator<Snap>() {
        @Override
        public Snap createFromParcel(Parcel in) {
            return new Snap(in);
        }

        @Override
        public Snap[] newArray(int size) {
            return new Snap[size];
        }
    };
}
