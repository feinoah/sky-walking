package com.a.eye.skywalking.collector.worker;

import com.a.eye.skywalking.collector.actor.ClusterWorkerContext;
import com.a.eye.skywalking.collector.actor.LocalWorkerContext;
import com.a.eye.skywalking.collector.actor.Role;
import com.a.eye.skywalking.collector.worker.storage.RecordAnalysisData;
import com.google.gson.JsonObject;

/**
 * @author pengys5
 */
public abstract class RecordAnalysisMember extends AnalysisMember {

    private RecordAnalysisData recordAnalysisData = new RecordAnalysisData();

    public RecordAnalysisMember(Role role, ClusterWorkerContext clusterContext, LocalWorkerContext selfContext) {
        super(role, clusterContext, selfContext);
    }

    final public void setRecord(String id, JsonObject record) throws Exception {
        getRecordAnalysisData().getElseCreate(id).setRecord(record);
    }

    final public RecordAnalysisData getRecordAnalysisData() {
        return recordAnalysisData;
    }
}
