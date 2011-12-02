package com.gsu.tools;

import java.util.Date;

public class ReflectionUtilities {
    public static boolean isPrimitive(Object object){
        return object.getClass().isPrimitive()||
                 Number.class.isInstance(object)||
                 Boolean.class.isInstance(object)||
                 Character.class.isInstance(object)||
                 String.class.isInstance(object);
    }
}
