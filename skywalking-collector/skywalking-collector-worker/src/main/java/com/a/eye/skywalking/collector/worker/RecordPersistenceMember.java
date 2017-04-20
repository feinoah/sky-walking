package com.a.eye.skywalking.collector.worker;

import com.a.eye.skywalking.collector.actor.ClusterWorkerContext;
import com.a.eye.skywalking.collector.actor.LocalWorkerContext;
import com.a.eye.skywalking.collector.actor.Role;
import com.a.eye.skywalking.collector.worker.storage.EsClient;
import com.a.eye.skywalking.collector.worker.storage.RecordData;
import com.a.eye.skywalking.collector.worker.storage.RecordPersistenceData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;

import java.util.List;

/**
 * @author pengys5
 */
public abstract class RecordPersistenceMember extends PersistenceMember<RecordPersistenceData> {

    private Logger logger = LogManager.getFormatterLogger(RecordPersistenceMember.class);

    public RecordPersistenceMember(Role role, ClusterWorkerContext clusterContext, LocalWorkerContext selfContext) {
        super(role, clusterContext, selfContext);
    }

    @Override
    final public RecordPersistenceData initializeData() {
        return new RecordPersistenceData();
    }

    @Override
    final public void analyse(Object message) throws Exception {
        if (message instanceof RecordData) {
            RecordData recordData = (RecordData) message;
            logger.debug("setRecord: id: %s, data: %s", recordData.getId(), recordData.getRecord());
            RecordPersistenceData data = getPersistenceData();
            data.holdData();
            data.getElseCreate(recordData.getId(), false).setRecord(recordData.getRecord());
            data.releaseData();
        } else {
            logger.error("message unhandled");
        }
    }

    @Override
    void prepareIndex(List<IndexRequestBuilder> builderList) {
        Client client = EsClient.INSTANCE.getClient();
        getPersistenceData().getLast().asMap().forEach((key, value) -> {
            IndexRequestBuilder builder = client.prepareIndex(esIndex(), esType(), key).setSource(value.getRecord().toString());
            builderList.add(builder);
        });
        getPersistenceData().getLast().clear();
    }
}
