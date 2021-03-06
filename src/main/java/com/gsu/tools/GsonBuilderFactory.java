package com.gsu.tools;

import java.util.Date;

import com.google.gson.*;
import com.gsu.annotations.Simplifiable;
import com.gsu.tools.deserializers.ComplexDeserializer;
import com.gsu.tools.deserializers.NativeDeserializer;
import com.gsu.tools.deserializers.SimpleDeserializer;
import com.gsu.tools.serializers.ComplexSerializer;
import com.gsu.tools.serializers.NativeSerializer;
import com.gsu.tools.serializers.SimpleSerializer;

public class GsonBuilderFactory {

    public static String toJson(Object object){
        return GsonBuilderFactory.getComplexGsonBuilder(object.getClass()).create().toJson(object);
    }
    
    public static GsonBuilder getComplexGsonBuilder(final Class<?> clazz) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(clazz, new ComplexSerializer());
        builder.registerTypeAdapter(clazz, new ComplexDeserializer());
        return builder;
    }

    public static GsonBuilder getSimpleGsonBuilder(Class<?> clazz) {
        GsonBuilder builder = new GsonBuilder();
        if(simplifiable(clazz)){
            builder.registerTypeAdapter(clazz, new SimpleSerializer());
            builder.registerTypeAdapter(clazz, new SimpleDeserializer());
        } else if(!isPrimitive(clazz)){
            builder.registerTypeAdapter(clazz,new NativeSerializer());
            builder.registerTypeAdapter(clazz,new NativeDeserializer());
        }
        return builder;
    }

    private static boolean isPrimitive(Class<?> clazz){
          return  Number.class.isAssignableFrom(clazz)||
                  Boolean.class.isAssignableFrom(clazz)||
                  Character.class.isAssignableFrom(clazz)||
                  String.class.isAssignableFrom(clazz) ||
                  Date.class.isAssignableFrom(clazz);
    }

    private static boolean simplifiable(Class<?> clazz){
        return clazz.isAnnotationPresent(Simplifiable.class);
    }
}
