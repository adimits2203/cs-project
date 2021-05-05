package com.my.project.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WfhRequest {

    private double latitude ;
    private double longitude;
    private double radius; // in KM
    private long totalPopulation; // within that region
    private double threshholdPercentage;// threshold in %age
}
