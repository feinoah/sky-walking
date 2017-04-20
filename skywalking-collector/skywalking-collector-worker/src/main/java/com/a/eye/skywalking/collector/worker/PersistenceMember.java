package com.a.eye.skywalking.collector.worker;

import com.a.eye.skywalking.collector.actor.*;
import com.a.eye.skywalking.collector.worker.storage.FlushAndSwitch;
import com.a.eye.skywalking.collector.worker.storage.Window;
import org.elasticsearch.action.index.IndexRequestBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * @author pengys5
 */
public abstract class PersistenceMember<T extends Window> extends AbstractLocalSyncWorker {

    PersistenceMember(Role role, ClusterWorkerContext clusterContext, LocalWorkerContext selfContext) {
        super(role, clusterContext, selfContext);
        persistenceData = initializeData();
    }

    private T persistenceData;

    public abstract T initializeData();

    protected T getPersistenceData() {
        return persistenceData;
    }

    public abstract String esIndex();

    public abstract String esType();

    public abstract void analyse(Object message) throws Exception;

    @Override
    final public void preStart() throws ProviderNotFoundException {

    }

    @Override
    protected void onWork(Object request, Object response) throws Exception {
        if (request instanceof FlushAndSwitch) {
            persistenceData.switchPointer();
            while (persistenceData.getLast().isHolding()) {
                Thread.sleep(10);
            }

            List<IndexRequestBuilder> builderList = (LinkedList) response;
            prepareIndex(builderList);
        } else {
            analyse(request);
        }
    }

    abstract void prepareIndex(List<IndexRequestBuilder> builderList);
}
