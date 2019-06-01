package com.smomic.dao;

import org.postgis.PGgeometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.smomic.dao.DaoConverter.convertToStringList;

@Repository
@Qualifier("tractDao")
public class TractDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public String findMbbById(String id) {
        String sql = "SELECT st_asewkt(st_envelope(geom)) FROM nyc_tracts WHERE tractid = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, String.class);
    }

    public PGgeometry findGeomById(String id) {
        String sql = "SELECT geom FROM nyc_tracts WHERE tractid = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, PGgeometry.class);
    }

    public List<String> findIdByDiffrentIdAndOrderByGeom(String id, PGgeometry geom) {
        String sql = "SELECT tractid FROM nyc_tracts WHERE tractid != ? ORDER BY geom <-> (?)";
        return convertToStringList(jdbcTemplate.queryForList(sql, id, geom));
    }

    public Map<String, Object> findById(String id) {
        String sql = "SELECT * FROM nyc_tracts WHERE tractid = ?";
        return jdbcTemplate.queryForList(sql, id).iterator().next();
    }

}
