package com.a.eye.skywalking.collector.worker.noderef.analysis;

import com.a.eye.skywalking.collector.actor.ClusterWorkerContext;
import com.a.eye.skywalking.collector.actor.LocalWorkerContext;
import com.a.eye.skywalking.collector.actor.ProviderNotFoundException;
import com.a.eye.skywalking.collector.actor.WorkerRefs;
import com.a.eye.skywalking.collector.actor.selector.RollingSelector;
import com.a.eye.skywalking.collector.worker.config.WorkerConfig;
import com.a.eye.skywalking.collector.worker.mock.RecordDataAnswer;
import com.a.eye.skywalking.collector.worker.noderef.persistence.NodeRefHourAgg;
import com.a.eye.skywalking.collector.worker.storage.RecordData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.TimeZone;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author pengys5
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ClusterWorkerContext.class})
@PowerMockIgnore({"javax.management.*"})
public class NodeRefHourAnalysisTestCase {

    private NodeRefHourAnalysis analysis;
    private RecordDataAnswer answer;
    private ClusterWorkerContext clusterWorkerContext;
    private NodeRefResRecordAnswer recordAnswer;

    @Before
    public void init() throws Exception {
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        clusterWorkerContext = PowerMockito.mock(ClusterWorkerContext.class);
        WorkerRefs workerRefs = mock(WorkerRefs.class);
        answer = new RecordDataAnswer();
        doAnswer(answer).when(workerRefs).tell(Mockito.any(RecordData.class));

        when(clusterWorkerContext.lookup(NodeRefHourAgg.Role.INSTANCE)).thenReturn(workerRefs);
        LocalWorkerContext localWorkerContext = PowerMockito.mock(LocalWorkerContext.class);

        WorkerRefs nodeRefResSumWorkerRefs = mock(WorkerRefs.class);
        recordAnswer = new NodeRefResRecordAnswer();
        doAnswer(recordAnswer).when(nodeRefResSumWorkerRefs).tell(Mockito.any(AbstractNodeRefResSumAnalysis.NodeRefResRecord.class));

        when(localWorkerContext.lookup(NodeRefResSumHourAnalysis.Role.INSTANCE)).thenReturn(nodeRefResSumWorkerRefs);
        analysis = new NodeRefHourAnalysis(NodeRefHourAnalysis.Role.INSTANCE, clusterWorkerContext, localWorkerContext);
    }

    @Test
    public void testRole() {
        Assert.assertEquals(NodeRefHourAnalysis.class.getSimpleName(), NodeRefHourAnalysis.Role.INSTANCE.roleName());
        Assert.assertEquals(RollingSelector.class.getSimpleName(), NodeRefHourAnalysis.Role.INSTANCE.workerSelector().getClass().getSimpleName());
    }

    @Test
    public void testFactory() {
        NodeRefHourAnalysis.Factory factory = new NodeRefHourAnalysis.Factory();
        Assert.assertEquals(NodeRefHourAnalysis.class.getSimpleName(), factory.role().roleName());
        Assert.assertEquals(NodeRefHourAnalysis.class.getSimpleName(), factory.workerInstance(null).getClass().getSimpleName());

        int testSize = 10;
        WorkerConfig.Queue.NodeRef.NodeRefHourAnalysis.Size = testSize;
        Assert.assertEquals(testSize, factory.queueSize());
    }

    String jsonFile = "/json/noderef/analysis/noderef_hour_analysis.json";
    String resSumJsonFile = "/json/noderef/analysis/noderef_ressum_hour_analysis_request.json";

    @Test
    public void testAnalyse() throws Exception {
        NodeRefAnalyse.INSTANCE.analyse(resSumJsonFile, jsonFile, analysis, answer, recordAnswer);
    }

    @Test
    public void testPreStart() throws ProviderNotFoundException {
        Mockito.when(clusterWorkerContext.findProvider(NodeRefResSumHourAnalysis.Role.INSTANCE)).thenReturn(new NodeRefResSumHourAnalysis.Factory());

        ArgumentCaptor<NodeRefResSumHourAnalysis.Role> argumentCaptor = ArgumentCaptor.forClass(NodeRefResSumHourAnalysis.Role.class);
        analysis.preStart();
        verify(clusterWorkerContext).findProvider(argumentCaptor.capture());
    }
}
