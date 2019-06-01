package com.smomic.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.smomic.dao.DaoConverter.convertToStringList;

@Repository
@Qualifier("dao")
public class StreetDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<String> findTypeAndGroupByType() {
        String sql = "SELECT DISTINCT type FROM nyc_streets GROUP BY type;";
        return convertToStringList(jdbcTemplate.queryForList(sql));
    }

    public List<Map<String, Object>> findIdByTypeAndWithinGeom(String type, String mbb) {
        String sql = "SELECT id FROM nyc_streets WHERE type = ? " +
                "AND st_dwithin(st_geomfromewkt(?), geom, 0)";
        return jdbcTemplate.queryForList(sql, type, mbb);
    }

}
