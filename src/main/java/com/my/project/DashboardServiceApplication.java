package com.my.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@SpringBootApplication
@EnableSolrRepositories(basePackages = "com.my.project")
public class DashboardServiceApplication {


	public static void main(String[] args) {
		SpringApplication.run(DashboardServiceApplication.class, args);

	}

}
