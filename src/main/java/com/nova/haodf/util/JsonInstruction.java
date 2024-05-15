package com.nova.haodf.util;

import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

public class JsonInstruction {
    public static final Function<Object, Object> STRING_TO_INTEGER_ADAPTER_FUNCTION
            = val -> val instanceof Integer
            ? val : Integer.parseInt(val.toString().replaceAll(",", ""));
    public static final Function<Object, Object> STRING_TO_LONG_ADAPTER_FUNCTION
            = val -> val instanceof Long
            ? val : Long.parseLong(val.toString().replaceAll(",", ""));
    public static final Function<Object, Object> INTEGER_TO_LONG_ADAPTER_FUNCTION
            = val -> val instanceof Integer
            ? ((Integer) val).longValue() : val;
    public static final Function<Object, Object> BIG_DECIMAL_TO_DOUBLE_ADAPTER_FUNCTION
            = val -> {
        if (val instanceof BigDecimal bigDecimal) {
            return bigDecimal.doubleValue();
        } else if (val instanceof Integer integer) {
            return integer.doubleValue();
        } else {
            return val;
        }
    };
    public static final Function<Object, Object> STRING_TO_LOCAL_DATETIME_ADAPTER_FUNCTION
            = val -> LocalDateTime.parse(val.toString().replace(' ', 'T'));
    public static final Function<Object, Object> STRING_TO_LOCAL_DATE_ADAPTER_FUNCTION
            = val -> LocalDate.parse(val.toString());
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonInstruction.class);
    private static final String DELIMITER = ":";
    private final String path;
    private final Class<?> clazz;
    private final Function<Object, Object> transform;

    public JsonInstruction(String path, Class<?> clazz) {
        this(path, clazz, null);
    }

    public JsonInstruction(String path, Class<?> clazz, Function<Object, Object> transform) {
        this.path = path;
        this.clazz = clazz;
        this.transform = transform;
    }

    public static Object getFieldDataFromJson(JSONObject jsonObject, String path) {
        LOGGER.trace("Current path = {}", path);
        String[] splits = path.split(DELIMITER);
        JSONObject lastObject = jsonObject;
        int splitCount = splits.length;
        Object result = null;
        try {
            for (int i = 0; i < splitCount - 1; ++i) {
                lastObject = lastObject.getJSONObject(splits[i]);
            }
            result = lastObject.get(splits[splitCount - 1]);
        } catch (Exception exception) {
            LOGGER.warn("Failed to get data from json, path = {}", path, exception);
        }
        return result;
    }

    public String getPath() {
        return path;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Function<Object, Object> getTransform() {
        return transform;
    }

    public Object applyTransform(Object fieldData) {
        if (fieldData instanceof String && ((String) fieldData).isEmpty()) {
            return null;
        }
        return fieldData != null && transform != null
                ? transform.apply(fieldData) : fieldData;
    }

    @Override
    public String toString() {
        return "JsonInstruction {" +
                "path='" + path + '\'' +
                ", clazz=" + clazz +
                ", transform=" + transform +
                '}';
    }
}
