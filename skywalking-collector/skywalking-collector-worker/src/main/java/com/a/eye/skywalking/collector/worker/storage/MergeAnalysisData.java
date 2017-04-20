package com.a.eye.skywalking.collector.worker.storage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author pengys5
 */
public class MergeAnalysisData {

    private Data<MergeData> data = new Data(new LinkedHashMap<String, MergeData>());

    public MergeData getElseCreate(String id) {
        if (!data.containsKey(id)) {
            data.put(id, new MergeData(id, false));
        }
        return data.get(id);
    }

    public Map<String, MergeData> asMap() {
        return data.asMap();
    }
}
