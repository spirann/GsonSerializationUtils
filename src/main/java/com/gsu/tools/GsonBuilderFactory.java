package com.gsu.tools;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.google.gson.*;

public class GsonBuilderFactory {

    public static String toJson(Object object){
        return GsonBuilderFactory.getComplexGsonBuilder(object.getClass()).create().toJson(object);
    }
    
    public static GsonBuilder getComplexGsonBuilder(final Class<?> clazz) {
        GsonBuilder builder = new GsonBuilder();
        if(needAdapter(clazz)){
            builder.registerTypeAdapter(clazz, new ComplexSerializer());
        }
        return builder;
    }

    public static GsonBuilder getSimpleGsonBuilder(Class<?> clazz) {
        GsonBuilder builder = new GsonBuilder();
        if(needAdapter(clazz)){
            builder.registerTypeAdapter(clazz, new SimpleSerializer());
        }
        return builder;
    }

    private static boolean needAdapter(Class<?> clazz){
          return !( Number.class.isAssignableFrom(clazz)||
                  Boolean.class.isAssignableFrom(clazz)||
                  Character.class.isAssignableFrom(clazz)||
                  String.class.isAssignableFrom(clazz) ||
                  Date.class.isAssignableFrom(clazz)
          );
    }
}
