package com.jtexplorer.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * 实体类操作
 */
@Slf4j
public class EntityUtil {


    /**
     * 使用JSON将父类列表转换为子类列表
     *
     * @param parentList 父类数据列表
     * @param childClass 子类class
     */
    public static <T> List<T> parentListToChildList(List parentList, Class<T> childClass) {
        if(ListUtil.isEmpty(parentList)){
            return null;
        }
        if(childClass == null){
            return null;
        }
        return JsonFormatUtil.stringToArrayList(JsonFormatUtil.objectToString(parentList),childClass);
    }
    /**
     * 使用JSON将父类转换为子类
     *
     * @param parent 父类数据
     * @param childClass 子类class
     */
    public static <T> T parentToChild(Object parent, Class<T> childClass) {
        if(parent == null){
            return null;
        }
        if(childClass == null){
            return null;
        }
        return JsonFormatUtil.stringToObject(JsonFormatUtil.objectToString(parent),childClass);
    }




    /**
     * 使用JSON将子类列表转换为父类列表
     *
     * @param childList 子类数据列表
     * @param parentClass 父类class
     */
    public static <T> List<T> childListToParentList(List childList, Class<T> parentClass) {
        if(ListUtil.isEmpty(childList)){
            return null;
        }
        if(parentClass == null){
            return null;
        }
        return JsonFormatUtil.stringToArrayList(JsonFormatUtil.objectToString(childList),parentClass);
    }

    /**
     * 使用JSON将子类转换为父类
     *
     * @param child 子类数据
     * @param parentClass 父类class
     */
    public static <T> T childToParent(Object child, Class<T> parentClass) {
        if(child == null){
            return null;
        }
        if(parentClass == null){
            return null;
        }
        return JsonFormatUtil.stringToObject(JsonFormatUtil.objectToString(child),parentClass);
    }
}
