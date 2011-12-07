package com.gsu.tools.deserializers;

import com.google.gson.*;
import com.gsu.annotations.Simple;
import com.gsu.tools.GsonBuilderFactory;
import com.gsu.tools.ReflectionUtilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class SimpleDeserializer implements JsonDeserializer<Object>{
    @Override
    public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Object dest = null;
        try {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Class clazz = ((Class)type);
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            dest = constructor.newInstance();

            for(Field field : dest.getClass().getDeclaredFields()){

                field.setAccessible(true);
                if(! ReflectionUtilities.isPrimitive(field.getType())){
                        if (!field.isAnnotationPresent(Simple.class)){
                            Object object = GsonBuilderFactory.getSimpleGsonBuilder(field.getType()).create().fromJson(jsonObject.get(field.getName()), field.getType());
                            if(object != null){
                                field.set(dest,object);
                            }
                        }
                } else {
                    addPrimitive(jsonObject,field,dest);
                }

            }
        } catch( Exception e) {
            e.printStackTrace();;
        }

        return dest;
    }

    private void addPrimitive(JsonObject jsonObject, Field field, Object dest) throws IllegalAccessException {
        if(jsonObject.get(field.getName()) != null){
            JsonPrimitive primitive = jsonObject.get(field.getName()).getAsJsonPrimitive();
            if(primitive.isBoolean()){
                field.setBoolean(dest,primitive.getAsBoolean());
            } else if (primitive.isNumber()){
                if(Integer.class.isAssignableFrom(field.getType())){
                    field.set(dest, primitive.getAsNumber().intValue());
                } else if(Double.class.isAssignableFrom(field.getType())){
                    field.set(dest, primitive.getAsNumber().doubleValue());
                } else if(Byte.class.isAssignableFrom(field.getType())){
                    field.set(dest, primitive.getAsNumber().byteValue());
                } else if(Float.class.isAssignableFrom(field.getType())){
                    field.set(dest, primitive.getAsNumber().floatValue());
                } else if(Long.class.isAssignableFrom(field.getType())){
                    field.set(dest, primitive.getAsNumber().longValue());
                } else if(Short.class.isAssignableFrom(field.getType())){
                    field.set(dest, primitive.getAsNumber().shortValue());
                }
            } else if (primitive.isString()){
                field.set(dest,primitive.getAsString());
            }
        }
    }
}
