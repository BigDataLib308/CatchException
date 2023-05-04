package com.jtexplorer.utils;


import java.util.List;


public class ListUtil {
    /**
     * 判断List集合是否是null或空：是，返回true
     *
     * @param list 要判断的List集合
     * @return boolean  是，返回true    否，返回false
     */
    public static boolean isEmpty(List list) {
        return !isNotEmpty(list);
    }

    /**
     * 判断List集合是否是null或空：否，返回true
     *
     * @param list 要判断的List集合
     * @return boolean  否，返回true    是，返回false
     */
    public static boolean isNotEmpty(List list) {
        return list != null && list.size() > 0;
    }

}
