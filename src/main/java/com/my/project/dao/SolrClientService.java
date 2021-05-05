package com.my.project.dao;

import com.my.project.models.LocationDataSolr;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.MapSolrParams;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SolrClientService {

    final String solrUrl = "http://localhost:8983/solr";
    private SolrClient getSolrClient(){
        return new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
    }

    final SolrClient client = getSolrClient();

    public List<LocationDataSolr> search(Double lat, Double lon, Double radius){
        try {
            final Map<String, String> queryParamMap = new HashMap<>();
            queryParamMap.put("q", "*:*");
            queryParamMap.put("d", radius+"");
            queryParamMap.put("pt", lat+","+lon);
            queryParamMap.put("fq","{!geofilt}");
            queryParamMap.put("sfield","latlol_0_coordinate");
            MapSolrParams queryParams = new MapSolrParams(queryParamMap);
            final QueryResponse response = client.query("locationdatasolr", queryParams);
            final List<LocationDataSolr> documents = response.getBeans(LocationDataSolr.class);
            return documents;

        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }

}
