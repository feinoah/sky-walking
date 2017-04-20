package com.a.eye.skywalking.collector.worker.storage;

/**
 * @author pengys5
 */
public class MergePersistenceData extends Window<MergeData> implements PersistenceData<MergeData> {

    private Data<MergeData> lockedData;

    public MergeData getElseCreate(String id, boolean isDBValue) {
        if (!lockedData.containsKey(id)) {
            lockedData.put(id, new MergeData(id, isDBValue));
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
