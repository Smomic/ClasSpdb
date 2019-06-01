package com.smomic.execution;

import com.smomic.classification.Classifier;
import com.smomic.classification.Relief;
import com.smomic.config.DatabaseConfig;
import com.smomic.data.DataController;
import com.smomic.data.FileController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.stream.IntStream;

import static com.smomic.data.Parameters.*;

class Controller {

    private ApplicationContext context;

    private Classifier classifier;

    private DataController dataController;

    private FileController fileController;

    private Relief relief;

    private boolean generateData;

    Controller(String classAttribute, boolean generateData, String path, int numberOfTest, int threshold) {
        this.generateData = generateData;
        if (generateData) {
            context = new AnnotationConfigApplicationContext(DatabaseConfig.class);
            dataController = new DataController(context, classAttribute, threshold);
            fileController = new FileController(path);
        }
        if (numberOfTest > 0)
            classifier = new Classifier(path, numberOfTest);
    }

    Controller(String classAttribute, boolean generateData, String path, int numberOfTest) {
        this(classAttribute, generateData, path, numberOfTest, Integer.valueOf(THRESHOLD_VALUE.getValue()));
    }

    Controller(String classAttribute, boolean generateData, String path) {
        this(classAttribute, generateData, path, Integer.valueOf(NUMBER_OF_TESTS.getValue()),
                Integer.valueOf(THRESHOLD_VALUE.getValue()));
    }

    Controller(String classAttribute, boolean generateData, int numberOfTest, int threshold) {
        this(classAttribute, generateData, RESOURCE_PATH.getValue() + classAttribute, numberOfTest, threshold);
    }

    Controller(String classAttribute, boolean generateData, int numberOfTest) {
        this(classAttribute, generateData, RESOURCE_PATH.getValue() + classAttribute, numberOfTest,
                Integer.valueOf(THRESHOLD_VALUE.getValue()));
    }

    Controller(String classAttribute, boolean generateData) {
        this(classAttribute, generateData, RESOURCE_PATH.getValue() + classAttribute,
                Integer.valueOf(NUMBER_OF_TESTS.getValue()), Integer.valueOf(THRESHOLD_VALUE.getValue()));
    }

    void run() throws IOException {
        if (generateData) {
            dataController.prepareData();
            proceedRelief();
            dataController.correctData(relief.getWeights());
            fileController.writeToFile(dataController.getTrainingData());
        }
        proceedClassification();
    }

    private void proceedRelief() {
        relief = new Relief(context, dataController.getTrainingData());
        relief.run();
    }

    private void proceedClassification() {
        IntStream.range(Integer.valueOf(MIN_DEPTH.getValue()), Integer.valueOf(MAX_DEPTH.getValue())).forEach(i -> {
            double classificationError = classifier.computeClassificationError(i);
            System.out.println("Depth: " + i + ", Classification error: "
                    + new DecimalFormat("#0.000").format(classificationError * 100) + "%");
        });
        System.out.println("Precision: " + classifier.computePrecision() * 100 + "%\nRecall: "
                + classifier.computeRecall() * 100 + "%");
        classifier.closeContext();
    }
}
