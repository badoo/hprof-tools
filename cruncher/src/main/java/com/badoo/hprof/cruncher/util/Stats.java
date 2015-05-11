package com.badoo.hprof.cruncher.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for keeping track of stats related to how well Hprof Cruncher works.
 *
 * Created by Erik Andre on 10/05/15.
 */
public class Stats {

    public enum StatType {
        STRING_HPROF,
        STRING_BMD,
        CLASS_HPROF,
        CLASS_BMD,
        INSTANCE_HPROF,
        INSTANCE_BMD,
        ARRAY_HPROF,
        ARRAY_BMD
    }

    private static final Map<StatType, Long> sData = new HashMap<StatType, Long>();

    public static void increment(StatType type, long count) {
        sData.put(type, getStat(type) + count);
    }

    public static long getStat(StatType type) {
        if (!sData.containsKey(type)) {
            sData.put(type, 0L);
        }
        return sData.get(type);
    }

}
