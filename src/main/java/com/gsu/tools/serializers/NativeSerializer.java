package com.gsu.tools.serializers;

import com.google.gson.*;
import java.lang.reflect.Type;

public class NativeSerializer implements JsonSerializer<Object>{

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context){
        return new JsonPrimitive(src.toString());
    }
}
