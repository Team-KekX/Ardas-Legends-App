package com.ardaslegends.data.service.utils;

import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ServiceUtils {

    public static <T> void checkBlanks(T obj, List<String> fieldNames) {

        List<Field> fields = null;
        fields = getFieldsFromNames(obj, fieldNames);
        for (var field : fields) {
            field.setAccessible(true);
            if(field.getType().equals(String.class)){
                try {
                    String strField = (String) field.get(obj);
                    if(strField.isBlank()) {
                        log.warn("{} must not be blank!", field.getName());
                        throw new IllegalArgumentException("%s must not be blank!".formatted(field.getName()));
                    }
                }
                catch (IllegalAccessException e) {
                    log.warn("Illegal access for object {} and field {}", obj, field);
                }
            }
        }
    }

    public static <T> void checkNulls(T obj, List<String> fieldNames) {
        List<Field> fields = null;
        fields = getFieldsFromNames(obj, fieldNames);
        for (var field : fields) {
            field.setAccessible(true);
            try {
                if(field.get(obj) == null) {
                    log.warn("{} must not be null!", field.getName());
                    throw new NullPointerException("%s must not be null!".formatted(field.getName()));
                }

            }
            catch (IllegalAccessException e) {
                log.warn("Illegal access for object {} and field {}", obj, field);
            }

        }
    }

    private static <T> List<Field> getFieldsFromNames(T obj, List<String> fieldNames) {
        List<Field> fields = new ArrayList<>();
        for (String fieldName : fieldNames) {
            try {
                fields.add(obj.getClass().getDeclaredField(fieldName));
            } catch (NoSuchFieldException e) {
                log.warn("No such field '{}'!", fieldName);
                throw ServiceException.noSuchField(fieldName, e);
            }
        }
        return fields;
    }

}
