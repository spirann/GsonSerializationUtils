package com.gsu.tools.serializers;

import com.google.gson.*;
import com.gsu.annotations.Simple;
import com.gsu.tools.GsonBuilderFactory;
import com.gsu.tools.ReflectionUtilities;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class SimpleSerializer implements JsonSerializer<Object>{

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context){
        JsonObject jsonObject = new JsonObject();
        for(Field field : src.getClass().getDeclaredFields()){
            try {
                field.setAccessible(true);
                Object value =  field.get(src);
                if (value!= null){
                    if(! ReflectionUtilities.isPrimitive(field.getType())){
                        if (!field.isAnnotationPresent(Simple.class)){
                            JsonElement element = GsonBuilderFactory.getSimpleGsonBuilder(field.getType()).create().toJsonTree(value);
                            jsonObject.add(field.getName(), element);
                        }
                    } else {
                        addPrimitive(jsonObject, field, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private void addPrimitive(JsonObject jsonObject, Field field, Object value) throws IllegalAccessException {
            JsonElement primitive;
            if(value instanceof Boolean){
                primitive = new JsonPrimitive((Boolean)value);
            } else if(value instanceof Number){
                primitive = new JsonPrimitive((Number)value);
            } else {
                primitive = new JsonPrimitive(value.toString());
            }
            jsonObject.add(field.getName(), primitive);
    }
}
