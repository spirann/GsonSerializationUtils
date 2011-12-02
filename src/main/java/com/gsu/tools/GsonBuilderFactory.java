package com.gsu.tools;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.google.gson.*;
import com.gsu.annotations.Primitive;
import com.gsu.annotations.Simplified;

public class GsonBuilderFactory {

    public static String toJson(Object object){
        return GsonBuilderFactory.getGsonBuilder(object.getClass()).create().toJson(object);
    }
    
    public static GsonBuilder getGsonBuilder(final Class<?> clazz) {
        GsonBuilder builder = new GsonBuilder();
        if(needAdapter(clazz)){
            builder.registerTypeAdapter(clazz, new JsonSerializer(){

                @Override
                public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context){
                    JsonObject object = new JsonObject();
                    for(Field field : src.getClass().getDeclaredFields()){
                        field.setAccessible(true);
                        try{
                            if (field.get(src)!= null){
                                if(!isPrimitive(field.get(src))){
                                    if (field.isAnnotationPresent(Simplified.class)){
                                        addSimplified(src,object,field);
                                    } else if (field.isAnnotationPresent(Primitive.class)){
                                        addPrimitive(src, object, field);
                                    } else {
                                        JsonElement element = getGsonBuilder(field.getType()).create().toJsonTree(field.get(src));
                                        object.add(field.getName(), element);
                                    }
                                } else {
                                    addPrimitive(src,object,field);
                                }
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    return object;
                }

            });
        }
        return builder;
    }

    public static GsonBuilder getBasicGsonBuilder(Class<?> clazz) {
        GsonBuilder builder = new GsonBuilder();
        if(needAdapter(clazz)){
            builder.registerTypeAdapter(clazz, new JsonSerializer(){

                @Override
                public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context){
                    JsonObject object = new JsonObject();
                    for(Field field : src.getClass().getDeclaredFields()){
                        try {
                            field.setAccessible(true);
                            if (field.get(src)!= null){
                                if(! isPrimitive(field.get(src))){
                                    if (field.isAnnotationPresent(Primitive.class)){
                                        addPrimitive(src, object, field);
                                    } else if (!field.isAnnotationPresent(Simplified.class)){
                                        JsonElement element = getBasicGsonBuilder(field.getType()).create().toJsonTree(field.get(src));
                                        object.add(field.getName(), element);
                                    }
                                } else {
                                    addPrimitive(src,object,field);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return object;
                }

            });
        }
        return builder;
    }

    private static boolean isPrimitive(Object object){
        return object.getClass().isPrimitive()||
                 Number.class.isInstance(object)||
                 Boolean.class.isInstance(object)||
                 Character.class.isInstance(object)||
                 String.class.isInstance(object);
    }

    private static boolean needAdapter(Class<?> clazz){
          return !( Number.class.isAssignableFrom(clazz)||
                  Boolean.class.isAssignableFrom(clazz)||
                  Character.class.isAssignableFrom(clazz)||
                  String.class.isAssignableFrom(clazz) ||
                  Date.class.isAssignableFrom(clazz)
          );
    }

    private static void addPrimitive(Object src, JsonObject object, Field field) throws IllegalAccessException {
            JsonElement primitive = new JsonPrimitive(field.get(src).toString());
            object.add(field.getName(), primitive);
    }

    private static void addSimplified(Object src, JsonObject object, Field field) throws IllegalAccessException {
            Object value = field.get(src);
            if(value.getClass().isArray()) {
                JsonArray array = new JsonArray();
                for(int i =0; i< Array.getLength(value); i++ ){
                    Object item = Array.get(value,i);
                    JsonElement reference = getBasicGsonBuilder(item.getClass()).create().toJsonTree(item);
                    array.add(reference);
                }
                object.add(field.getName(),array);
            }else if(Collection.class.isInstance(value)) {
                JsonArray array = new JsonArray();
                for(Object item: (Collection)value){
                    JsonElement reference = getBasicGsonBuilder(item.getClass()).create().toJsonTree(item);
                    array.add(reference);
                }
                object.add(field.getName(),array);
            }else if(Map.class.isInstance(value)) {
                JsonArray array = new JsonArray();
                for(Object key: ((Map)value).keySet()){
                    Object mapValue = ((Map)value).get(key);
                    JsonArray entry = new JsonArray();
                    JsonElement keyEntry = getBasicGsonBuilder(key.getClass()).create().toJsonTree(key);
                    entry.add(keyEntry);
                    JsonElement valueEntry = getBasicGsonBuilder(mapValue.getClass()).create().toJsonTree(mapValue);
                    entry.add(valueEntry);
                    array.add(entry);
                }
                object.add(field.getName(),array);
            } else {
                JsonElement reference = getBasicGsonBuilder(field.getType()).create().toJsonTree(value);
                object.add(field.getName(), reference);
            }
    }
    
    
}
