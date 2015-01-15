package cache;
import java.util.*;

/**
 * 
 * @author Conor
 * 
 *         TODO: Rename this class to better reflect its actual purpose
 *
 * @param <K>
 *            Key
 * @param <V>
 *            Value
 */
public class Cache<K, V> implements Map<K, V> {

    Map<K, V> _cache;

    public static <K, V> Cache<K, V> newInstance() {
        return new Cache(new HashMap<K, V>());
    }

    protected Cache(Map<K, V> map) {
        _cache = new HashMap<K, V>();
    }

    @Override
    public void clear() {
        _cache.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return _cache.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return _cache.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return _cache.entrySet();
    }

    @Override
    public V get(Object arg0) {
        return _cache.get(arg0);
    }

    @Override
    public boolean isEmpty() {
        return _cache.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return _cache.keySet();
    }

    @Override
    public V put(K key, V value) {
        return _cache.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        _cache.putAll(map);
    }

    @Override
    public V remove(Object key) {
        return _cache.remove(key);
    }

    @Override
    public int size() {
        return _cache.size();
    }

    @Override
    public Collection<V> values() {
        return _cache.values();
    }
}
