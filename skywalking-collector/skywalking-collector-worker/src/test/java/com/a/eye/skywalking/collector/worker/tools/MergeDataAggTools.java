package com.a.eye.skywalking.collector.worker.tools;

import com.a.eye.skywalking.collector.actor.AbstractClusterWorker;
import com.a.eye.skywalking.collector.worker.Const;
import com.a.eye.skywalking.collector.worker.mock.MergeDataAnswer;
import com.a.eye.skywalking.collector.worker.storage.MergeData;
import org.junit.Assert;

/**
 * @author pengys5
 */
public enum MergeDataAggTools {
    INSTANCE;

    public void testOnWork(AbstractClusterWorker agg, MergeDataAnswer mergeDataAnswer) throws Exception {
        String id = "2016" + Const.ID_SPLIT + "A" + Const.ID_SPLIT + "B";
        MergeData mergeData = new MergeData(id, false);
        mergeData.setMergeData("Column", "Value");
        agg.allocateJob(mergeData);
        Assert.assertEquals("Value", mergeDataAnswer.getMergeDataList().get(0).toMap().get("Column"));
    }
}
