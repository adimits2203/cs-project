package com.my.project.models;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class DashBoardResponseRange {
    private Date startDate;
    private Date endDate;
    private List<DashboardResponseData> dataList;
}
