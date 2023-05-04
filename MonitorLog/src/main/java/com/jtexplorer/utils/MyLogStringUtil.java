package com.jtexplorer.utils;

public class MyLogStringUtil {

    public MyLogStringUtil() {
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNotEmpty(Object str) {
        if (str == null) {
            return false;
        } else {
            return !isEmpty(str.toString());
        }
    }

    public static boolean isEmpty(Object str) {
        return str == null ? true : isEmpty(str.toString());
    }

}
