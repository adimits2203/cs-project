package com.my.project.util;

import com.my.project.dao.LocationDataSolrRepo;
import com.my.project.models.LocationDataSolr;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.my.project.util.Constants.DATE_TIME_FORMAT;

@Slf4j
@Component
public class ReaderUtil {



    @Autowired
    LocationDataSolrRepo solrRepo;



    @Value("${batch_size}")
    private int batchSize;

    public void readCSV(Reader in){
        try {
            List<LocationDataSolr> locationDataList = new ArrayList();
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
            if(records!=null){
                long startTime = System.currentTimeMillis();
                int count = 0;
                for (CSVRecord record : records) {
                    LocationDataSolr locationData = getLocationData(record);
                    log.info(locationData.toString());
                    count++;
                    locationDataList.add(getLocationData(record));
                    if(count==batchSize){
                        solrRepo.saveAll(locationDataList);
                        locationDataList = new ArrayList<>();
                        count=0;
                    }
                }
                if(locationDataList!=null && !locationDataList.isEmpty()){
                    solrRepo.saveAll(locationDataList);
                }
                log.info("number of records: "+ count+" took "+(System.currentTimeMillis() - startTime));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private LocationDataSolr getLocationData(CSVRecord record) {
        LocationDataSolr locationData = new LocationDataSolr();
        locationData.setCounty(record.get("Admin2"));
        locationData.setActive(StringUtils.isNotBlank(record.get("Active"))?Long.valueOf(record.get("Active")): 0L);
        locationData.setConfirmed(StringUtils.isNotBlank(record.get("Confirmed"))?Long.valueOf(record.get("Confirmed")): 0L);
        locationData.setCountry(record.get("Country_Region"));
        locationData.setState(record.get("Province_State"));
        locationData.setDeaths(StringUtils.isNotBlank(record.get("Deaths"))?Long.valueOf(record.get("Deaths")): 0L);
        locationData.setIncidentRate(StringUtils.isNotBlank(record.get("Incident_Rate")) ? Double.valueOf(Double.valueOf(record.get("Incident_Rate"))): 0D);
        locationData.setLatlol_0_coordinate(getLat(record.get("Lat"))+","+ getLat(record.get("Long_")));
        locationData.setRecovered(Long.valueOf(record.get("Recovered")));
        locationData.setLastUpdate(getDate(record.get("Last_Update")));
        locationData.setCaseFatalityRatio(StringUtils.isNotBlank(record.get("Case_Fatality_Ratio")) ? Double.valueOf(Double.valueOf(record.get("Case_Fatality_Ratio"))): 0D);
        return locationData;
    }

    private String getLat(String lat){
        return StringUtils.isNotBlank(lat)? lat: "0.0";
    }


    private static Date getDate(String last_update) {
        DateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
        try {
            return new Date(format.parse(last_update).getTime());
        } catch (ParseException  e) {
            log.error("Error while converting date: "+ last_update);
            return new Date(Calendar.getInstance().getTime().getTime());
        }
    }
}
