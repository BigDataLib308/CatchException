package com.jtexplorer.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class JsonFormatUtil {
    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static class SingletonHolder {
        private SingletonHolder() {
        }

        private final static Gson INSTANCE = new GsonBuilder().serializeNulls().disableHtmlEscaping().setDateFormat(DATE_FORMAT).create();
    }

    private static Gson getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 格式化
     *
     * @param jsonStr
     * @return
     * @author lizhgb
     * @Date 2015-10-14 下午1:17:35
     * @Modified 2017-04-28 下午8:55:35
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        boolean isInQuotationMarks = false;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '"':
                    if (last != '\\') {
                        isInQuotationMarks = !isInQuotationMarks;
                    }
                    sb.append(current);
                    break;
                case '{':
                case '[':
                    sb.append(current);
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent++;
                        addIndentBlank(sb, indent);
                    }
                    break;
                case '}':
                case ']':
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent--;
                        addIndentBlank(sb, indent);
                    }
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\' && !isInQuotationMarks) {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    /**
     * 添加space
     *
     * @param sb
     * @param indent
     * @author lizhgb
     * @Date 2015-10-14 上午10:38:04
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }


    public static String objectToString(Object o) {
        return gson.toJson(o);
    }

    public static <T> T stringToObject(String str, Class<T> classOfT) {
        return gson.fromJson(str, classOfT);
    }

    public static <T> List<T> stringToArrayList(String str, Class<T> classOfT) {
        T[] ts = getInstance().fromJson(str, TypeToken.getArray(classOfT).getType());
        List<T> list = new ArrayList<>();
        if(ts != null && ts.length > 0){
            for(T t : ts){
                list.add(t);
            }
        }

        return ts == null ? null : list;
    }





}