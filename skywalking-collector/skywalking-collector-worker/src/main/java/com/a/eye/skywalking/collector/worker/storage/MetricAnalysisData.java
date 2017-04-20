package com.a.eye.skywalking.collector.worker.storage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author pengys5
 */
public class MetricAnalysisData {

    private Data<MetricData> data = new Data(new LinkedHashMap<String, MetricData>());

    public MetricData getElseCreate(String id) {
        if (!data.containsKey(id)) {
            data.put(id, new MetricData(id, false));
        }
        return data.get(id);
    }

    public Map<String, MetricData> asMap() {
        return data.asMap();
    }
}
