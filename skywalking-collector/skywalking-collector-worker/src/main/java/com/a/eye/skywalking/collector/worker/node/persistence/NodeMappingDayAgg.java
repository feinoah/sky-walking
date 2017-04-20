package com.a.eye.skywalking.collector.worker.node.persistence;

import com.a.eye.skywalking.collector.actor.*;
import com.a.eye.skywalking.collector.actor.selector.HashCodeSelector;
import com.a.eye.skywalking.collector.actor.selector.WorkerSelector;
import com.a.eye.skywalking.collector.worker.config.WorkerConfig;
import com.a.eye.skywalking.collector.worker.storage.RecordData;

/**
 * @author pengys5
 */
public class NodeMappingDayAgg extends AbstractClusterWorker {

    NodeMappingDayAgg(com.a.eye.skywalking.collector.actor.Role role, ClusterWorkerContext clusterContext, LocalWorkerContext selfContext) {
        super(role, clusterContext, selfContext);
    }

    @Override
    public void preStart() throws ProviderNotFoundException {
        getClusterContext().findProvider(NodeMappingDaySave.Role.INSTANCE).create(this);
    }

    @Override
    protected void onWork(Object message) throws Exception {
        if (message instanceof RecordData) {
            getSelfContext().lookup(NodeMappingDaySave.Role.INSTANCE).tell(message);
        } else {
            throw new IllegalArgumentException("message instance must RecordData");
        }
    }

    public static class Factory extends AbstractClusterWorkerProvider<NodeMappingDayAgg> {
        @Override
        public Role role() {
            return Role.INSTANCE;
        }

        @Override
        public NodeMappingDayAgg workerInstance(ClusterWorkerContext clusterContext) {
            return new NodeMappingDayAgg(role(), clusterContext, new LocalWorkerContext());
        }

        @Override
        public int workerNum() {
            return WorkerConfig.WorkerNum.Node.NodeMappingDayAgg.Value;
        }
    }

    public enum Role implements com.a.eye.skywalking.collector.actor.Role {
        INSTANCE;

        @Override
        public String roleName() {
            return NodeMappingDayAgg.class.getSimpleName();
        }

        @Override
        public WorkerSelector workerSelector() {
            return new HashCodeSelector();
        }
    }
}
