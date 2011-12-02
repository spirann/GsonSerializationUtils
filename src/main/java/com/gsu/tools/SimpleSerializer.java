package com.gsu.tools;

import com.google.gson.*;
import com.gsu.annotations.Primitive;
import com.gsu.annotations.Simplified;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class SimpleSerializer implements JsonSerializer<Object>{

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context){
        JsonObject object = new JsonObject();
        for(Field field : src.getClass().getDeclaredFields()){
            try {
                field.setAccessible(true);
                Object value =  field.get(src);
                if (value!= null){
                    if(! ReflectionUtilities.isPrimitive(field.get(src))){
                        if (field.isAnnotationPresent(Primitive.class)){
                            addPrimitive(object, field, value);
                        } else if (!field.isAnnotationPresent(Simplified.class)){
                            JsonElement element = GsonBuilderFactory.getSimpleGsonBuilder(field.getType()).create().toJsonTree(value);
                            object.add(field.getName(), element);
                        }
                    } else {
                        addPrimitive(object, field, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    private static void addPrimitive(JsonObject object, Field field, Object value) throws IllegalAccessException {
            JsonElement primitive = new JsonPrimitive(value.toString());
            object.add(field.getName(), primitive);
    }
}
