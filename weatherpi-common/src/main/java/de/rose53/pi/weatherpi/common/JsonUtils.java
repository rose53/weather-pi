package de.rose53.pi.weatherpi.common;

import javax.json.JsonObject;

public class JsonUtils {

    public static boolean has(JsonObject object, String name) {
        if (object == null || name == null) {
            return false;
        }
        return object.containsKey(name) && !object.isNull(name);
    }
}
