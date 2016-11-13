package in.mobifirst.tagtree.tokens;


import android.view.Gravity;

import java.util.List;
import java.util.Map;

import in.mobifirst.tagtree.model.Token;

public class Snap {

    private int mGravity;
    private String mCounter;
    private List<Token> mTokens;

    public Snap(int gravity, String counter, List<Token> apps) {
        mGravity = gravity;
        mCounter = counter;
        mTokens = apps;
    }

    public Snap(String counter, List<Token> apps) {
        mGravity = Gravity.START;
        mCounter = counter;
        mTokens = apps;
    }

    public Snap(Map<String, Token> stringTokenMap) {

    }

    public String getCounter() {
        return mCounter;
    }

    public void setCounter(String mCounter) {
        this.mCounter = mCounter;
    }


    public int getGravity() {
        return mGravity;
    }

    public List<Token> getTokens() {
        return mTokens;
    }

}
