package com.ardaslegends.data.service.utils;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                    checkBlankString(strField, field.getName());
                }
                catch (IllegalAccessException e) {
                    log.warn("Illegal access for object {} and field {}", obj, field);
                }
            }
        }
    }


    public static <T> void checkAllBlanks(T obj) {
        List<String> stringFields = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> field.getType().equals(String.class))
                .map(Field::getName)
                .collect(Collectors.toList());
        checkBlanks(obj, stringFields);
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

    public static <T> void checkAllNulls(T obj) {
        checkNulls(obj, Arrays.stream(obj.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));
    }

    public static boolean boundLordLeaderPermission(Player player, Army army) {
        log.debug("Checking if bound - lord - leader permission is fulfilled for Army [{}, Faction: {}], Player [{}, Faction:{}]", army.getName(), army.getFaction(), player.getIgn(), player.getFaction());

        if(player.equals(army.getBoundTo())) {
            log.debug("Player [{}] is bound to army, allowed action", player.getIgn());
            return true;
        }

        // TODO: Implement Lordship Permissions

        if(player.equals(army.getFaction().getLeader())) {
            log.debug("Player [{}] is faction leader of army, allowed action", player.getIgn());
            return true;
        }

        log.debug("Player [{}] is not allowed to perform action as per bound - lord - leader permission set!", player.getIgn());
        return false;
    }

    public static void checkBlankString(String value, String fieldName) {
        if(value.isBlank()) {
            log.warn("{} must not be blank!", fieldName);
            throw new IllegalArgumentException("%s must not be blank!".formatted(fieldName));
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
