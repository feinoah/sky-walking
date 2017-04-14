package com.a.eye.skywalking.collector.worker.segment;

import org.apache.http.*;
import org.apache.http.config.*;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.conn.DefaultHttpResponseParser;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.util.CharArrayBuffer;

import java.nio.charset.CodingErrorAction;

/**
 * @author pengys5
 */
public class ClientConfiguration {

    public static PoolingHttpClientConnectionManager createPool() throws Exception {
        // Use custom message parser / writer to customize the way HTTP
        // messages are parsed from and written out to the data stream.
        HttpMessageParserFactory<HttpResponse> responseParserFactory = new DefaultHttpResponseParserFactory() {

            @Override
            public HttpMessageParser<HttpResponse> create(
                    SessionInputBuffer buffer, MessageConstraints constraints) {
                LineParser lineParser = new BasicLineParser() {

                    @Override
                    public Header parseHeader(final CharArrayBuffer buffer) {
                        try {
                            return super.parseHeader(buffer);
                        } catch (ParseException ex) {
                            return new BasicHeader(buffer.toString(), null);
                        }
                    }

                };
                return new DefaultHttpResponseParser(
                        buffer, lineParser, DefaultHttpResponseFactory.INSTANCE, constraints) {

                    @Override
                    protected boolean reject(final CharArrayBuffer line, int count) {
                        // try to ignore all garbage preceding a status line infinitely
                        return false;
                    }

                };
            }

        };
        HttpMessageWriterFactory<HttpRequest> requestWriterFactory = new DefaultHttpRequestWriterFactory();

        // Use a custom connection factory to customize the process of
        // initialization of outgoing HTTP connections. Beside standard connection
        // configuration parameters HTTP connection factory can define message
        // parser / writer routines to be employed by individual connections.
        HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(
                requestWriterFactory, responseParserFactory);

        // Create a registry of custom connection socket factories for supported
        // protocol schemes.
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .build();

        // Create a connection manager with custom configuration.
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry, connFactory);

        // Create socket configuration
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .build();
        // Configure the connection manager to use socket configuration either
        // by default or for a specific host.
        connManager.setDefaultSocketConfig(socketConfig);
        connManager.setSocketConfig(new HttpHost("localhost", 80), socketConfig);
        // Validate connections after 1 sec of inactivity
        connManager.setValidateAfterInactivity(1000);

        // Create message constraints
        MessageConstraints messageConstraints = MessageConstraints.custom()
                .setMaxHeaderCount(200)
                .setMaxLineLength(2000)
                .build();
        // Create connection configuration
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .setMessageConstraints(messageConstraints)
                .build();
        // Configure the connection manager to use connection configuration either
        // by default or for a specific host.
        connManager.setDefaultConnectionConfig(connectionConfig);
        connManager.setConnectionConfig(new HttpHost("localhost", 80), ConnectionConfig.DEFAULT);
        return connManager;
    }

}
