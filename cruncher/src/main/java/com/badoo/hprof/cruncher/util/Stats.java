package com.badoo.hprof.cruncher.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Helper class for keeping track of stats related to how well Hprof Cruncher works.
 *
 * Created by Erik Andre on 10/05/15.
 */
public class Stats {

    public enum Type {
        TOTAL,
        STRING,
        CLASS,
        INSTANCE,
        ARRAY
    }

    public enum Variant {
        HPROF,
        BMD
    }

    private static final Map<String, Long> sData = new HashMap<String, Long>();
    private static boolean enabled;

    public static void setEnabled(boolean enabled) {
        Stats.enabled = enabled;
    }

    public static void increment(@Nonnull Type type, @Nonnull Variant variant,  long count) {
        if (!enabled) {
            return;
        }
        final String key = getKey(type, variant);
        sData.put(key, getStat(type, variant) + count);
    }

    public static void printStats() {
        if (!enabled) {
            return;
        }
        for (Type type : Type.values()) {
            long hprofSize = getStat(type, Variant.HPROF);
            long bmdSize = getStat(type, Variant.BMD);
            final double ratio = hprofSize > 0? bmdSize / (double) hprofSize : 0;
            System.out.printf("%s size, before: %d, after: %d, ratio: %.3f\n", type, hprofSize, bmdSize, ratio);
        }
    }

    private static long getStat(@Nonnull Type type, @Nonnull Variant variant) {
        final String key = getKey(type, variant);
        if (!sData.containsKey(key)) {
            sData.put(key, 0L);
        }
        return sData.get(key);
    }

    private static String getKey(@Nonnull Type type, @Nonnull Variant variant) {
        return type + "-" + variant;
    }

}
