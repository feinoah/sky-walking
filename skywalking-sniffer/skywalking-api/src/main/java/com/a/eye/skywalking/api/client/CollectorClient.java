package com.a.eye.skywalking.api.client;

import com.a.eye.skywalking.api.boot.ServiceManager;
import com.a.eye.skywalking.api.conf.Config;
import com.a.eye.skywalking.api.queue.TraceSegmentProcessQueue;
import com.a.eye.skywalking.logging.ILog;
import com.a.eye.skywalking.logging.LogManager;
import com.a.eye.skywalking.trace.SegmentsMessage;
import com.a.eye.skywalking.trace.TraceSegment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * The <code>CollectorClient</code> runs as an independency thread.
 * It retrieves cached {@link TraceSegment} from {@link TraceSegmentProcessQueue},
 * and send to collector by HTTP-RESTFUL-SERVICE: POST /skywalking/trace/segment
 *
 * @author wusheng
 */
public class CollectorClient implements Runnable {
    private static final ILog logger = LogManager.getLogger(CollectorClient.class);
    private static long SLEEP_TIME_MILLIS = 500;
    private String[] serverList;
    private volatile int selectedServer = -1;

    public CollectorClient() {
        serverList = Config.Collector.SERVERS.split(",");
        Random r = new Random();
        if (serverList.length > 0) {
            selectedServer = r.nextInt(serverList.length);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                long sleepTime = -1;
                TraceSegmentProcessQueue segmentProcessQueue = ServiceManager.INSTANCE.findService(TraceSegmentProcessQueue.class);
                List<TraceSegment> cachedTraceSegments = segmentProcessQueue.getCachedTraceSegments();
                if (cachedTraceSegments.size() > 0) {
                    SegmentsMessage message = null;
                    int count = 0;
                    for (TraceSegment segment : cachedTraceSegments) {
                        if (message == null) {
                            message = new SegmentsMessage();
                        }
                        message.append(segment);
                        if (count == Config.Collector.BATCH_SIZE) {
                            sendToCollector(message);
                            message = null;
                        }
                    }
                    sendToCollector(message);
                } else {
                    sleepTime = SLEEP_TIME_MILLIS;
                }

                if (sleepTime > 0) {
                    try2Sleep(sleepTime);
                }
            } catch (Throwable t) {
                logger.error(t, "Send trace segments to collector failure.");
            }
        }
    }

    /**
     * Send the given {@link SegmentsMessage} to collector.
     *
     * @param message to be send.
     */
    private void sendToCollector(SegmentsMessage message) throws RESTResponseStatusError, IOException {
        if (message == null) {
            return;
        }
        Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
        String messageJson = gson.toJson(message);
        CloseableHttpClient httpClient = HttpClients.custom().build();
        try {
            HttpPost httpPost = ready2Send(messageJson);
            if (httpPost != null) {
                CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (200 != statusCode) {
                    findBackupServer();
                    throw new RESTResponseStatusError(statusCode);
                }
            }
        } catch (IOException e) {
            findBackupServer();
            throw e;
        } finally {
            httpClient.close();
        }
    }

    /**
     * Prepare the given message for HTTP Post service.
     *
     * @param messageJson to send
     * @return {@link HttpPost}, when is ready to send. otherwise, null.
     */
    private HttpPost ready2Send(String messageJson) {
        if (selectedServer == -1) {
            //no available server
            return null;
        }
        HttpPost post = new HttpPost("http://" + serverList[selectedServer] + Config.Collector.SERVICE_NAME);
        StringEntity entity = new StringEntity(messageJson, ContentType.APPLICATION_JSON);
        post.setEntity(entity);

        return post;
    }

    /**
     * Choose the next server in {@link #serverList}, by moving {@link #selectedServer}.
     */
    private void findBackupServer() {
        selectedServer++;
        if (selectedServer == serverList.length) {
            selectedServer = 0;
        }
    }

    /**
     * Try to sleep, and ignore the {@link InterruptedException}
     *
     * @param millis the length of time to sleep in milliseconds
     */
    private void try2Sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }
}
