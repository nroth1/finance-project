package cache;

import java.util.*;

import org.apache.commons.lang3.builder.*;

import feeder.*;

/**
 * 
 * @author Conor
 * 
 *         TODO: Implement NamedCache from coherence
 */
public class StockCache extends Cache<String, Set<StockInfo>> {

    /**
     * 
     */
    private static final long serialVersionUID = -4198658223073957347L;

    /**
     * 
     * @return
     */
    public static StockCache newInstance() {
        return new StockCache(new HashMap<String, Set<StockInfo>>());
    }

    /**
     * 
     * @param map
     */
    protected StockCache(Map<String, Set<StockInfo>> map) {
        super(map);
    }

    /**
     * 
     * @param o
     * @return
     */
    public boolean Equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof StockCache)) {
            return false;
        }

        // TODO: Use equals builder
        StockCache temp = (StockCache) o;
        return temp.keySet().containsAll(this.keySet()) && temp.values().containsAll(this.values());
    }

    /**
     * 
     */
    public int hashCode() {
        return new HashCodeBuilder().append(this._cache).toHashCode();
    }

    /**
     * 
     */
    public String toString() {
        String result = "";
        for (String stock : _cache.keySet()) {
            result = result + stock + _cache.get(stock).toString();
        }
        return result;
    }

    public int daysOfTrading() {
        int count = 0;
        for (Set<StockInfo> infoSet : _cache.values()) {
            // System.out.println("infoset size: " + infoSet.size());
            count += infoSet.size();
        }

        return count;
    }

}
