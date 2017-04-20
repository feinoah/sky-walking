package com.a.eye.skywalking.collector.worker.segment.entity;

/**
 * @author pengys5
 */
public abstract class DeserializeObject {
    public String jsonStr;

    public String getJsonStr() {
        return jsonStr;
    }

    public void setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }
}
