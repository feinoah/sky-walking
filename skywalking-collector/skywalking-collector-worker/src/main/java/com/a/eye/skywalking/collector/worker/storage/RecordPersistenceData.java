package com.a.eye.skywalking.collector.worker.storage;

/**
 * @author pengys5
 */
public class RecordPersistenceData extends Window<RecordData> implements PersistenceData<RecordData> {

    private Data<RecordData> lockedData;

    public RecordData getElseCreate(String id, boolean isDBValue) {
        if (!lockedData.containsKey(id)) {
            lockedData.put(id, new RecordData(id, isDBValue));
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
