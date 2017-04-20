package com.a.eye.skywalking.collector.worker.noderef.persistence;

import com.a.eye.skywalking.collector.actor.ClusterWorkerContext;
import com.a.eye.skywalking.collector.actor.LocalWorkerContext;
import com.a.eye.skywalking.collector.actor.selector.HashCodeSelector;
import com.a.eye.skywalking.collector.worker.config.WorkerConfig;
import com.a.eye.skywalking.collector.worker.noderef.NodeRefIndex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.TimeZone;

/**
 * @author pengys5
 */
public class NodeRefHourSaveTestCase {

    private NodeRefHourSave save;

    @Before
    public void init() {
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        ClusterWorkerContext cluster = new ClusterWorkerContext(null);
        LocalWorkerContext local = new LocalWorkerContext();
        save = new NodeRefHourSave(NodeRefHourSave.Role.INSTANCE, cluster, local);
    }

    @Test
    public void testEsIndex() {
        Assert.assertEquals(NodeRefIndex.Index, save.esIndex());
    }

    @Test
    public void testEsType() {
        Assert.assertEquals(NodeRefIndex.Type_Hour, save.esType());
    }

    @Test
    public void testRole() {
        Assert.assertEquals(NodeRefHourSave.class.getSimpleName(), NodeRefHourSave.Role.INSTANCE.roleName());
        Assert.assertEquals(HashCodeSelector.class.getSimpleName(), NodeRefHourSave.Role.INSTANCE.workerSelector().getClass().getSimpleName());
    }

    @Test
    public void testFactory() {
        NodeRefHourSave.Factory factory = new NodeRefHourSave.Factory();
        Assert.assertEquals(NodeRefHourSave.class.getSimpleName(), factory.role().roleName());
        Assert.assertEquals(NodeRefHourSave.class.getSimpleName(), factory.workerInstance(null).getClass().getSimpleName());
    }
}
