package cache;
import java.util.*;

import com.google.gson.*;

/**
 * 
 * @author Conor
 *
 */
public class JsonCache extends Cache<String, JsonObject> {

    /**
     * 
     * @param map
     */
    protected JsonCache(Map<String, JsonObject> map) {
        super(map);
    }

}
