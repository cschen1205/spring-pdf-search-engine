package com.github.chen0040.pdf_search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class PdfSearchEngineApp {
    public static void main(String[] args) {
        SpringApplication.run(PdfSearchEngineApp.class, args);
    }
}

