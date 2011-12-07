package com.gsu.tools.serializers;

import com.google.gson.*;
import com.gsu.annotations.Simple;
import com.gsu.tools.ReflectionUtils;
import com.gsu.tools.Simplifier;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class ComplexSerializer implements JsonSerializer<Object> {

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context){
        JsonObject jsonObject = new JsonObject();
        for(Field field : src.getClass().getDeclaredFields()){
            field.setAccessible(true);
            try{
                if (field.get(src)!= null){
                    Object value = field.get(src);
                    if(!ReflectionUtils.isPrimitive(field.getType())){
                        if (field.isAnnotationPresent(Simple.class)){
                            Simplifier.addSimplified(jsonObject, field, value);
                        } else {
                            JsonElement element = context.serialize(value);
                            jsonObject.add(field.getName(), element);
                        }
                    } else {
                        //TODO check if necessary
                        addPrimitive(jsonObject,field,value);
                    }
                }
            } catch(Exception e){
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
