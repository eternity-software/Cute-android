package ru.etysoft.cute.activities;

import java.lang.reflect.Field;

public class LocaleChanger {
    public static void setRColor(Class rClass, String rFieldName, Object newValue) {
        setR(rClass, "color", rFieldName, newValue);
    }

    public static void setRString(Class rClass, String rFieldName, Object newValue) {
        setR(rClass, "string", rFieldName, newValue);
    }

    // AsciiStrings.STRING_DOLAR = "$";
    public static void setR(Class rClass, String innerClassName, String rFieldName, Object newValue) {
        setStatic(rClass.getName() + "$" + innerClassName, rFieldName, newValue);
    }

    public static boolean setStatic(String aClassName, String staticFieldName, Object toSet) {
        try {
            return setStatic(Class.forName(aClassName), staticFieldName, toSet);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setStatic(Class<?> aClass, String staticFieldName, Object toSet) {
        try {
            Field declaredField = aClass.getDeclaredField(staticFieldName);
            declaredField.setAccessible(true);
            declaredField.set(null, toSet);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
