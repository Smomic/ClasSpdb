package com.smomic.data;

public enum Parameters {
    NUMBER_OF_TESTS("10"),
    THRESHOLD_VALUE("200"),
    MIN_DEPTH("4"),
    MAX_DEPTH("8"),
    APP_NAME("ClasSpdb"),
    RESOURCE_PATH("src/main/resources/"),
    HELP("h"),
    CLASS_TYPE("c"),
    GENERATE("g"),
    FILE_PATH("f"),
    NUM_OF_TESTS("n"),
    THRESHOLD("t");

    String value;

    Parameters(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
