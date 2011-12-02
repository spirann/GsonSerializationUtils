package com.gsu.tools.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class Primitiver {

    public static void addPrimitive(JsonObject jsonObject, Field field, Object value) throws IllegalAccessException {
        if (value.getClass().isArray()) {
            addArray(jsonObject, value, field);
        } else if (Collection.class.isInstance(value)) {
            addCollection(jsonObject, field, (Collection) value);
        } else if (Map.class.isInstance(value)) {
            addMap(jsonObject, field, (Map) value);
        } else {
            JsonElement primitive = new JsonPrimitive(value.toString());
            jsonObject.add(field.getName(), primitive);
        }
    }

    public static void addMap(JsonObject jsonObject, Field field, Map value) {
        JsonArray array = new JsonArray();
        for (Object key : value.keySet()) {
            Object mapValue = value.get(key);
            JsonArray entry = new JsonArray();
            JsonElement keyEntry = new JsonPrimitive(key.toString());
            entry.add(keyEntry);
            JsonElement valueEntry = new JsonPrimitive(mapValue.toString());
            entry.add(valueEntry);
            array.add(entry);
        }
        jsonObject.add(field.getName(), array);
    }

    public static void addCollection(JsonObject jsonObject, Field field, Collection value) {
        JsonArray array = new JsonArray();
        for (Object item : value) {
            JsonElement reference = new JsonPrimitive(item.toString());
            array.add(reference);
        }
        jsonObject.add(field.getName(), array);
    }

    public static void addArray(JsonObject jsonObject, Object value, Field field) {
        JsonArray array = new JsonArray();
        for (int i = 0; i < Array.getLength(value); i++) {
            Object item = Array.get(value, i);
            JsonElement reference = new JsonPrimitive(item.toString());
            array.add(reference);
        }
        jsonObject.add(field.getName(), array);
    }
}
