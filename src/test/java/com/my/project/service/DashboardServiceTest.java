package com.my.project.service;

import com.my.project.dao.LocationDataSolrRepo;
import com.my.project.dao.SolrClientService;
import com.my.project.models.*;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DashboardServiceTest {

    @MockBean
    private LocationDataSolrRepo solrRepo;

    @MockBean
    private SolrClientService solrClient;

    @Autowired
    DashboardService service;

    @BeforeEach
    void setUp() {
    }

    @Test
    void TestGetDashboardDataWithCountryAndStateAndWindowHavingData() throws ParseException {
        DashboardRequestData requestData = new DashboardRequestData();
        requestData.setStartDate("2021-01-02 05:22:33");
        requestData.setEndDate("2021-01-03 05:22:33");
        requestData.setCountry("China");
        requestData.setState("Hubei");
        LocationDataSolr locationDataSolr = new LocationDataSolr();
        List<LocationDataSolr> locationDataSolrList = new ArrayList<>();
        locationDataSolrList.add(locationDataSolr);
        Mockito.when(solrRepo.findAllByCountryAndStateAndLastUpdateBetween(Mockito.any()
                ,Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(locationDataSolrList);
        List<DashboardResponseData> dashboardData = service.getDashboardData(requestData);
        assert(dashboardData.size()==1);

    }

    @Test
    void TestGetDashboardDataWithCountryAndStateAndWindowHavingNoData() throws ParseException {
        DashboardRequestData requestData = new DashboardRequestData();
        requestData.setStartDate("2021-01-02 05:22:33");
        requestData.setEndDate("2021-01-03 05:22:33");
        requestData.setCountry("China");
        requestData.setState("Hubei");
        List<DashboardResponseData> dashboardData = service.getDashboardData(requestData);
        assert(dashboardData.size()==0);
    }

    @Test
    void TestGetDashboardDataWithCountryAndWindowHavingData() throws ParseException {
        DashboardRequestData requestData = new DashboardRequestData();
        requestData.setStartDate("2021-01-02 05:22:33");
        requestData.setEndDate("2021-01-03 05:22:33");
        requestData.setCountry("China");
        LocationDataSolr locationDataSolr = new LocationDataSolr();
        List<LocationDataSolr> locationDataSolrList = new ArrayList<>();
        locationDataSolrList.add(locationDataSolr);
        Mockito.when(solrRepo.findAllByCountryAndLastUpdateBetween(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(locationDataSolrList);
        List<DashboardResponseData> dashboardData = service.getDashboardData(requestData);
        assert(dashboardData.size()==1);
    }

    @Test
    void TestGetDashboardDataWithCountryAndWindowHacingNoData() throws ParseException {
        DashboardRequestData requestData = new DashboardRequestData();
        requestData.setStartDate("2021-01-02 05:22:33");
        requestData.setEndDate("2021-01-03 05:22:33");
        requestData.setCountry("China");
        List<DashboardResponseData> dashboardData = service.getDashboardData(requestData);
        assert(dashboardData.size()==0);
    }

    @Test
    void TestGetContainmentZone() {
        List<LocationDataSolr> containmentZonesSolr = new ArrayList();
        LocationDataSolr  locationDataSolr = new LocationDataSolr();
        locationDataSolr.setCountry("China");
        locationDataSolr.setConfirmed(1L);
        locationDataSolr.setActive(1L);
        containmentZonesSolr.add(locationDataSolr);
        Mockito.when(solrRepo.findAllByLastUpdate(Mockito.any()))
                .thenReturn(containmentZonesSolr);
        List<DashboardResponseData> containmentZones = service.getContainmentZones(new Date(), 10D);
        assertNotNull(containmentZones);
    }

    @Test
    void testIsReadytoWfoIfActiveCasesMoreThanThreshhold() {
        List<LocationDataSolr> search = new ArrayList();
        LocationDataSolr  locationDataSolr = new LocationDataSolr();
        locationDataSolr.setCountry("China");
        locationDataSolr.setConfirmed(1000L);
       locationDataSolr.setDeaths(0L);
       locationDataSolr.setRecovered(0L);
       search.add(locationDataSolr);
        Mockito.when(solrClient.search(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(search);
        WfhRequest wfhRequest = new WfhRequest();
        wfhRequest.setTotalPopulation(10000L);
        wfhRequest.setThreshholdPercentage(10);
        assertFalse(service.isReadyToWfo(wfhRequest));


    }

    @Test
    void testIsReadytoWfoIfActiveCasesLessThanThreshhold() {
        List<LocationDataSolr> search = new ArrayList();
        LocationDataSolr  locationDataSolr = new LocationDataSolr();
        locationDataSolr.setCountry("China");
        locationDataSolr.setConfirmed(1000L);
        locationDataSolr.setDeaths(0L);
        locationDataSolr.setRecovered(0L);
        search.add(locationDataSolr);
        Mockito.when(solrClient.search(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(search);
        WfhRequest wfhRequest = new WfhRequest();
        wfhRequest.setTotalPopulation(1000000L);
        wfhRequest.setThreshholdPercentage(1);
        assertTrue(service.isReadyToWfo(wfhRequest));


    }


    //@Test - // TODO : Add Frequency Support!
    void TestGetDashboardDataWithCountryAndStateAndWindowAndFrequencyHavingData() {
        DashboardRequestData requestData = new DashboardRequestData();
        requestData.setStartDate("2021-01-02 05:22:33");
        requestData.setEndDate("2021-01-03 05:22:33");
        requestData.setCountry("China");
        requestData.setState("Hubei");
        requestData.setFrequency(Frequency.MONTHLY);
        LocationDataSolr locationDataSolr = new LocationDataSolr();
        List<LocationDataSolr> locationDataSolrList = new ArrayList<>();
        locationDataSolrList.add(locationDataSolr);
        Mockito.when(solrRepo.findAllByCountryAndStateAndLastUpdateBetween(Mockito.any()
                ,Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(locationDataSolrList);
        try{
            service.getDashboardData(requestData);
        }
       catch (Exception ex){
            if(ex.getMessage().equals("Frequency filter is not supported")){
                assertTrue(false);
            }
       }

    }
}