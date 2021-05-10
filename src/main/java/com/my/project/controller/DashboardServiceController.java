package com.my.project.controller;

import com.my.project.models.DashboardRequestData;
import com.my.project.models.DashboardResponseData;
import com.my.project.models.WfhRequest;
import com.my.project.service.DashboardService;
import com.my.project.service.ingest.IngestionService;
import com.my.project.util.Constants;
import com.my.project.util.Utils;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/dashboard-service")
public class DashboardServiceController {


    private IngestionService ingestionService;


    private DashboardService dashboardService;


    @Autowired
    public DashboardServiceController(IngestionService ingestionService, DashboardService dashboardService) {
        this.ingestionService = ingestionService;

        this.dashboardService = dashboardService;
    }

    @GetMapping("/ingest/one")
    public ResponseEntity<String> ingestAll() {
        ingestionService.ingestOne();
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/data/get")
    public ResponseEntity<List<DashboardResponseData>> getData(@RequestBody DashboardRequestData requestData) throws ParseException {
        if (!Utils.isWindow(requestData.getStartDate(), requestData.getEndDate())) {
            ResponseEntity.badRequest().build();
        }
        List<DashboardResponseData> dashboardData = dashboardService.getDashboardData(requestData);
        if (dashboardData.size() > 0) {
            return ResponseEntity.ok(dashboardData);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/data/get/ready-to-wfo")
    public ResponseEntity<Boolean> isReadyToWfo(@RequestBody WfhRequest wfhRequest) {
        boolean isReadyToWfh = false;

        isReadyToWfh = dashboardService.isReadyToWfo(wfhRequest);

        return ResponseEntity.ok(isReadyToWfh);

    }

    @GetMapping("/data/containment-zones/{asOn}/{threshHold}")
    public ResponseEntity<List<DashboardResponseData>> getContainmentZones(@PathVariable String asOn, @PathVariable double threshHold) throws ParseException {
        DateFormat format = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
            Date date = format.parse(asOn);
            List<DashboardResponseData> containmentZones = dashboardService.getContainmentZones(date, threshHold);
            return ResponseEntity.ok(containmentZones);
    }
}
