package com.smomic.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Qualifier("subwayStationDao")
public class SubwayStationDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findIdByContainsGeom(String geom) {
        String sql = "SELECT id FROM nyc_subway_stations WHERE st_contains(st_geomfromewkt(?), geom);";
        return jdbcTemplate.queryForList(sql, geom);
    }

}
