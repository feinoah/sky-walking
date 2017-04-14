package com.a.eye.skywalking.collector.worker.segment;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author pengys5
 */
public enum HttpClientTools {
    INSTANCE;

    private Logger logger = LogManager.getFormatterLogger(HttpClientTools.class);

    public String get(String url, List<NameValuePair> params) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        httpClient.getConnectionManager().closeIdleConnections(5, TimeUnit.SECONDS);
        try {
            HttpGet httpget = new HttpGet(url);
            String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params));
            httpget.setURI(new URI(httpget.getURI().toString() + "?" + paramStr));
            logger.debug("executing get request %s", httpget.getURI());

            try (CloseableHttpResponse response = httpClient.execute(httpget)) {
                HttpEntity entity = response.getEntity();
                System.out.println(response.getStatusLine());
                if (entity != null) {
                    return EntityUtils.toString(entity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();

    public String post(String url, String data) throws IOException {
        HttpURLConnection connection = client.open(new URL(url));
        OutputStream out = null;
        InputStream in = null;
        try {
            // Write the request.
            connection.setRequestMethod("POST");
            out = connection.getOutputStream();
            out.write(data.getBytes());
            out.close();

            // Read the response.
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Unexpected HTTP response: "
                        + connection.getResponseCode() + " " + connection.getResponseMessage());
            }
            in = connection.getInputStream();
            return readFirstLine(in);
        } finally {
            // Clean up.
            if (out != null) out.close();
            if (in != null) in.close();
        }
    }

    String readFirstLine(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        return reader.readLine();
    }
}
