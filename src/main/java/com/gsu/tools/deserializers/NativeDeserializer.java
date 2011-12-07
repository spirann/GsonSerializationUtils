package com.gsu.tools.deserializers;

import com.google.gson.*;

import java.lang.reflect.Type;

public class NativeDeserializer implements JsonDeserializer<Object>{

    @Override
     public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Object dest = null;
        try{
            dest = ((Class)type).getConstructor(String.class).newInstance(jsonElement.getAsJsonPrimitive().getAsString());
        } catch( Exception e) {
            e.printStackTrace();
        }
        return dest;
    }
}
