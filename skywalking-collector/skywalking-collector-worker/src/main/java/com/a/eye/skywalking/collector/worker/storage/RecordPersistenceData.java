package com.a.eye.skywalking.collector.worker.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author pengys5
 */
public class RecordPersistenceData implements Iterable {

    private Map<String, RecordData> persistenceData = new HashMap<>();

    public RecordData getElseCreate(String id) {
        if (!persistenceData.containsKey(id)) {
            persistenceData.put(id, new RecordData(id));
        }
        return persistenceData.get(id);
    }

    public int size() {
        return persistenceData.size();
    }

    public void clear() {
        persistenceData.clear();
    }

    public boolean hasNext() {
        return persistenceData.entrySet().iterator().hasNext();
    }

    public RecordData pushOne() {
        RecordData one = persistenceData.entrySet().iterator().next().getValue();
        persistenceData.remove(one.getId());
        return one;
    }

    @Override
    public void forEach(Consumer action) {
        throw new UnsupportedOperationException("forEach");
    }

    @Override
    public Spliterator spliterator() {
        throw new UnsupportedOperationException("spliterator");
    }

    @Override
    public Iterator<Map.Entry<String, RecordData>> iterator() {
        return persistenceData.entrySet().iterator();
    }
}
