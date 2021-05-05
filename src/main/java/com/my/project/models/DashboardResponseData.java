package com.my.project.models;


import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DashboardResponseData implements Serializable {
    private Long confirmed,deaths,recovered,active;
    private Double incidentRate, caseFatalityRatio;
    private Date frequency;
    private String state, country;
}
