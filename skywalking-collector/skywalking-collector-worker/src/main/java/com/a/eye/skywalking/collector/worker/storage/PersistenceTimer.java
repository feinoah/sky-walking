package com.a.eye.skywalking.collector.worker.storage;

import com.a.eye.skywalking.collector.actor.AbstractLocalSyncWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;

import java.util.LinkedList;
import java.util.List;

/**
 * @author pengys5
 */
public class PersistenceTimer {

    private Logger logger = LogManager.getFormatterLogger(PersistenceTimer.class);

    public void start() {
        logger.info("persistence timer start");
        final long timeInterval = 10000;
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    bulk();

                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void bulk() {
        Client client = EsClient.INSTANCE.getClient();
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        List<IndexRequestBuilder> dataList = new LinkedList<>();

        List<AbstractLocalSyncWorker> workers = PersistenceWorkerListener.INSTANCE.getWorkers();
        for (AbstractLocalSyncWorker worker : workers) {
            logger.info("worker role name: %s", worker.getRole().roleName());
            try {
                worker.allocateJob(new FlushAndSwitch(), dataList);
            } catch (Exception e) {
            }
        }

        logger.info("bulk data size: %s", dataList.size());
        if (dataList.size() > 0) {
            for (IndexRequestBuilder builder : dataList) {
                bulkRequest.add(builder);
            }

            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                logger.error(bulkResponse.buildFailureMessage());
            }
        }
    }
}
