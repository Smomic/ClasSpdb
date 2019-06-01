package com.smomic.classification;

import com.smomic.dao.TractDao;
import com.smomic.data.DataFormat;
import org.postgis.PGgeometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Relief {

    @Autowired
    TractDao dao;

    private List<DataFormat> trainingData;

    private List<Integer> weightList;

    private Map<String, List<String>> nearestGeomIndexMap;

    public Relief(ApplicationContext context, List<DataFormat> inputData) {
        dao = (TractDao) context.getBean("tractDao");
        this.trainingData = inputData;
        this.weightList = new ArrayList<>();
        this.nearestGeomIndexMap = new HashMap<>();
    }

    public void run() {
        computeNearestGeom();
        initWeights();
        trainingData.forEach(d -> computeWeights(findNearestNeighbours(d), d));

    }

    private void computeNearestGeom() {
        trainingData.forEach(d -> {
            PGgeometry geom = dao.findGeomById(d.getIndex());
            nearestGeomIndexMap.put(d.getIndex(), dao.findIdByDiffrentIdAndOrderByGeom(d.getIndex(), geom));
        });
    }

    private List<Integer> findNearestNeighbours(DataFormat sample) {
        int nearestId;
        int nearestHit = -1;
        int nearestMiss = -1;

        List<String> trainIdList = getTrainingDataId();

        for (String nearest : nearestGeomIndexMap.get(sample.getIndex())) {
            if (trainIdList.contains(nearest)) {
                nearestId = trainIdList.indexOf(nearest);

                if (trainingData.get(nearestId).getValue()
                        .equals(sample.getValue())) {
                    nearestHit = nearestId;
                } else {
                    nearestMiss = nearestId;
                }

                if (nearestHit >= 0 && nearestMiss >= 0)
                    break;
            }
        }
        return Arrays.asList(nearestHit, nearestMiss);
    }

    // Fill weights with zeros
    private void initWeights() {
        int size = trainingData.iterator().next().getPredicateList().size();
        weightList = new ArrayList<>(Collections.nCopies(size, 0));
    }

    private void computeWeights(List<Integer> neighboursList, DataFormat sample) {
        IntStream.range(0, weightList.size() - 1).forEach(i -> {
            if (comparePredicates(i, neighboursList.get(0), sample)) {
                changeWeight(i, 1);
            } else {
                changeWeight(i, -1);
            }

            if (comparePredicates(i, neighboursList.get(1), sample)) {
                changeWeight(i, -1);
            } else {
                changeWeight(i, 1);
            }

        });
    }

    private boolean comparePredicates(int index, int neighbourIndex, DataFormat sample) {
        return trainingData.get(neighbourIndex).getPredicateList().get(index)
                .equals(sample.getPredicateList().get(index));
    }

    private void changeWeight(int index, int value) {
        weightList.set(index, weightList.get(index) + value);
    }

    private List<String> getTrainingDataId() {
        return trainingData
                .stream()
                .map(DataFormat::getIndex)
                .collect(Collectors.toList());
    }

    public List<Integer> getWeights() {
        return weightList;
    }
}
