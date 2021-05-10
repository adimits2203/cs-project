package com.my.project.dao;

import com.my.project.models.LocationDataSolr;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LocationDataSolrRepo extends SolrCrudRepository<LocationDataSolr, String> {

    List<LocationDataSolr> findAllByCountryAndLastUpdateBetween(String country, Date start, Date end);
    List<LocationDataSolr> findAllByLastUpdateBetween(Date start, Date end);

    List<LocationDataSolr> findAllByLastUpdate(Date asOn);

     List<LocationDataSolr> findAllByCountryAndStateAndLastUpdateBetween(String country, String state, Date start, Date end);
}
