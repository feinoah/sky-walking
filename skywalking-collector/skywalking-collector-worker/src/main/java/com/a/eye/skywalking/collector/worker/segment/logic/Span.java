package com.a.eye.skywalking.collector.worker.segment.logic;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pengys5
 */
public class Span extends DeserializeObject {
    private int spanId;
    private int parentSpanId;
    private long startTime;
    private long endTime;
    private String operationName;
    private Map<String, String> tagsWithStr;
    private Map<String, Boolean> tagsWithBool;
    private Map<String, Integer> tagsWithInt;

    public int getSpanId() {
        return spanId;
    }

    public int getParentSpanId() {
        return parentSpanId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getOperationName() {
        return operationName;
    }

    public Span deserialize(JsonReader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");

        boolean first = true;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("si")) {
                Integer si = reader.nextInt();
                this.spanId = si;
                JsonBuilder.INSTANCE.append(stringBuilder, "si", si, first);
            } else if (name.equals("si")) {
                Integer ps = reader.nextInt();
                this.parentSpanId = ps;
                JsonBuilder.INSTANCE.append(stringBuilder, "ps", ps, first);
            } else if (name.equals("st")) {
                Long st = reader.nextLong();
                this.startTime = st;
                JsonBuilder.INSTANCE.append(stringBuilder, "st", st, first);
            } else if (name.equals("et")) {
                Long et = reader.nextLong();
                this.endTime = et;
                JsonBuilder.INSTANCE.append(stringBuilder, "et", et, first);
            } else if (name.equals("on")) {
                String on = reader.nextString();
                this.operationName = on;
                JsonBuilder.INSTANCE.append(stringBuilder, "on", on, first);
            } else if (name.equals("ts")) {
                tagsWithStr = new HashMap<>();
                reader.beginArray();

                while (reader.hasNext()) {
                    String key = reader.nextName();
                    String value = reader.nextString();
                    tagsWithStr.put(key, value);
                }
                reader.endArray();
                JsonBuilder.INSTANCE.append(stringBuilder, "on", tagsWithStr, first);
            } else {
                reader.skipValue();
            }
            first = false;
        }
        reader.endObject();

        stringBuilder.append("}");
        this.jsonStr = stringBuilder.toString();
        return this;
    }
}
