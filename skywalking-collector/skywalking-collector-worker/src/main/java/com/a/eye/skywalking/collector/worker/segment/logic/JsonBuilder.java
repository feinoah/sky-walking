package com.a.eye.skywalking.collector.worker.segment.logic;

import java.util.List;
import java.util.Map;

/**
 * @author pengys5
 */
public enum JsonBuilder {
    INSTANCE;

    public void append(StringBuilder builder, String name, String value, boolean first) {
        if (!first) {
            builder.append(",");
        }
        builder.append("\"").append(name).append("\":\"").append(value).append("\"");
    }

    public void append(StringBuilder builder, String name, Number value, boolean first) {
        if (!first) {
            builder.append(",");
        }
        builder.append("\"").append(name).append("\":").append(value);
    }

    public void append(StringBuilder builder, String name, List<?> value, boolean first) {
        if (!first) {
            builder.append(",");
        }
        builder.append("\"").append(name).append("\":");
        builder.append("[");

        boolean isFirst = true;
        for (int i = 0; i < value.size(); i++) {
            DeserializeObject deserializeObject = (DeserializeObject) value.get(i);
            if (!isFirst) {
                builder.append(",");
            }
            builder.append(deserializeObject.getJsonStr());
            isFirst = false;
        }

        builder.append("]");
    }

    public void append(StringBuilder builder, String name, Map<String, String> tagsWithStr, boolean first) {

    }
}
