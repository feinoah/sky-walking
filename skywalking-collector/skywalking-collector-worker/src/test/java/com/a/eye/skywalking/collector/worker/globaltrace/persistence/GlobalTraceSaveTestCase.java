package com.a.eye.skywalking.collector.worker.globaltrace.persistence;

import com.a.eye.skywalking.collector.actor.ClusterWorkerContext;
import com.a.eye.skywalking.collector.actor.LocalWorkerContext;
import com.a.eye.skywalking.collector.actor.selector.HashCodeSelector;
import com.a.eye.skywalking.collector.worker.globaltrace.GlobalTraceIndex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.TimeZone;

/**
 * @author pengys5
 */
public class GlobalTraceSaveTestCase {

    private GlobalTraceSave save;

    @Before
    public void init() {
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        ClusterWorkerContext cluster = new ClusterWorkerContext(null);
        LocalWorkerContext local = new LocalWorkerContext();
        save = new GlobalTraceSave(GlobalTraceSave.Role.INSTANCE, cluster, local);
    }

    @Test
    public void testEsIndex() {
        Assert.assertEquals(GlobalTraceIndex.Index, save.esIndex());
    }

    @Test
    public void testEsType() {
        Assert.assertEquals(GlobalTraceIndex.Type_Record, save.esType());
    }

    @Test
    public void testRole() {
        Assert.assertEquals(GlobalTraceSave.class.getSimpleName(), GlobalTraceSave.Role.INSTANCE.roleName());
        Assert.assertEquals(HashCodeSelector.class.getSimpleName(), GlobalTraceSave.Role.INSTANCE.workerSelector().getClass().getSimpleName());
    }

    @Test
    public void testFactory() {
        GlobalTraceSave.Factory factory = new GlobalTraceSave.Factory();
        Assert.assertEquals(GlobalTraceSave.class.getSimpleName(), factory.role().roleName());
        Assert.assertEquals(GlobalTraceSave.class.getSimpleName(), factory.workerInstance(null).getClass().getSimpleName());
    }
}
