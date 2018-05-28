package com.upcacm.zwedit.util;

import com.upcacm.zwedit.ot.TextOperation;


public class JsonUtil {

    public static String dump(TextOperation op) { return "";}

    public static Pair<Integer, TextOperation> load(String rawText) {
        return new Pair(new Integer(0), new TextOperation());
    }
}
