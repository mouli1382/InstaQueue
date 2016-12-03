package in.mobifirst.tagtree.tokens;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import in.mobifirst.tagtree.model.Token;

public class Snap implements Parcelable {

    private int counter;
    private List<Token> tokenList;

    public Snap(int counter, List<Token> tokens) {
        this.counter = counter;
        tokenList = tokens;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
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
        parcel.writeInt(counter);
        parcel.writeTypedList(tokenList);
    }

    protected Snap(Parcel in) {
        counter = in.readInt();
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
