package com.gsu.tools;

import java.util.Date;

public class ReflectionUtils {
    public static boolean isPrimitive(Class clazz){
        return clazz.isPrimitive()||
                 Number.class.isAssignableFrom(clazz)||
                 Boolean.class.isAssignableFrom(clazz)||
                 Character.class.isAssignableFrom(clazz)||
                 String.class.isAssignableFrom(clazz);
    }
}
