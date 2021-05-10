package com.my.project.dao;

import com.my.project.models.DashBoardResponseRange;
import com.my.project.models.LocationDataSolr;
import com.my.project.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.common.params.MapSolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class SolrClientService {

    @Value("${solr.url}")
    String solrUrl = "http://localhost:8983/solr";

    @Value("${solr.collection}")
    String collection = "locationdatasolr";

    @Autowired
    LocationDataSolrRepo repo;


    private SolrClient getSolrClient(){
        return new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
    }

    final SolrClient client = getSolrClient();

    public List<LocationDataSolr> search(Double lat, Double lon, Double radius) {
        try {
            final Map<String, String> queryParamMap = new HashMap<>();
            queryParamMap.put("q", "*:*");
            queryParamMap.put("d", radius+"");
            queryParamMap.put("pt", lat+","+lon);
            queryParamMap.put("fq","{!geofilt}");
            queryParamMap.put("sfield","latlol_0_coordinate");
            MapSolrParams queryParams = new MapSolrParams(queryParamMap);
            final QueryResponse response = client.query(collection, queryParams);
            final List<LocationDataSolr> documents = response.getBeans(LocationDataSolr.class);
            return documents;

        }catch (Exception ex){
            log.error(ex.getMessage());
            throw new RuntimeException("Error in Solr search: ",ex);
        }
    }


    public List<LocationDataSolr> searchWithFrequency() {
        try {
            DateFormat format = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);

            
            final Map<String, String> queryParamMap = new HashMap<>();
            queryParamMap.put("q", "*");
            queryParamMap.put("facet", "true");
            queryParamMap.put("facet.range", "lastUpdate");
            queryParamMap.put("facet.range.gap", "+1MONTH");
           queryParamMap.put("facet.range.start", "2021-01-01T00:00:00Z");
            queryParamMap.put("facet.range.end", "2021-12-31T05:22:33Z");
            
            MapSolrParams queryParams = new MapSolrParams(queryParamMap);
            final QueryResponse response = client.query(collection, queryParams);
            Map<String, List<Object>> map = new HashMap();
           for (RangeFacet facet:response.getFacetRanges()
                 ) {
               System.out.println(facet.getStart() +" TO "+ facet.getEnd());
                List<LocationDataSolr> allByCountryAndStateAndLastUpdateBetween = repo.findAllByLastUpdateBetween((Date) facet.getStart(), (Date) facet.getEnd());
                List list = new ArrayList();
                list.add(facet.getStart());
                list.add(facet.getEnd());
                list = allByCountryAndStateAndLastUpdateBetween;
                map.put(facet.getStart()+"_"+facet.getEnd() ,list);
            }


            System.out.println(map);

            return new ArrayList<>();
        }catch (Exception ex){
            log.error(ex.getMessage());
            throw new RuntimeException("Error in Solr search: ",ex);
        }
    }

}
