package com.jay.runner.runnerwebapp.run;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Runjsondataloader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Runjsondataloader.class);
    private final JdbcRunrepository runRepository;
    private final ObjectMapper objectMapper;

    public Runjsondataloader(JdbcRunrepository runRepository, ObjectMapper objectMapper) {
        this.runRepository = runRepository;
        this.objectMapper = objectMapper;

    }

    @Override
    public void run(String... args) throws Exception {

        if (runRepository.count() == 0) {
            try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/runs.json")) {
                Runs allRuns = objectMapper.readValue(inputStream, Runs.class);
                log.info("Reading {} runs from JSON data and saving it to a database ", allRuns.runs().size());
                runRepository.saveAll(allRuns.runs());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read JSON data", e);
            }

        } else {
            log.info("NOT loading Runs from JSON data because the collection contains data");
        }
    }

}