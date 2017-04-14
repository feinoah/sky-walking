package com.a.eye.skywalking.collector.worker.segment.logic;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pengys5
 */
public enum SegmentsMessage {
    INSTANCE;

    public List<Segment> readJsonString(String jsonStr) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(jsonStr));
        try {
            return readSegmentArray(reader);
        } finally {
            reader.close();
        }
    }

    private List<Segment> readSegmentArray(JsonReader reader) throws IOException {
        List<Segment> segments = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            Segment segment = new Segment();
            segments.add(segment.deserialize(reader));
        }
        reader.endArray();
        return segments;
    }
}
