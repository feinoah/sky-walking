package com.a.eye.skywalking.collector.worker.segment.persistence;

import com.a.eye.skywalking.collector.actor.AbstractLocalSyncWorker;
import com.a.eye.skywalking.collector.actor.AbstractLocalSyncWorkerProvider;
import com.a.eye.skywalking.collector.actor.ClusterWorkerContext;
import com.a.eye.skywalking.collector.actor.LocalWorkerContext;
import com.a.eye.skywalking.collector.actor.selector.RollingSelector;
import com.a.eye.skywalking.collector.actor.selector.WorkerSelector;
import com.a.eye.skywalking.collector.worker.config.CacheSizeConfig;
import com.a.eye.skywalking.collector.worker.segment.SegmentIndex;
import com.a.eye.skywalking.collector.worker.segment.entity.Segment;
import com.a.eye.skywalking.collector.worker.storage.AbstractIndex;
import com.a.eye.skywalking.collector.worker.storage.EsClient;
import com.a.eye.skywalking.collector.worker.storage.FlushAndSwitch;
import com.a.eye.skywalking.collector.worker.storage.PersistenceWorkerListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author pengys5
 */
public class SegmentSave extends AbstractLocalSyncWorker {

    private Logger logger = LogManager.getFormatterLogger(SegmentSave.class);

    private Map<String, String> persistenceData = new LinkedHashMap<>();

    public String esIndex() {
        return SegmentIndex.Index;
    }

    public String esType() {
        return AbstractIndex.Type_Record;
    }

    public SegmentSave(com.a.eye.skywalking.collector.actor.Role role, ClusterWorkerContext clusterContext, LocalWorkerContext selfContext) {
        super(role, clusterContext, selfContext);
    }

    int i = 0;

    @Override
    protected void onWork(Object request, Object response) throws Exception {
        if (request instanceof Segment) {
            Segment segment = (Segment) request;
            persistenceData.put(segment.getTraceSegmentId() + i, segment.getJsonStr());
//            if (persistenceData.size() >= CacheSizeConfig.Cache.Persistence.size) {
//                persistence();
//            }
        } else if (request instanceof FlushAndSwitch) {
            persistence();
        } else {
            logger.error("unhandled message, message instance must Segment, but is %s", request.getClass().toString());
        }
        i++;
    }

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

        persistenceData.forEach((key, value) -> {
            bulkRequest.add(client.prepareIndex(esIndex(), esType(), key).setSource(value));
        });

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            logger.error(bulkResponse.buildFailureMessage());
        }
        return !bulkResponse.hasFailures();
    }

    public static class Factory extends AbstractLocalSyncWorkerProvider<SegmentSave> {
        @Override
        public Role role() {
            return Role.INSTANCE;
        }

        @Override
        public SegmentSave workerInstance(ClusterWorkerContext clusterContext) {
            SegmentSave worker = new SegmentSave(role(), clusterContext, new LocalWorkerContext());
            PersistenceWorkerListener.INSTANCE.register(worker);
            return worker;
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
