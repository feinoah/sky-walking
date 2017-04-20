package com.a.eye.skywalking.collector.worker.storage;

import java.util.Map;

/**
 * @author pengys5
 */
public class Data<T> {
    private Map<String, T> data;
    private boolean isHold;

    Data(Map<String, T> data) {
        this.data = data;
        this.isHold = false;
    }

    public void release() {
        isHold = false;
    }

    public void hold() {
        isHold = true;
    }

    public boolean isHolding() {
        return isHold;
    }

    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    public void put(String key, T value) {
        data.put(key, value);
    }

    public T get(String key) {
        return data.get(key);
    }

    public int size() {
        return data.size();
    }

    public void clear() {
        data.clear();
    }

    public Map<String, T> asMap() {
        return data;
    }
}
