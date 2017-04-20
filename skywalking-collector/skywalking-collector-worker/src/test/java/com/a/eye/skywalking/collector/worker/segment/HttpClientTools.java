package com.a.eye.skywalking.collector.worker.segment;

import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @author pengys5
 */
public enum HttpClientTools {
    INSTANCE;

    private Logger logger = LogManager.getFormatterLogger(HttpClientTools.class);

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    public String post(String url, String data) throws IOException {
        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

//    public String post(String url, String data) throws IOException {
//        HttpURLConnection connection = client.open(new URL(url));
//        OutputStream out = null;
//        InputStream in = null;
//        try {
//            // Write the request.
//            connection.setRequestMethod("POST");
//            out = connection.getOutputStream();
//            out.write(data.getBytes());
//            out.close();
//
//            // Read the response.
//            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                throw new IOException("Unexpected HTTP response: "
//                        + connection.getResponseCode() + " " + connection.getResponseMessage());
//            }
//            in = connection.getInputStream();
//            return readFirstLine(in);
//        } finally {
//            // Clean up.
//            if (out != null) out.close();
//            if (in != null) in.close();
//        }
//    }
//
//    String readFirstLine(InputStream in) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
//        return reader.readLine();
//    }
}
