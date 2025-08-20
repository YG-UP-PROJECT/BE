// src/main/java/com/example/ygup/publicdata/util/JsonUtils.java
package com.example.ygup.publicdata.util;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonUtils {

    public static String text(JsonNode n, String name) {
        return n.hasNonNull(name) ? n.get(name).asText() : null;
    }

    public static Double dbl(JsonNode n, String name) {
        return n.hasNonNull(name) ? n.get(name).asDouble() : null;
    }

    public static Long lng(JsonNode n, String name) {
        return n.hasNonNull(name) ? n.get(name).asLong() : null;
    }

    public static Integer intOrNull(JsonNode n, String name) {
        return n.hasNonNull(name) ? n.get(name).asInt() : null;
    }
}
