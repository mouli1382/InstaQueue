package in.mobifirst.tagtree.model;

public class CurrentToken {

    private String tokenId;
    private long currentToken;
    private int counterNumber;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public long getCurrentToken() {
        return currentToken;
    }

    public void setCurrentToken(long currentToken) {
        this.currentToken = currentToken;
    }

    public int getCounterNumber() {
        return counterNumber;
    }

    public void setCounterNumber(int counterNumber) {
        this.counterNumber = counterNumber;
    }
}