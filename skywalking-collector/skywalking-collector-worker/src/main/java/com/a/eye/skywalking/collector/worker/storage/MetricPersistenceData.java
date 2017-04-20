package com.a.eye.skywalking.collector.worker.storage;

/**
 * @author pengys5
 */
public class MetricPersistenceData extends Window<MetricData> implements PersistenceData<MetricData> {

    private Data<MetricData> lockedData;

    public MetricData getElseCreate(String id, boolean isDBValue) {
        if (!lockedData.containsKey(id)) {
            lockedData.put(id, new MetricData(id, isDBValue));
        }
        return lockedData.get(id);
    }

    public void holdData() {
        lockedData = getCurrentAndHold();
    }

    public void releaseData() {
        lockedData.release();
        lockedData = null;
    }
}
