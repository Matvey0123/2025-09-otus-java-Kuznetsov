package ru.otus.cachehw;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyCache<K, V> implements HwCache<K, V> {

    private final Map<K, V> cache = new WeakHashMap<>();

    private final List<HwListener<K, V>> listeners = new ArrayList<>();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        runListeners(key, value, "PUT");
    }

    @Override
    public void remove(K key) {
        var value = cache.remove(key);
        runListeners(key, value, "REMOVE");
    }

    @Override
    public V get(K key) {
        var value = cache.get(key);
        runListeners(key, value, "GET");
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void runListeners(K key, V value, String action) {
        try {
            listeners.forEach(l -> l.notify(key, value, action));
        } catch (RuntimeException e) {
            log.error("error running listeners: {}", e.getMessage());
        }
    }
}
