package com.gsu.tools.serializers;

import com.google.gson.*;
import com.gsu.annotations.Primitive;
import com.gsu.annotations.Simplified;
import com.gsu.tools.ReflectionUtilities;

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
                    if(!ReflectionUtilities.isPrimitive(value)){
                        if (field.isAnnotationPresent(Primitive.class)){
                            Primitiver.addPrimitive(jsonObject,field,value);
                        } else if (field.isAnnotationPresent(Simplified.class)){
                            Simplifier.addSimplified(jsonObject, field, value);
                        } else {
                            JsonElement element = context.serialize(value);
                            jsonObject.add(field.getName(), element);
                        }
                    } else {
                        //TODO check if necessary
                        JsonElement primitive = new JsonPrimitive(value.toString());
                        jsonObject.add(field.getName(), primitive);
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
}
