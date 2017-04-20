package com.a.eye.skywalking.collector.worker.storage;

import com.a.eye.skywalking.collector.actor.selector.AbstractHashMessage;
import com.a.eye.skywalking.collector.worker.Const;
import com.google.gson.JsonObject;

/**
 * @author pengys5
 */
public class RecordData extends AbstractHashMessage {

    private String id;
    private String aggId;
    private boolean isDBValue;
    private JsonObject record;

    public RecordData(String key, boolean isDBValue) {
        super(key);
        this.id = key;
        this.isDBValue = isDBValue;
        String[] ids = id.split(Const.IDS_SPLIT);
        for (int i = 1; i < ids.length; i++) {
            if (i == 1) {
                this.aggId = ids[i];
            } else {
                this.aggId = this.aggId + Const.ID_SPLIT + ids[i];
            }
        }
        record = new JsonObject();
    }

    public String getId() {
        return id;
    }

    public boolean isDBValue() {
        return isDBValue;
    }

    public JsonObject getRecord() {
        record.addProperty(AbstractIndex.AGG_COLUMN, this.aggId);
        return record;
    }

    public void setRecord(JsonObject record) {
        this.record = record;
    }
}
