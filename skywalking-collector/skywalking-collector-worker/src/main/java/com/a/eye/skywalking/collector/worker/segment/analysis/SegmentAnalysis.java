package com.a.eye.skywalking.collector.worker.segment.analysis;

import com.a.eye.skywalking.collector.actor.AbstractLocalAsyncWorkerProvider;
import com.a.eye.skywalking.collector.actor.ClusterWorkerContext;
import com.a.eye.skywalking.collector.actor.LocalWorkerContext;
import com.a.eye.skywalking.collector.actor.ProviderNotFoundException;
import com.a.eye.skywalking.collector.actor.selector.RollingSelector;
import com.a.eye.skywalking.collector.actor.selector.WorkerSelector;
import com.a.eye.skywalking.collector.worker.RecordAnalysisMember;
import com.a.eye.skywalking.collector.worker.config.WorkerConfig;
import com.a.eye.skywalking.collector.worker.segment.entity.Segment;
import com.a.eye.skywalking.collector.worker.segment.persistence.SegmentSave;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author pengys5
 */
public class SegmentAnalysis extends RecordAnalysisMember {

    private Logger logger = LogManager.getFormatterLogger(SegmentAnalysis.class);

    SegmentAnalysis(Role role, ClusterWorkerContext clusterContext, LocalWorkerContext selfContext) {
        super(role, clusterContext, selfContext);
    }

    @Override
    public void preStart() throws ProviderNotFoundException {
        getClusterContext().findProvider(SegmentSave.Role.INSTANCE).create(this);
    }

    @Override
    public void analyse(Object message) throws Exception {
        if (message instanceof Segment) {
            Segment segment = (Segment) message;
            getSelfContext().lookup(SegmentSave.Role.INSTANCE).tell(segment);
        } else {
            logger.error("unhandled message, message instance must JsonObject, but is %s", message.getClass().toString());
        }
    }

    @Override
    protected void aggregation() {
    }

    public static class Factory extends AbstractLocalAsyncWorkerProvider<SegmentAnalysis> {
        @Override
        public Role role() {
            return SegmentAnalysis.Role.INSTANCE;
        }

        @Override
        public SegmentAnalysis workerInstance(ClusterWorkerContext clusterContext) {
            return new SegmentAnalysis(role(), clusterContext, new LocalWorkerContext());
        }

        @Override
        public int queueSize() {
            return WorkerConfig.Queue.Segment.SegmentAnalysis.Size;
        }
    }

    public enum Role implements com.a.eye.skywalking.collector.actor.Role {
        INSTANCE;

        @Override
        public String roleName() {
            return SegmentAnalysis.class.getSimpleName();
        }

        @Override
        public WorkerSelector workerSelector() {
            return new RollingSelector();
        }
    }
}
