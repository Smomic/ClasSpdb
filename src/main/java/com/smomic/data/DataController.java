package com.smomic.data;

import com.smomic.dao.SociodataDao;
import com.smomic.dao.StreetDao;
import com.smomic.dao.SubwayStationDao;
import com.smomic.dao.TractDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

public class DataController {

    @Autowired
    SociodataDao sociodataDao;

    @Autowired
    StreetDao streetDao;

    @Autowired
    SubwayStationDao subwayStationDao;

    @Autowired
    TractDao tractDao;

    private List<DataFormat> trainingData;

    private Map<String, Boolean> blockMap;

    private String classAttribute;

    private int threshold;

    private static final int CORRECT = 1;

    private static final int INCORRECT = 0;


    public DataController(ApplicationContext context, String classAttribute, int threshold) {
        sociodataDao = (SociodataDao) context.getBean("sociodataDao");
        streetDao = (StreetDao) context.getBean("streetDao");
        subwayStationDao = (SubwayStationDao) context.getBean("subwayStationDao");
        tractDao = (TractDao) context.getBean("tractDao");
        this.threshold = threshold;
        trainingData = new ArrayList<>();
        this.classAttribute = classAttribute;
    }

    public void prepareData() {
        prepareBlocks();
        prepareTrainData();
    }

    private void prepareBlocks() {
        blockMap = sociodataDao.findIdBySocioAttribute(classAttribute);
}

    private void prepareTrainData() {
        for (Map.Entry<String, Boolean> entry : blockMap.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            String mbb = tractDao.findMbbById(key);
            List<Object> predicate = preparePredicates(mbb);
            DataFormat element = new DataFormat(predicate, key, value);
            trainingData.add(element);
        }
    }

    // type has always a value
    private List<Object> preparePredicates(String mbb) {
        List<String> typeList = streetDao.findTypeAndGroupByType();

        List<Object> predicateList = typeList
                .stream()
                .map(type -> streetDao.findIdByTypeAndWithinGeom(type, mbb))
                .map(DataController::getPredicateValue)
                .collect(Collectors.toList());

        List<Map<String, Object>> result = subwayStationDao.findIdByContainsGeom(mbb);
        predicateList.add(getPredicateValue(result));
        return predicateList;
    }

    private static int getPredicateValue(List<Map<String, Object>> result) {
        return (result.isEmpty()) ? INCORRECT
                : CORRECT;
    }

    public void correctData(List<Integer> weightList) {
        correctPredicates(weightList);
        extendPredicates();
    }

    // Remove predicates under threshold value
    private void correctPredicates(List<Integer> weightList) {
        List<Integer> predicateIndexList = weightList
                .stream()
                .filter(weight -> weight > threshold)
                .map(weightList::indexOf)
                .collect(Collectors.toList());
        trainingData.forEach(d -> d.replacePredicateList(predicateIndexList));
        System.out.println(predicateIndexList.size());

    }

    // Add no spacial predicates
    private void extendPredicates() {
        trainingData.forEach(d -> {
            Map<String, Object> track = tractDao.findById(d.getIndex());
            List<Object> trackPredicateList = getPredicateValues(track, getNoSpacialTrackPredicates());

            Map<String, Object> sociodata = sociodataDao.findById(d.getIndex());
            List<Object> sociodataPredicateList = getPredicateValues(sociodata, getNoSpacialSociodataPredicates());

            trackPredicateList.addAll(sociodataPredicateList);
            d.extendPredicateList(trackPredicateList);
        });
    }

    private static List<Object> getPredicateValues(Map<String, Object> sampleMap, List<String> predicateList) {
        return predicateList
                .stream()
                .map(sampleMap::get)
                .collect(Collectors.toList());
    }

    public List<DataFormat> getTrainingData() {
        return trainingData;
    }


    private List<String> getNoSpacialSociodataPredicates() {
        return  Arrays.asList("family_count", "family_income_median", "family_income_mean", "family_income_aggregate",
                "edu_total", "edu_no_highschool_dipl", "edu_highschool_dipl", "edu_college_dipl", "edu_graduate_dipl");
    }

    private List<String> getNoSpacialTrackPredicates() {
        return Arrays.asList("total", "total_asian", "total_black", "total_nativ", "total_other", "total_white");
    }
}