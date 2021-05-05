package com.my.project.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class DashboardRequestData implements Serializable {
    private String state;
    private String country;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private String startDate;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private String endDate;
    private Frequency frequency;
}
