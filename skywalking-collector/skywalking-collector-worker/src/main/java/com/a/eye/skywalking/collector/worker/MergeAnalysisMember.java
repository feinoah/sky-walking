package com.a.eye.skywalking.collector.worker;

import com.a.eye.skywalking.collector.actor.ClusterWorkerContext;
import com.a.eye.skywalking.collector.actor.LocalWorkerContext;
import com.a.eye.skywalking.collector.actor.Role;
import com.a.eye.skywalking.collector.worker.storage.MergeAnalysisData;

/**
 * @author pengys5
 */
public abstract class MergeAnalysisMember extends AnalysisMember {

    private MergeAnalysisData mergeAnalysisData;

    protected MergeAnalysisMember(Role role, ClusterWorkerContext clusterContext, LocalWorkerContext selfContext) {
        super(role, clusterContext, selfContext);
        mergeAnalysisData = new MergeAnalysisData();
    }

    protected MergeAnalysisData getMergeAnalysisData() {
        return mergeAnalysisData;
    }

    final protected void setMergeData(String id, String column, String value) throws Exception {
        getMergeAnalysisData().getElseCreate(id).setMergeData(column, value);
    }
}
