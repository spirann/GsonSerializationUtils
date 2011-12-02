package com.gsu.tools.serializers;

import com.google.gson.*;
import com.gsu.annotations.Primitive;
import com.gsu.annotations.Simplified;
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
                    if(! ReflectionUtilities.isPrimitive(field.get(src))){
                        if (field.isAnnotationPresent(Primitive.class)){
                            addPrimitive(jsonObject, field, value);
                        } else if (!field.isAnnotationPresent(Simplified.class)){
                            System.out.println("src : "+src.getClass());
                            System.out.println("normal : "+field.getType());
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
            JsonElement primitive = new JsonPrimitive(value.toString());
            jsonObject.add(field.getName(), primitive);
    }
}
