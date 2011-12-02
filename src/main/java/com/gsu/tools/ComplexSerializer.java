package com.gsu.tools;

import com.google.gson.*;
import com.gsu.annotations.Primitive;
import com.gsu.annotations.Simplified;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class ComplexSerializer implements JsonSerializer<Object> {
    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context){
        JsonObject jsonObject = new JsonObject();
        for(Field field : src.getClass().getDeclaredFields()){
            field.setAccessible(true);
            try{
                if (field.get(src)!= null){
                    Object value = field.get(src);
                    if(!ReflectionUtilities.isPrimitive(value)){
                        if (field.isAnnotationPresent(Simplified.class)){
                            addSimplified(jsonObject,field,value);
                        } else if (field.isAnnotationPresent(Primitive.class)){
                            addPrimitive(jsonObject, field, value);
                        } else {
                            JsonElement element = GsonBuilderFactory.getComplexGsonBuilder(field.getType()).create().toJsonTree(value);
                            jsonObject.add(field.getName(), element);
                        }
                    } else {
                        addPrimitive(jsonObject, field, value);
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private static void addPrimitive(JsonObject jsonObject, Field field, Object value) throws IllegalAccessException {
        JsonElement primitive = new JsonPrimitive(value.toString());
        jsonObject.add(field.getName(), primitive);
    }

    private static void addSimplified(JsonObject jsonObject, Field field, Object value) throws IllegalAccessException {
        if(value.getClass().isArray()) {
            addArray(jsonObject, value, field);
        }else if(Collection.class.isInstance(value)) {
            addCollection(jsonObject, field, (Collection) value);
        }else if(Map.class.isInstance(value)) {
            addMap(jsonObject, field, (Map) value);
        } else {
            JsonElement reference = GsonBuilderFactory.getSimpleGsonBuilder(field.getType()).create().toJsonTree(value);
            jsonObject.add(field.getName(), reference);
        }
    }

    private static void addMap(JsonObject jsonObject, Field field, Map value) {
        JsonArray array = new JsonArray();
        for(Object key: value.keySet()){
            Object mapValue = value.get(key);
            JsonArray entry = new JsonArray();
            JsonElement keyEntry = GsonBuilderFactory.getSimpleGsonBuilder(key.getClass()).create().toJsonTree(key);
            entry.add(keyEntry);
            JsonElement valueEntry = GsonBuilderFactory.getSimpleGsonBuilder(mapValue.getClass()).create().toJsonTree(mapValue);
            entry.add(valueEntry);
            array.add(entry);
        }
        jsonObject.add(field.getName(),array);
    }

    private static void addCollection(JsonObject jsonObject, Field field, Collection value) {
        JsonArray array = new JsonArray();
        for(Object item: value){
            JsonElement reference = GsonBuilderFactory.getSimpleGsonBuilder(item.getClass()).create().toJsonTree(item);
            array.add(reference);
        }
        jsonObject.add(field.getName(),array);
    }

    private static void addArray(JsonObject jsonObject, Object value, Field field) {
        JsonArray array = new JsonArray();
        for(int i =0; i< Array.getLength(value); i++ ){
            Object item = Array.get(value,i);
            JsonElement reference = GsonBuilderFactory.getSimpleGsonBuilder(item.getClass()).create().toJsonTree(item);
            array.add(reference);
        }
        jsonObject.add(field.getName(),array);
    }


}
