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
        
        
        builder.registerTypeAdapter(clazz, new JsonSerializer(){
            
            @Override
            public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context){
                JsonObject object = new JsonObject();
                for(Field field : src.getClass().getDeclaredFields()){
                    if (field.isAnnotationPresent(Simplified.class)){
                        //if(field.getType().equals(Collection.class)) {}
                        //else if(field.getType() instanceof Map<?,?>) {} 
                        //else{
                            try{
                                field.setAccessible(true);
                                JsonElement reference = getBasicGsonBuilder(field.getType()).create().toJsonTree(field.get(src));
                                object.add(field.getName(), reference);
                            } catch(Exception e){
                                e.printStackTrace(); 
                            }
                        //}
                    } else if (field.isAnnotationPresent(Primitive.class)){
                        try {
                            field.setAccessible(true);
                            JsonElement id = context.serialize(field.get(src).toString());
                            object.add(field.getName(), id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            field.setAccessible(true);
                            JsonElement element = getGsonBuilder(field.getType()).create().toJsonTree(field.get(src));
                            object.add(field.getName(), element); 
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                }
                return object;
            }
            
        });
        
        return builder;
    }
    
    public static GsonBuilder getBasicGsonBuilder(Class<?> clazz) {
        GsonBuilder builder = new GsonBuilder();
        
        
        builder.registerTypeAdapter(clazz, new JsonSerializer(){
            
            @Override
            public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context){
                JsonObject object = new JsonObject();
                for(Field field : src.getClass().getDeclaredFields()){
                    if (field.isAnnotationPresent(Primitive.class)){
                        try {
                            field.setAccessible(true);
                            JsonElement id = context.serialize(field.get(src).toString());
                            object.add(field.getName(), id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if ( !field.isAnnotationPresent(Simplified.class)){
                        try {
                            field.setAccessible(true);
                            JsonElement element = getBasicGsonBuilder(field.getType()).create().toJsonTree(field.get(src));
                            object.add(field.getName(), element); 
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return object;
            }
            
        });
        
        return builder;
    }
    
    
}
