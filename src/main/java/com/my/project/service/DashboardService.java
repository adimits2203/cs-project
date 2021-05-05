package com.my.project.service;


import com.my.project.dao.LocationDataSolrRepo;
import com.my.project.dao.SolrClientService;
import com.my.project.models.*;
import com.my.project.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class DashboardService {

    @Autowired
    private LocationDataSolrRepo solrRepo;

    @Autowired
    private SolrClientService solrClient;


    public List<DashboardResponseData> getDashboardData(DashboardRequestData requestData) throws ParseException {
        List<DashboardResponseData> data = new ArrayList<>();
        DateFormat format = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);

        Date start;
        List<LocationDataSolr> solrDataList = new ArrayList<>();
        Date end;
        if (isCountry(requestData)) {
            start = format.parse(requestData.getStartDate());
            end = format.parse(requestData.getEndDate());
            Frequency frequency = requestData.getFrequency();
            solrDataList = getLocationDataSolrs(requestData, start, end, frequency);
        }
        getLocationData(data, solrDataList);

        return data;
    }

    private List<LocationDataSolr> getLocationDataSolrs(DashboardRequestData requestData, Date start, Date end, Frequency frequency) {
        List<LocationDataSolr> solrDataList;
        if (StringUtils.isNotBlank(requestData.getState())) {
            solrDataList = getLocationDataSolrsWithState(requestData, start, end, frequency);
        } else {
            solrDataList = getLocationDataSolrsCountry(requestData, start, end, frequency);
        }
        return solrDataList;
    }

    private List<LocationDataSolr> getLocationDataSolrsCountry(DashboardRequestData requestData, Date start, Date end, Frequency frequency) {
        List<LocationDataSolr> solrDataList;
        if (requestData.getFrequency() != null) {
            throw new RuntimeException("Frequency filter is not supported");//solrRepo.findAllByCountryAndLastUpdateBetweenWithFrequency(requestData.getCountry(), start, end, frequency);
        } else {
            solrDataList = solrRepo.findAllByCountryAndLastUpdateBetween(requestData.getCountry(), start, end);
        }
        return solrDataList;
    }

    private List<LocationDataSolr> getLocationDataSolrsWithState(DashboardRequestData requestData, Date start, Date end, Frequency frequency) {
        List<LocationDataSolr> solrDataList;
        if (requestData.getFrequency() != null) {
            throw new RuntimeException("Frequency filter is not supported");//solrRepo.findAllByCountryAndStateLastUpdateBetweenWithFrequency(requestData.getCountry(), start, end, frequency);
        } else {
            solrDataList = solrRepo.findAllByCountryAndStateAndLastUpdateBetween(requestData.getCountry(), requestData.getState(), start, end);
        }
        return solrDataList;
    }

    private void getLocationData(List<DashboardResponseData> data, List<LocationDataSolr> solrDataList) {
        if (solrDataList != null && solrDataList.size() > 0) {
            solrDataList.forEach(loc -> {
                mapSolrDataToDashboardResponse(data, loc);
            });
        }
    }

    private void mapSolrDataToDashboardResponse(List<DashboardResponseData> data, LocationDataSolr loc) {
        DashboardResponseData dashboardResponseData = new DashboardResponseData();
        dashboardResponseData.setActive(loc.getActive());
        dashboardResponseData.setConfirmed(loc.getConfirmed());
        dashboardResponseData.setDeaths(loc.getDeaths());
        dashboardResponseData.setRecovered(loc.getRecovered());
        dashboardResponseData.setIncidentRate(loc.getIncidentRate());
        dashboardResponseData.setCaseFatalityRatio(loc.getCaseFatalityRatio());
        dashboardResponseData.setState(loc.getState());
        dashboardResponseData.setCountry(loc.getCountry());
        data.add(dashboardResponseData);
    }


    private boolean isCountry(DashboardRequestData requestData) {
        return StringUtils.isNotBlank(requestData.getCountry());
    }

    public boolean isReadyToWfo(WfhRequest wfhRequest) {
        List<LocationDataSolr> search = solrClient.search(wfhRequest.getLatitude(), wfhRequest.getLongitude(), wfhRequest.getRadius());
        return isReadyToWfo(wfhRequest, search);
    }

    private boolean isReadyToWfo(WfhRequest wfhRequest, List<LocationDataSolr> search) {
        long totalConfirmed = search.stream().mapToLong(LocationDataSolr::getConfirmed).sum();
        long totalDeaths = search.stream().mapToLong(LocationDataSolr::getDeaths).sum();
        long totalRecovered = search.stream().mapToLong(LocationDataSolr::getRecovered).sum();
        double totalActive = totalConfirmed - totalDeaths - totalRecovered;
        double percActive = (totalActive / wfhRequest.getTotalPopulation()) * 100;
        if (percActive < wfhRequest.getThreshholdPercentage()) {
            return true;
        }
        return false;
    }

    public List<DashboardResponseData> getContainmentZones(Date date, Double threshhold) {
        List<LocationDataSolr> containmentZones = solrRepo.findAllByLastUpdate(date);
        List<DashboardResponseData> responseDataList = new ArrayList<>();
        containmentZones.stream().filter(d -> (d.getActive() < threshhold)).forEach(d -> {
            mapSolrDataToResponse(responseDataList, d);
        });
        return responseDataList;
    }

    private void mapSolrDataToResponse(List<DashboardResponseData> responseDataList, LocationDataSolr d) {
        DashboardResponseData responseData = new DashboardResponseData();
        responseData.setCaseFatalityRatio(d.getCaseFatalityRatio());
        responseData.setRecovered(d.getRecovered());
        responseData.setDeaths(d.getDeaths());
        responseData.setConfirmed(d.getConfirmed());
        responseData.setActive(d.getActive());
        responseData.setCountry(d.getCountry());
        responseData.setState(d.getState());
        responseDataList.add(responseData);
    }
}
