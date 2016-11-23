package in.mobifirst.tagtree.model;

import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import in.mobifirst.tagtree.util.TimeUtils;

public class StoreCounter {
    private static final String TAG = "StoreCounter";

    private long activatedToken;
    private long avgTurnAroundTime;
    private int counterUserCount;
    private Map<String, Long> tokens;

    public long getAvgTurnAroundTime() {
        return avgTurnAroundTime;
    }

    public void setAvgTurnAroundTime(long avgTurnAroundTime) {
        this.avgTurnAroundTime = avgTurnAroundTime;
    }

    public int getCounterUserCount() {
        return counterUserCount;
    }

    public void setCounterUserCount(int counterUserCount) {
        this.counterUserCount = counterUserCount;
    }

    public Map<String, Long> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, Long> tokens) {
        this.tokens = tokens;
    }

    public long getActivatedToken() {
        return activatedToken;
    }

    public void setActivatedToken(long activatedToken) {
        this.activatedToken = activatedToken;
    }

    private long getAvgTATPerToken() {
        if (counterUserCount > 0) {
            long result = (new BigDecimal(avgTurnAroundTime).divide(new BigDecimal(counterUserCount), BigDecimal.ROUND_HALF_UP)).longValue();
            Log.e(TAG, "avgTATPerToken = " + result);
            return result;
        }
        return 0;
    }

    public String ETA(long given) {
        long avgTATPerToken = getAvgTATPerToken();

        if (avgTATPerToken == 0)
            return "" + 0;

        if (tokens == null || tokens.size() == 0)
            return TimeUtils.getDuration(avgTATPerToken);

        List<Long> tokenList = new ArrayList<>(tokens.values());
        if (tokenList.size() == 0) {
            return TimeUtils.getDuration(avgTATPerToken);
        }

        //ToDo add activatedTimestamp under store counter and sort by it here instead of the token number as it need not be in a chronological order.
        Collections.sort(tokenList);
        if (tokenList.size() > 1) {
            long ETA = (tokenList.indexOf(given) + 1) * avgTATPerToken;
            Log.e(TAG, "ETA = " + ETA);
            return TimeUtils.getDuration(ETA);
        }
        return TimeUtils.getDuration(avgTATPerToken);
    }
}
