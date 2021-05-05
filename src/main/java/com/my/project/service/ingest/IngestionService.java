package com.my.project.service.ingest;

import org.springframework.stereotype.Service;

@Service
public interface IngestionService {

    public void ingestAll();

    public void ingestOne();
}
