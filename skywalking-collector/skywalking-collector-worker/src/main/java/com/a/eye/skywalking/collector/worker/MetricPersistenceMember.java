package com.a.eye.skywalking.collector.worker;

import com.a.eye.skywalking.collector.actor.ClusterWorkerContext;
import com.a.eye.skywalking.collector.actor.LocalWorkerContext;
import com.a.eye.skywalking.collector.actor.Role;
import com.a.eye.skywalking.collector.worker.storage.EsClient;
import com.a.eye.skywalking.collector.worker.storage.MetricData;
import com.a.eye.skywalking.collector.worker.storage.MetricPersistenceData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;

import java.util.List;

/**
 * @author pengys5
 */
public abstract class MetricPersistenceMember extends PersistenceMember<MetricPersistenceData> {

    private Logger logger = LogManager.getFormatterLogger(MetricPersistenceMember.class);

    public MetricPersistenceMember(Role role, ClusterWorkerContext clusterContext, LocalWorkerContext selfContext) {
        super(role, clusterContext, selfContext);
    }

    @Override
    public MetricPersistenceData initializeData() {
        return new MetricPersistenceData();
    }

    @Override
    final public void analyse(Object message) throws Exception {
        if (message instanceof MetricData) {
            MetricData metricData = (MetricData) message;
            MetricPersistenceData data = getPersistenceData();
            data.holdData();
            data.getElseCreate(metricData.getId(), false).merge(metricData);
            data.releaseData();
        } else {
            logger.error("message unhandled");
        }
    }

    final protected void persistence() {
        MultiGetResponse multiGetResponse = searchFromEs();
        for (MultiGetItemResponse itemResponse : multiGetResponse) {
            GetResponse response = itemResponse.getResponse();
            if (response != null && response.isExists()) {
                getPersistenceData().getElseCreate(response.getId(), true).merge(response.getSource());
            }
        }
    }

    private MultiGetResponse searchFromEs() {
        Client client = EsClient.INSTANCE.getClient();
        MultiGetRequestBuilder multiGetRequestBuilder = client.prepareMultiGet();

        getPersistenceData().getLast().asMap().forEach((key, value) -> {
            if (!value.isDBValue()) {
                multiGetRequestBuilder.add(esIndex(), esType(), value.getId());
            }
        });
        return multiGetRequestBuilder.get();
    }

    @Override
    void prepareIndex(List<IndexRequestBuilder> builderList) {
        Client client = EsClient.INSTANCE.getClient();
        getPersistenceData().getLast().asMap().forEach((key, value) -> {
            IndexRequestBuilder builder = client.prepareIndex(esIndex(), esType(), key).setSource(value.toMap());
            builderList.add(builder);
        });
        getPersistenceData().getLast().clear();
    }
}
