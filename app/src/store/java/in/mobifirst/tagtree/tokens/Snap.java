package in.mobifirst.tagtree.tokens;


import java.util.List;

import in.mobifirst.tagtree.model.Token;

public class Snap {

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
}
