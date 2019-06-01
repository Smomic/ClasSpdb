package com.smomic.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.smomic.dao.DaoConverter.convertToMap;

@Repository
@Qualifier("sociodataDao")
public class SociodataDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Map<String, Object> findById(String id) {
        String sql = "SELECT * FROM nyc_census_sociodata WHERE tractid = ?";
        return jdbcTemplate.queryForList(sql, id).iterator().next();
    }

    public Map<String, Boolean> findIdBySocioAttribute(String attribute) {
        String sql = "SELECT c.tractid, s." + attribute + " >= GREATEST(s.transit_private, s.transit_public, " +
                "s.transit_walk, s.transit_other, s.transit_none) " +
                "FROM nyc_tracts AS c JOIN nyc_census_sociodata AS s ON c.tractid = s.tractid ORDER BY c.tractid;";

        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
        return convertToMap(resultList);
    }

}
