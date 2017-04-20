package com.a.eye.skywalking.collector.worker.noderef.analysis;

import com.a.eye.skywalking.collector.actor.*;
import com.a.eye.skywalking.collector.actor.selector.RollingSelector;
import com.a.eye.skywalking.collector.actor.selector.WorkerSelector;
import com.a.eye.skywalking.collector.worker.config.WorkerConfig;
import com.a.eye.skywalking.collector.worker.noderef.persistence.NodeRefMinuteAgg;
import com.a.eye.skywalking.collector.worker.segment.SegmentPost;
import com.a.eye.skywalking.collector.worker.segment.entity.Segment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author pengys5
 */
public class NodeRefMinuteAnalysis extends AbstractNodeRefAnalysis {

    private Logger logger = LogManager.getFormatterLogger(NodeRefMinuteAnalysis.class);

    protected NodeRefMinuteAnalysis(com.a.eye.skywalking.collector.actor.Role role, ClusterWorkerContext clusterContext, LocalWorkerContext selfContext) {
        super(role, clusterContext, selfContext);
    }

    @Override
    public void preStart() throws ProviderNotFoundException {
        super.preStart();
        getClusterContext().findProvider(NodeRefResSumMinuteAnalysis.Role.INSTANCE).create(this);
    }

    @Override
    public void analyse(Object message) throws Exception {
        if (message instanceof SegmentPost.SegmentWithTimeSlice) {
            SegmentPost.SegmentWithTimeSlice segmentWithTimeSlice = (SegmentPost.SegmentWithTimeSlice) message;
            Segment segment = segmentWithTimeSlice.getSegment();
            long minute = segmentWithTimeSlice.getMinute();
            long hour = segmentWithTimeSlice.getHour();
            long day = segmentWithTimeSlice.getDay();
            int second = segmentWithTimeSlice.getSecond();
            analyseNodeRef(segment, segmentWithTimeSlice.getMinute(), minute, hour, day, second);
        }
    }

    @Override
    protected void sendToResSumAnalysis(AbstractNodeRefResSumAnalysis.NodeRefResRecord refResRecord) throws Exception {
        getSelfContext().lookup(NodeRefResSumMinuteAnalysis.Role.INSTANCE).tell(refResRecord);
    }

    @Override
    protected void aggregation() {
        getRecordAnalysisData().asMap().forEach((key, value) -> {
            try {
                getClusterContext().lookup(NodeRefMinuteAgg.Role.INSTANCE).tell(value);
            } catch (WorkerNotFoundException e) {
                logger.error("The role of %s worker not found", NodeRefMinuteAgg.Role.INSTANCE.roleName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public static class Factory extends AbstractLocalAsyncWorkerProvider<NodeRefMinuteAnalysis> {
        @Override
        public Role role() {
            return Role.INSTANCE;
        }

        @Override
        public NodeRefMinuteAnalysis workerInstance(ClusterWorkerContext clusterContext) {
            return new NodeRefMinuteAnalysis(role(), clusterContext, new LocalWorkerContext());
        }

        @Override
        public int queueSize() {
            return WorkerConfig.Queue.NodeRef.NodeRefMinuteAnalysis.Size;
        }
    }

    public enum Role implements com.a.eye.skywalking.collector.actor.Role {
        INSTANCE;

        @Override
        public String roleName() {
            return NodeRefMinuteAnalysis.class.getSimpleName();
        }

        @Override
        public WorkerSelector workerSelector() {
            return new RollingSelector();
        }
    }
}
