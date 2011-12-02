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
            System.out.println("**** "+clazz);
            builder.registerTypeAdapter(clazz, new JsonSerializer(){

                @Override
                public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context){
                    JsonObject object = new JsonObject();
                    for(Field field : src.getClass().getDeclaredFields()){
                        System.out.println("type "+field.getType());
                        try{
                            if(! isPrimitive(field)){
                                if (field.isAnnotationPresent(Simplified.class)){
                                    //if(field.getType().equals(Collection.class)) {}
                                    //else if(field.getType() instanceof Map<?,?>) {}
                                    field.setAccessible(true);
                                    JsonElement reference = getBasicGsonBuilder(field.getType()).create().toJsonTree(field.get(src));
                                    object.add(field.getName(), reference);
                                } else if (field.isAnnotationPresent(Primitive.class)){
                                    field.setAccessible(true);
                                    JsonElement id = context.serialize(field.get(src).toString());
                                    object.add(field.getName(), id);
                                } else {
                                    field.setAccessible(true);
                                    JsonElement element = getGsonBuilder(field.getType()).create().toJsonTree(field.get(src));
                                    object.add(field.getName(), element);
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
            System.out.println(clazz);
            builder.registerTypeAdapter(clazz, new JsonSerializer(){

                @Override
                public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context){

                    JsonObject object = new JsonObject();
                    for(Field field : src.getClass().getDeclaredFields()){
                        try {
                            if(! isPrimitive(field.get(src))){
                                if (field.isAnnotationPresent(Primitive.class)){
                                    field.setAccessible(true);
                                    JsonElement id = context.serialize(field.get(src).toString());
                                    object.add(field.getName(), id);
                                } else if ( !field.isAnnotationPresent(Simplified.class)){
                                    field.setAccessible(true);
                                    JsonElement element = getBasicGsonBuilder(field.getType()).create().toJsonTree(field.get(src));
                                    object.add(field.getName(), element);
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

    private static void addPrimitive(Object src, JsonObject object, Field field) {
        try{
            field.setAccessible(true);
            JsonElement primitive = new JsonPrimitive(field.get(src).toString());
            object.add(field.getName(), primitive);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
}
