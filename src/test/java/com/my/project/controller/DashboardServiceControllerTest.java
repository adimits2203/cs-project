package com.my.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.project.dao.LocationDataSolrRepo;
import com.my.project.models.DashboardRequestData;
import com.my.project.models.DashboardResponseData;
import com.my.project.service.DashboardService;
import com.my.project.service.ingest.IngestionService;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.solr.client.solrj.SolrClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DashboardServiceControllerTest {

    @MockBean
    private IngestionService ingestionService;



    @MockBean
    private DashboardService dashboardService;


    protected MockMvc mvc;


    public static final String uri = "http://localhost:8080/v1/dashboard-service";

    @Autowired
    WebApplicationContext webApplicationContext;

    @BeforeEach
    protected void setup(){
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void TestIngestAllPresent() throws Exception {
         MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri+"/ingest/all")
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

    }

    @Test
    void TestGetDataPresent() throws Exception {
        List< DashboardResponseData > list = new ArrayList<>();
        list.add(new DashboardResponseData());
        Mockito.when(dashboardService.getDashboardData(Mockito.any())).thenReturn(list);
        String s= "{\n" +
                "  \"state\": \"\",\n" +
                "  \"country\": \"China\",\n" +
                "  \"startDate\": \"2021-01-02 05:22:33\",\n" +
                "  \"endDate\": \"2021-01-03 05:22:33\"\n" +
                "}";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri + "/data/get")
                .contentType(MediaType.APPLICATION_JSON).content(s).accept(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    void TestIsReadyToWfoPresent() throws Exception {
        List< DashboardResponseData > list = new ArrayList<>();
        list.add(new DashboardResponseData());
        Mockito.when(dashboardService.isReadyToWfo(Mockito.any())).thenReturn(true);
        String s ="{\n" +
                "  \"latitude\": 30.9756,\n" +
                "  \"longitude\": 112.2707,\n" +
                "  \"radius\": 100,\n" +
                "  \"totalPopulation\": 100000,\n" +
                "  \"threshholdPercentage\": 5\n" +
                "}";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri + "/data/get/ready-to-wfo")
                .contentType(MediaType.APPLICATION_JSON).content(s).accept(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    void TestGetContainmentZonesPresent() throws Exception {
        List< DashboardResponseData > list = new ArrayList<>();
        list.add(new DashboardResponseData());
        Mockito.when(dashboardService.getDashboardData(Mockito.any())).thenReturn(list);
        DashboardRequestData dashboardRequestData = new DashboardRequestData();
        String asOn = "2021-01-02 05:22:33";
        Double threshhold = 20.0D;
        String s = uri + "/data/containment-zones/"+asOn+"/"+threshhold;
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(s)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}