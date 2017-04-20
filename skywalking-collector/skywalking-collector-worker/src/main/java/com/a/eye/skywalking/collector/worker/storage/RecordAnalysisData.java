package com.a.eye.skywalking.collector.worker.storage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author pengys5
 */
public class RecordAnalysisData {

    private Data<RecordData> data = new Data(new LinkedHashMap<String, RecordData>());

    public RecordData getElseCreate(String id) {
        if (!data.containsKey(id)) {
            data.put(id, new RecordData(id, false));
        }
        return data.get(id);
    }

    public Map<String, RecordData> asMap() {
        return data.asMap();
    }
}
