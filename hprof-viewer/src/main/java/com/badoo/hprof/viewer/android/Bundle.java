package com.badoo.hprof.viewer.android;

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Class representing an Android Bundle
 *
 * Created by Erik Andre on 08/12/15.
 */
public class Bundle {

    private final Map<?, Object> map;

    public Bundle(@Nonnull Map<?, Object> map) {
        this.map = map;
    }

    @Nonnull
    public Map<Object, Object> getMap() {
        //noinspection unchecked
        return (Map<Object, Object>) map;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (Object key : map.keySet()) {
            builder.append(key != null? key.toString() : "null");
            builder.append("=");
            Object value = map.get(key);
            builder.append(value != null? value.toString() : "null");
            if (count != map.size() - 1) {
                builder.append(",");
                builder.append("\n");
            }
            count++;
        }
        return builder.toString();
    }
}
