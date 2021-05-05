package com.my.project.service.ingest;


import com.my.project.models.LocationDataSolr;
import com.my.project.util.ReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Service
@Slf4j
public class CSVIngestService implements IngestionService{

    @Value("${source.ingest.service.url}")
    String url;

    @Value("${source.ingest.service.daily.file.path}")
    String localFilePath;

    @Autowired
    ReaderUtil readerUtil;

    @Override
    public void ingestAll() {

    }

    @Override
    public void ingestOne() {
        try{
            readerUtil.readCSV(new FileReader(localFilePath));
        }
        catch (Exception ex){
            ex.printStackTrace();
            log.error("Error while ingesting one file: "+localFilePath+":"+ex.getMessage());
        }


    }
}
