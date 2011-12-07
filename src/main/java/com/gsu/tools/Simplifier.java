package com.gsu.tools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gsu.tools.GsonBuilderFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class Simplifier {

    public static void addSimplified(JsonObject jsonObject, Field field, Object value) throws IllegalAccessException {
        if (value.getClass().isArray()) {
            addArray(jsonObject, field, value);
        } else if (Collection.class.isInstance(value)) {
            addCollection(jsonObject, field, (Collection) value);
        } else if (Map.class.isInstance(value)) {
            addMap(jsonObject, field, (Map) value);
        } else {
            JsonElement reference = GsonBuilderFactory.getSimpleGsonBuilder(field.getType()).create().toJsonTree(value);
            jsonObject.add(field.getName(), reference);
        }
    }

    public static void getSimplified(JsonElement jsonElement, Field field, Object dest)  throws IllegalAccessException{
        Class type = field.getType();
        if(type.isArray()) {
            getArray(jsonElement, field, dest);
        } else if (Collection.class.isAssignableFrom(type)) {
            getCollection(jsonElement, field, dest);
        } else if (Map.class.isAssignableFrom(type)) {
            getMap(jsonElement, field, dest);
        } else {
            Object object = GsonBuilderFactory.getSimpleGsonBuilder(field.getType()).create().fromJson(jsonElement,field.getType());
            field.set(dest,object);
        }
    }

    public static void addArray(JsonObject jsonObject, Field field, Object value) {
        JsonArray array = new JsonArray();
        for (int i = 0; i < Array.getLength(value); i++) {
            Object item = Array.get(value, i);
            JsonElement reference = GsonBuilderFactory.getSimpleGsonBuilder(item.getClass()).create().toJsonTree(item);
            array.add(reference);
        }
        jsonObject.add(field.getName(), array);
    }

    public static void getArray(JsonElement jsonElement, Field field, Object dest) throws IllegalAccessException {
        JsonArray array = jsonElement.getAsJsonArray();
        Class<?> componentType = field.getType().getComponentType();
        Object object = Array.newInstance(componentType,array.size());

        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            Object item = GsonBuilderFactory.getSimpleGsonBuilder(componentType).create().fromJson(element,componentType);
            Array.set(object,i,item);
        }
        field.set(dest, object);
    }

    public static void addCollection(JsonObject jsonObject, Field field, Collection value) {
        JsonArray array = new JsonArray();
        for (Object item : value) {
            JsonElement reference = GsonBuilderFactory.getSimpleGsonBuilder(item.getClass()).create().toJsonTree(item);
            array.add(reference);
        }
        jsonObject.add(field.getName(), array);
    }

    public static void getCollection(JsonElement jsonElement, Field field, Object dest) throws IllegalAccessException {
        JsonArray array = jsonElement.getAsJsonArray();
        Class componentType = field.getType();
        Class innerType = Object.class;
        if(((Class)componentType.getGenericSuperclass()).isAssignableFrom(ParameterizedType.class)){
           innerType = (Class)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        }
        Collection collection = null;
        try {
            if(componentType.isInterface()){
                //TODO check a better method
                if(componentType.isAssignableFrom(ArrayList.class)){
                    collection = new ArrayList();
                } else if(componentType.isAssignableFrom(HashSet.class)) {
                    collection = new HashSet();
                } else if(componentType.isAssignableFrom(TreeSet.class)) {
                    collection = new TreeSet();
                }

            } else {
                collection = (Collection)componentType.newInstance();
            }

            for (int i = 0; i < array.size(); i++) {
                JsonElement element = array.get(i);
                Object item = GsonBuilderFactory.getSimpleGsonBuilder(innerType).create().fromJson(element,innerType);
                collection.add(item);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        }




        field.set(dest,collection);
    }

    public static void addMap(JsonObject jsonObject, Field field, Map value) {
        JsonArray array = new JsonArray();
        for (Object key : value.keySet()) {
            Object mapValue = value.get(key);
            JsonArray entry = new JsonArray();
            JsonElement keyEntry = GsonBuilderFactory.getSimpleGsonBuilder(key.getClass()).create().toJsonTree(key);
            entry.add(keyEntry);
            JsonElement valueEntry = GsonBuilderFactory.getSimpleGsonBuilder(mapValue.getClass()).create().toJsonTree(mapValue);
            entry.add(valueEntry);
            array.add(entry);
        }
        jsonObject.add(field.getName(), array);
    }

    public static void getMap(JsonElement jsonElement, Field field, Object dest) throws IllegalAccessException {
        Map map = null;
        JsonArray array = jsonElement.getAsJsonArray();
        Class componentType = field.getType();
        Class innerTypeKey = Object.class;
        Class innerTypeValue = Object.class;
        if( field.getGenericType() instanceof ParameterizedType){
            innerTypeKey = (Class)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            innerTypeValue = (Class)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];
        }

        try {
            if(componentType.isInterface()){
                //TODO check a better method
                if(componentType.isAssignableFrom(HashMap.class)){
                    map = HashMap.class.newInstance();
                } else if(componentType.isAssignableFrom(TreeMap.class)) {
                    map = TreeMap.class.newInstance();
                }
            } else {
                map = (Map)componentType.newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < array.size(); i++) {
            JsonArray entry = array.get(i).getAsJsonArray();
            JsonElement keyEntry = entry.get(0);
            JsonElement valueEntry = entry.get(1);
            Object key = GsonBuilderFactory.getSimpleGsonBuilder(innerTypeKey).create().fromJson(keyEntry,innerTypeKey);
            Object value = GsonBuilderFactory.getSimpleGsonBuilder(innerTypeValue).create().fromJson(valueEntry,innerTypeValue);
            map.put(key,value);
        }

        field.set(dest,map);
    }

}