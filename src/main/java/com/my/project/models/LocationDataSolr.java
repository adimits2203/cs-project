package com.my.project.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.io.eval.LatLonVectorsEvaluator;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;


import java.util.Date;



@Getter
@Setter

@ToString
@NoArgsConstructor
@SolrDocument
public class LocationDataSolr
{
    @Id
    private String uuid;

    @Indexed
    @Field
    private String county;
    @Indexed
    @Field
    private String state;
    @Indexed
    @Field
    private String country;
    @Indexed
    @Field
    private Date lastUpdate;
    @Indexed
    @Field
    private String latlol_0_coordinate;
    @Indexed
    @Field
    private String latlol_1_coordinate;

    @Indexed
    @Field
    private Long confirmed;
    @Indexed
    @Field
            private Long deaths;
    @Indexed
    @Field
            private Long recovered;
    @Indexed
    @Field
            private Long active;
            @Indexed
            @Field
    private Double incidentRate;
    @Indexed
    @Field
            private Double caseFatalityRatio;

    public LocationDataSolr(Long active){
        setActive(active);
    }
}
