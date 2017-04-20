package com.a.eye.skywalking.collector.worker;

import com.a.eye.skywalking.collector.actor.ClusterWorkerContext;
import com.a.eye.skywalking.collector.actor.LocalWorkerContext;
import com.a.eye.skywalking.collector.actor.Role;
import com.a.eye.skywalking.collector.worker.storage.MetricAnalysisData;

/**
 * @author pengys5
 */
public abstract class MetricAnalysisMember extends AnalysisMember {

    private MetricAnalysisData metricAnalysisData = new MetricAnalysisData();

    public MetricAnalysisMember(Role role, ClusterWorkerContext clusterContext, LocalWorkerContext selfContext) {
        super(role, clusterContext, selfContext);
    }

    final protected void setMetric(String id, String column, Long value) throws Exception {
        getMetricAnalysisData().getElseCreate(id).setMetric(column, value);
    }

    public MetricAnalysisData getMetricAnalysisData() {
        return metricAnalysisData;
    }
}
