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
    private long avgBurstTime;
    private Map<String, Long> tokens;
    private long speedIndicator;

    public long getAvgBurstTime() {
        return avgBurstTime;
    }

    public void setAvgBurstTime(long avgBurstTime) {
        this.avgBurstTime = avgBurstTime;
    }

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


    public long getSpeedIndicator() {
        return speedIndicator;
    }

    public void setSpeedIndicator(long speedIndicator) {
        this.speedIndicator = speedIndicator;
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
            return "N/A";

        if (tokens == null || tokens.size() == 0)
            return TimeUtils.getDuration(avgTATPerToken);

        List<Long> tokenList = new ArrayList<>(tokens.values());
        if (tokenList.size() == 0) {
            return TimeUtils.getDuration(avgTATPerToken);
        }

        //ToDo add activatedTimestamp under store counter and sort by it here instead of the token number as it need not be in a chronological order.
        Collections.sort(tokenList);
        if (tokenList.size() > 0) {
            long ETA = (tokenList.indexOf(given)) * avgTATPerToken;
            Log.e(TAG, "ETA = " + ETA);
            return TimeUtils.getDuration(ETA);
        }
        return TimeUtils.getDuration(avgTATPerToken);
    }

    private long getAvgBurstPerToken() {
        if (counterUserCount > 0) {
            long result = (new BigDecimal(avgBurstTime).divide(new BigDecimal(counterUserCount), BigDecimal.ROUND_HALF_UP)).longValue();
            Log.e(TAG, "avgBurstTimePerToken = " + result);
            return result;
        }
        return 0;
    }

    public String ETS(long given) {
        long avgBurstPerToken = getAvgBurstPerToken();

        if (avgBurstPerToken == 0)
            return "N/A";

        if (tokens == null || tokens.size() == 0)
            return TimeUtils.getDuration(avgBurstPerToken);

        List<Long> tokenList = new ArrayList<>(tokens.values());
        if (tokenList.size() == 0) {
            return TimeUtils.getDuration(avgBurstPerToken);
        }

        Collections.sort(tokenList);
        if (tokenList.size() > 0) {
            long ETS = (tokenList.indexOf(given)) * avgBurstPerToken;
            Log.e(TAG, "ETA = " + ETS);
            return TimeUtils.getDuration(ETS);
        }
        return TimeUtils.getDuration(avgBurstPerToken);
    }

    public String getPosition(long given) {
        if (tokens == null || tokens.size() == 0)
            return "";

        List<Long> tokenList = new ArrayList<>(tokens.values());
        if (tokenList.size() == 0) {
            return "";
        }

        Collections.sort(tokenList);
        int index = tokenList.indexOf(given);
        if (index <= 0) {
            return "You are next.";
        } else {
            return "You are number " + index + " in line.";
        }
    }

    public String speed() {
        if (speedIndicator == 0)
            return "N/A";

        return "Min. serving time per person: " + TimeUtils.getDuration(speedIndicator);
    }
}
