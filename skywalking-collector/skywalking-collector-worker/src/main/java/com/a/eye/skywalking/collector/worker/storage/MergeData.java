package com.a.eye.skywalking.collector.worker.storage;

import com.a.eye.skywalking.collector.actor.selector.AbstractHashMessage;

import java.util.*;

/**
 * @author pengys5
 */
public class MergeData extends AbstractHashMessage {

    public static final String Split = ",";

    private String id;

    private boolean isDBValue;

    private Map<String, Set<String>> mergeData = new HashMap<>();

    public MergeData(String key, boolean isDBValue) {
        super(key);
        this.id = key;
        this.isDBValue = isDBValue;
    }

    public String getId() {
        return id;
    }

    public boolean isDBValue() {
        return isDBValue;
    }

    public void setMergeData(String column, String value) {
        if (!mergeData.containsKey(column)) {
            mergeData.put(column, new HashSet<>());
        }
        mergeData.get(column).add(value);
    }

    public void merge(MergeData data) {
        for (Map.Entry<String, Set<String>> entry : data.mergeData.entrySet()) {
            String column = entry.getKey();
            Set<String> value = entry.getValue();
            Iterator<String> iterator = value.iterator();
            while (iterator.hasNext()) {
                setMergeData(column, iterator.next());
            }
        }
    }

    public void merge(Map<String, Object> dbData) {
        for (Map.Entry<String, Object> entry : dbData.entrySet()) {
            if (!AbstractIndex.Time_Slice.equals(entry.getKey())
                    && !AbstractIndex.AGG_COLUMN.equals(entry.getKey())) {
                String dbValue = String.valueOf(entry.getValue());
                String[] dbValues = dbValue.split(Split);
                for (String value : dbValues) {
                    setMergeData(entry.getKey(), value);
                }
            }
        }
    }

    public Map<String, String> toMap() {
        Map<String, String> source = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : mergeData.entrySet()) {
            String column = entry.getKey();
            Iterator<String> iterator = entry.getValue().iterator();
            StringBuffer value = new StringBuffer();

            int i = 0;
            while (iterator.hasNext()) {
                if (i == 0) {
                    value.append(iterator.next());
                } else {
                    value.append(Split).append(iterator.next());
                }
                i++;
            }
            source.put(column, value.toString());
        }

        return source;
    }
}
