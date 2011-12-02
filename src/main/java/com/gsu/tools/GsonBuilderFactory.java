package com.gsu.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.google.gson.*;
import com.gsu.annotations.Primitive;
import com.gsu.annotations.Simplified;

public class GsonBuilderFactory {

    static {
    }
    
    public static GsonBuilder getGsonBuilder(final Class<?> clazz) {
        GsonBuilder builder = new GsonBuilder();
        if(!isPrimitive(clazz)){
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
                                        //if(field.getType().equals(Collection.class)) {}
                                        //else if(field.getType() instanceof Map<?,?>) {}
                                        JsonElement reference = getBasicGsonBuilder(field.getType()).create().toJsonTree(field.get(src));
                                        object.add(field.getName(), reference);
                                    } else if (field.isAnnotationPresent(Primitive.class)){
                                        addPrimitive(src,object,field);
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
        
        if(!isPrimitive(clazz)){
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
                                        addPrimitive(src,object,field);
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
                 String.class.isInstance(object);
    }

    private static boolean isPrimitive(Class<?> clazz){
          return Number.class.isAssignableFrom(clazz)||
                  Boolean.class.isAssignableFrom(clazz)||
                  String.class.isAssignableFrom(clazz);
    }

    private static void addPrimitive(Object src, JsonObject object, Field field) throws IllegalAccessException {
            JsonElement primitive = new JsonPrimitive(field.get(src).toString());
            object.add(field.getName(), primitive);
    }
    
    
}
