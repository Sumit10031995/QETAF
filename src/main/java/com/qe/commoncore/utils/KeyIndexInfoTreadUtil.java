package com.qe.commoncore.utils;

import java.util.Map;

public class KeyIndexInfoTreadUtil {
    private KeyIndexInfoTreadUtil() {
    }

    private static final ThreadLocal<Map<String, Integer>> keyIndexInfo = new ThreadLocal<Map<String, Integer>>();

    public static void setKeyIndexInfoDetails(Map<String, Integer> keyDetails) {
        keyIndexInfo.set(keyDetails);
    }

    public static Map<String, Integer> getKeyIndexInfoDetails() {
        return keyIndexInfo.get();
    }

    public static void clear() {
        keyIndexInfo.remove();
    }


}
