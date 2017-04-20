package com.a.eye.skywalking.collector.worker.segment.entity.tag;

import com.a.eye.skywalking.collector.worker.segment.entity.Span;

/**
 * Do the same thing as {@link StringTag}, just with a {@link Integer} value.
 *
 * Created by wusheng on 2017/2/18.
 */
public class IntTag extends AbstractTag<Integer> {
    public IntTag(String key) {
        super(key);
    }

    /**
     * Get a tag value, type of {@link Integer}.
     * After akka-message/serialize, all tags values are type of {@link String}, convert to {@link Integer}, if necessary.
     *
     * @param span
     * @return tag value
     */
    @Override
    public Integer get(Span span) {
        Integer tagValue = span.getIntTag(super.key);
        if (tagValue == null) {
            return null;
        } else {
            return tagValue;
        }
    }
}
