package com.smomic.classification;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.util.MLUtils;
import scala.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.smomic.data.Parameters.APP_NAME;

public class Classifier {

    private JavaSparkContext sparkContext;

    private String path;

    private JavaRDD<LabeledPoint> trainingData;

    private JavaRDD<LabeledPoint> testData;

    private int numberOfTest;

    private double classificationError = 0.0;

    private double time = 0;

    private JavaPairRDD<Object, Object> predictionAndLabel;

    public Classifier(String path, int numberOfTest) {
        this.path = path;
        this.numberOfTest = numberOfTest;
        initSpark();
    }

    private void initSpark() {
        turnOffLogger();
        SparkConf sparkConf = new SparkConf().setAppName(APP_NAME.getValue()).setMaster("local");
        sparkContext = new JavaSparkContext(sparkConf);
    }

    private void turnOffLogger() {
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);
    }

    public double computeClassificationError(int depth) {
        classificationError = 0;
        time = 0;
        IntStream.range(0, numberOfTest).forEach(i -> proceedDecisionTree(depth));
        System.out.println("time: " + time / numberOfTest);
        return classificationError / numberOfTest;
    }

    private void proceedDecisionTree(int maxDepth) {
        splitData();
        int numClasses = 2;
        //  Empty categoricalFeaturesInfo indicates all features are continuous.
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        String impurity = "gini";
        int maxBins = 50;

        long start = System.nanoTime();

        DecisionTreeModel model = DecisionTree.trainClassifier(trainingData, numClasses,
                categoricalFeaturesInfo, impurity, maxDepth, maxBins);

        predictionAndLabel = testData.mapToPair(p -> new Tuple2<>(model.predict(p.features()), p.label()));

        long elapsedTime = System.nanoTime() - start;
        time += changeToSecond(elapsedTime);

        classificationError += computeError(predictionAndLabel);
    }

    private void splitData() {
        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sparkContext.sc(), path).toJavaRDD();
        JavaRDD<LabeledPoint>[] splits = data.randomSplit(new double[]{0.9, 0.1});
        trainingData = splits[0];
        testData = splits[1];
    }

    private double computeError(JavaPairRDD<Object, Object> predictionAndLabel) {
        return predictionAndLabel.filter(pl -> !pl._1().equals(pl._2())).count() / (double) testData.count();
    }

    public Double computePrecision() {
        BinaryClassificationMetrics metrics =
                new BinaryClassificationMetrics(predictionAndLabel.rdd());
        List<Tuple2<Object, Object>> precTuple = metrics.precisionByThreshold().toJavaRDD().collect();
        return (Double) precTuple.get(0)._2();
    }

    public Double computeRecall() {
        BinaryClassificationMetrics metrics =
                new BinaryClassificationMetrics(predictionAndLabel.rdd());
        List<Tuple2<Object, Object>> recTuple = metrics.recallByThreshold().toJavaRDD().collect();
        return (Double) recTuple.get(0)._2();
    }

    public void closeContext() {
        sparkContext.close();
    }

    private double changeToSecond(long nano) {
        return (double) nano / 1_000_000_000;
    }
}
