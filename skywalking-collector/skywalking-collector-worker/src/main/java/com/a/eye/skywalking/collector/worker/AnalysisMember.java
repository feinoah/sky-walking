package com.a.eye.skywalking.collector.worker;

import com.a.eye.skywalking.collector.actor.*;
import com.a.eye.skywalking.collector.queue.EndOfBatchCommand;
import com.a.eye.skywalking.collector.worker.config.CacheSizeConfig;

/**
 * @author pengys5
 */
public abstract class AnalysisMember extends AbstractLocalAsyncWorker {

    AnalysisMember(Role role, ClusterWorkerContext clusterContext, LocalWorkerContext selfContext) {
        super(role, clusterContext, selfContext);
    }

    public abstract void analyse(Object message) throws Exception;

    @Override
    public void preStart() throws ProviderNotFoundException {
        super.preStart();
    }

    private int messageNum;

    @Override
    final public void onWork(Object message) throws Exception {
        if (message instanceof EndOfBatchCommand) {
            aggregation();
        } else {
            messageNum++;
            try {
                analyse(message);
            } catch (Exception e) {
                saveException(e);
            }

            if (messageNum >= CacheSizeConfig.Cache.Analysis.size) {
                aggregation();
                messageNum = 0;
            }
        }
    }

    protected abstract void aggregation();
}
