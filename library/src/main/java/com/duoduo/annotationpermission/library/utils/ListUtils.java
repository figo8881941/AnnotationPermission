package com.duoduo.annotationpermission.library.utils;

import java.util.List;


public class ListUtils {

    public static String[] stringListToArray(List<String> source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        int size = source.size();
        String[] reulst = source.toArray(new String[size]);
        return reulst;
    }
}
