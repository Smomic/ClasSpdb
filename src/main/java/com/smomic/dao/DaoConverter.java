package com.smomic.dao;

import java.util.*;
import java.util.stream.Collectors;

class DaoConverter {

    static List<String> convertToStringList(List<Map<String, Object>> mapList) {
        return mapList
                .stream()
                .filter(map -> !map.values().isEmpty())
                .map(map -> (String) map.values().iterator().next())
                .collect(Collectors.toList());
    }

    static Map<String, Boolean> convertToMap(List<Map<String, Object>> mapList) {
        return mapList
                .stream()
                .map(map -> new ArrayList<>(map.values()))
                .collect(Collectors.toMap(valueList -> (String) valueList.get(0),
                        valueList -> (Boolean) valueList.get(1), (a, b) -> b));
    }
}
