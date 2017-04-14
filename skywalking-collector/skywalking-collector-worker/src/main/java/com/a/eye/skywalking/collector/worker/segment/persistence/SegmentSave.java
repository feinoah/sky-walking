package com.a.eye.skywalking.collector.worker.segment.persistence;


import com.a.eye.skywalking.collector.actor.AbstractLocalAsyncWorkerProvider;
import com.a.eye.skywalking.collector.actor.ClusterWorkerContext;
import com.a.eye.skywalking.collector.actor.LocalWorkerContext;
import com.a.eye.skywalking.collector.actor.selector.RollingSelector;
import com.a.eye.skywalking.collector.actor.selector.WorkerSelector;
import com.a.eye.skywalking.collector.worker.RecordPersistenceMember;
import com.a.eye.skywalking.collector.worker.config.CacheSizeConfig;
import com.a.eye.skywalking.collector.worker.config.WorkerConfig;
import com.a.eye.skywalking.collector.worker.segment.SegmentIndex;
import com.a.eye.skywalking.collector.worker.segment.logic.Segment;
import com.a.eye.skywalking.collector.worker.storage.AbstractIndex;
import com.a.eye.skywalking.collector.worker.storage.EsClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author pengys5
 */
public class SegmentSave extends RecordPersistenceMember {

    private Logger logger = LogManager.getFormatterLogger(SegmentSave.class);

    private Map<String, String> persistenceData = new HashMap<>();

    @Override
    public String esIndex() {
        return SegmentIndex.Index;
    }

    @Override
    public String esType() {
        return AbstractIndex.Type_Record;
    }

    public SegmentSave(com.a.eye.skywalking.collector.actor.Role role, ClusterWorkerContext clusterContext, LocalWorkerContext selfContext) {
        super(role, clusterContext, selfContext);
    }

    int i = 0;

    @Override
    public void analyse(Object message) throws Exception {
        if (message instanceof Segment) {
            Segment segment = (Segment) message;
            persistenceData.put(segment.getTraceSegmentId() + i, segment.getJsonStr());
            if (persistenceData.size() >= CacheSizeConfig.Cache.Persistence.size) {
                persistence();
            }
        } else {
            logger.error("unhandled message, message instance must JsonObject, but is %s", message.getClass().toString());
        }
        i++;
    }

    @Override
    protected void persistence() {
        boolean success = saveToEs();
        if (success) {
            persistenceData.clear();
        }
    }

    private boolean saveToEs() {
        Client client = EsClient.INSTANCE.getClient();
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        logger.debug("persistenceData size: %s", persistenceData.size());

        Iterator<Map.Entry<String, String>> iterator = persistenceData.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> recordData = iterator.next();
            logger.debug("saveToEs: key: %s, data: %s", recordData.getKey(), recordData.getValue());
            bulkRequest.add(client.prepareIndex(esIndex(), esType(), recordData.getKey()).setSource(recordData.getValue()));
        }

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            logger.error(bulkResponse.buildFailureMessage());
        }
        return !bulkResponse.hasFailures();
    }

    public static class Factory extends AbstractLocalAsyncWorkerProvider<SegmentSave> {
        public static Factory INSTANCE = new Factory();

        @Override
        public Role role() {
            return Role.INSTANCE;
        }

        @Override
        public int queueSize() {
            return WorkerConfig.Queue.Segment.SegmentSave.Size;
        }

        @Override
        public SegmentSave workerInstance(ClusterWorkerContext clusterContext) {
            return new SegmentSave(role(), clusterContext, new LocalWorkerContext());
        }
    }

    public enum Role implements com.a.eye.skywalking.collector.actor.Role {
        INSTANCE;

        @Override
        public String roleName() {
            return SegmentSave.class.getSimpleName();
        }

        @Override
        public WorkerSelector workerSelector() {
            return new RollingSelector();
        }
    }
}
