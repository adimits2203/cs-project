package com.my.project.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponseData implements Serializable {
    private Long confirmed,deaths,recovered,active;
    private Double incidentRate, caseFatalityRatio;
    private Date frequency;
    private String state, country;
}
