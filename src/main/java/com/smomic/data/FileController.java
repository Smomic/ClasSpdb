package com.smomic.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileController {

    private String path;

    public FileController(String path) {
        this.path = path;
    }

    public void writeToFile(List<DataFormat> trainData) throws IOException {
        String dataText = convertDataToString(trainData);
        Path path = Paths.get(this.path);
        byte[] strToBytes = dataText.getBytes();

        Files.write(path, strToBytes);
    }

    // Convert to String in the way supported by MLUtils from Spark (LibSVMFile)
    private String convertDataToString(List<DataFormat> trainData) {
        return trainData
                .stream()
                .map(d -> convertBooleanToInteger(d.getValue()) + " " +
                        convertPredicateListToString(d.getPredicateList()) + System.lineSeparator())
                .collect(Collectors.joining());
    }

    private Integer convertBooleanToInteger(Boolean value) {
        return value ? 1 : 0;
    }

    private String convertPredicateListToString(List<Object> predicateList) {
        int index = 1;
        StringBuilder sb = new StringBuilder();
        for (Object o : predicateList) {
            sb.append(index++)
                    .append(":")
                    .append(o)
                    .append(" ");
        }
        return sb.toString();
    }


}
