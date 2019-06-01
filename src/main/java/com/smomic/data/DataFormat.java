package com.smomic.data;

import java.util.List;
import java.util.stream.Collectors;

public class DataFormat {
    private List<Object> predicateList;
    private String index;
    // binary classification
    private Boolean value;

    DataFormat(List<Object> predicateList, String index, Boolean value) {
        this.predicateList = predicateList;
        this.index = index;
        this.value = value;
    }

    public List<Object> getPredicateList() {
        return predicateList;
    }

    public String getIndex() {
        return index;
    }

    public Boolean getValue() {
        return value;
    }

    void extendPredicateList(List<Object> newPredicateList) {
        this.predicateList.addAll(newPredicateList);
    }

    void replacePredicateList(List<Integer> predicateIndexList) {
        predicateList = predicateIndexList
                .stream()
                .map(index -> predicateList.get(index))
                .collect(Collectors.toList());
    }
}
