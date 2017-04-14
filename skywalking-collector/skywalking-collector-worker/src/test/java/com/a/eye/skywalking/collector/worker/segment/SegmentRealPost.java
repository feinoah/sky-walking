package com.a.eye.skywalking.collector.worker.segment;

import com.a.eye.skywalking.collector.worker.segment.logic.Segment;
import com.a.eye.skywalking.collector.worker.segment.logic.SegmentsMessage;
import com.a.eye.skywalking.collector.worker.segment.mock.SegmentMock;

import java.util.Date;
import java.util.List;

/**
 * @author pengys5
 */
public class SegmentRealPost {

    public static void main(String[] args) throws Exception {
        SegmentMock mock = new SegmentMock();
//        String cacheServiceExceptionSegmentAsString = mock.mockCacheServiceExceptionSegmentAsString();
//        HttpClientTools.INSTANCE.post("http://localhost:7001/segments", cacheServiceExceptionSegmentAsString);
//
//        String portalServiceExceptionSegmentAsString = mock.mockPortalServiceExceptionSegmentAsString();
//        HttpClientTools.INSTANCE.post("http://localhost:7001/segments", portalServiceExceptionSegmentAsString);


//        Thread.sleep(10000);

        long startTime = new Date().getTime();
        int i = 0;

        String cacheServiceSegmentAsString = mock.mockCacheServiceSegmentAsString();
        String persistenceServiceSegmentAsString = mock.mockPersistenceServiceSegmentAsString();
        String portalServiceSegmentAsString = mock.mockPortalServiceSegmentAsString();

        while (true) {
            HttpClientTools.INSTANCE.post("http://localhost:12800/segments", cacheServiceSegmentAsString);
            HttpClientTools.INSTANCE.post("http://localhost:12800/segments", persistenceServiceSegmentAsString);
            HttpClientTools.INSTANCE.post("http://localhost:12800/segments", portalServiceSegmentAsString);

            i++;

            long endTime = new Date().getTime();
            long minus = (endTime - startTime) / 1000;

            System.out.printf("second: %s, segment number: %s \n", minus, i * 4);
            if (i % 1000 == 0) {
                Thread.sleep(100);
            }
        }

//        String specialSegmentAsString = mock.mockSpecialSegmentAsString();
//        HttpClientTools.INSTANCE.post("http://localhost:7001/segments", specialSegmentAsString);

    }
}
