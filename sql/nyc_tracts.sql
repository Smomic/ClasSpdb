CREATE TABLE nyc_tracts AS
SELECT
  SubStr(blkid, 1, 11) AS tractid,
  boroname,
  SUM(popn_total) AS total,
  SUM(popn_asian) AS total_asian,
  SUM(popn_black) AS total_black,
  SUM(popn_nativ) AS total_nativ,
  SUM(popn_other) AS total_other,
  SUM(popn_white) AS total_white,
  ST_Union(geom) AS geom
FROM nyc_census_blocks
GROUP BY tractid, boroname;

CREATE INDEX nyc_tracts_gidx ON nyc_tracts USING GIST (geom);

ALTER TABLE nyc_tracts ADD PRIMARY KEY (tractid);
