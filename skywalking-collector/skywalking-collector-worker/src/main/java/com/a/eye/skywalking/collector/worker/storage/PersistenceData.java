package com.a.eye.skywalking.collector.worker.storage;

/**
 * @author pengys5
 */
public interface PersistenceData<T> {

    T getElseCreate(String id, boolean isDBValue);

    void releaseData();

    void holdData();
}
