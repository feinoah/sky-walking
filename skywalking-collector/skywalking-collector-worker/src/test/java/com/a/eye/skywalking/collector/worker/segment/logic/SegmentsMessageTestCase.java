package com.a.eye.skywalking.collector.worker.segment.logic;

import com.a.eye.skywalking.collector.worker.segment.mock.SegmentMock;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author pengys5
 */
public class SegmentsMessageTestCase {

    @Test
    public void testReadJsonString() throws FileNotFoundException, IOException {
        SegmentMock mock = new SegmentMock();
        String cacheServiceSegmentAsString = mock.mockCacheServiceSegmentAsString();
        List<Segment> segmentList = SegmentsMessage.INSTANCE.readJsonString(cacheServiceSegmentAsString);
        for (Segment segment : segmentList) {
            System.out.println(segment.getJsonStr());
        }
    }
}
