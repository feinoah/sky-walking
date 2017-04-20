package com.a.eye.skywalking.collector.worker.segment;

import com.a.eye.skywalking.collector.worker.segment.mock.SegmentMock;

import java.util.Date;

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

        String cacheServiceSegmentAsString = mock.mockCacheServiceSegmentAsString();
        String persistenceServiceSegmentAsString = mock.mockPersistenceServiceSegmentAsString();
        String portalServiceSegmentAsString = mock.mockPortalServiceSegmentAsString();

        int count = 1000;
        long startTime = new Date().getTime();
        for (int j = 0; j < count; j++) {
//            HttpClientTools.INSTANCE.post("http://127.0.0.1:12800/segments", cacheServiceSegmentAsString);
//            HttpClientTools.INSTANCE.post("http://127.0.0.1:12800/segments", persistenceServiceSegmentAsString);
            HttpClientTools.INSTANCE.post("http://127.0.0.1:12800/segments", portalServiceSegmentAsString);
        }
        long endTime = new Date().getTime();
        long minus = (endTime - startTime) / 1000;
        System.out.printf("second: %s, segment number: %s \n", minus, count * 1);

//        String specialSegmentAsString = mock.mockSpecialSegmentAsString();
//        HttpClientTools.INSTANCE.post("http://localhost:7001/segments", specialSegmentAsString);

    }
}
