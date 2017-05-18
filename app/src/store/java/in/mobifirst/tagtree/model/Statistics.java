package in.mobifirst.tagtree.model;

import java.util.Map;

/**
 * Use the following JSON structure to get date wise statistics for a prticular service at a particular store.
 * statistics
 * -storeId
 * --serviceId
 * ---dateWiseStats
 * ----Map<date, operationMetrics>
 */
public class Statistics {
    private Map<String, StoreServiceMetrics> dateWiseStats;

    public Statistics(Map<String, StoreServiceMetrics> dateWiseStats) {
        this.dateWiseStats = dateWiseStats;
    }

    public Map<String, StoreServiceMetrics> getDateWiseStats() {
        return dateWiseStats;
    }
}
