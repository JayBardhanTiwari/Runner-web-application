package com.jay.runner.runnerwebapp.run;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

//integration test for controller
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class RunControllerIntTest {

    @LocalServerPort
    int randomServerPort;

    RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.create("http://localhost:" + randomServerPort);
    }

    @Test
    void shouldFindAllRuns() {
        List<Runs> runs = restClient.get()
                .uri("/api/runs")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        assertEquals(10, runs.size());
    }

    @Test
    void shouldFindRunById() {
        Run run = restClient.get()
                .uri("/api/runs/1")
                .retrieve()
                .body(Run.class);

        assertAll(
                () -> assertEquals(1, run.id()),
                () -> assertEquals("Noon Run", run.title()),
                () -> assertEquals("2024-02-20T06:05", run.startedOn().toString()),
                () -> assertEquals("2024-02-20T10:27", run.completedOn().toString()),
                () -> assertEquals(24, run.miles()),
                () -> assertEquals(Location.INDOOR, run.location()));
    }

    @Test
    void shouldCreateNewRun() {
        Run run = new Run(11, "Evening Run", LocalDateTime.now(), LocalDateTime.now().plusHours(2), 10, Location.OUTDOOR);

        ResponseEntity<Void> newRun = restClient.post()
                .uri("/api/runs")
                .body(run)
                .retrieve()
                .toBodilessEntity();

        assertEquals(201, newRun.getStatusCodeValue());
    }
    @Test
    void shouldUpdateExistingRun() {
        // Retrieve the existing run to verify it exists
        Run existingRun = restClient.get()
                .uri("/api/runs/1")
                .retrieve()
                .body(Run.class);

        assertNotNull(existingRun, "Run with ID 1 should exist before updating");

        // Prepare an updated run object
        Run updatedRun = new Run(1, "Updated Run", LocalDateTime.now(), LocalDateTime.now().plusHours(1), 15, Location.OUTDOOR);

        // Perform the update
        ResponseEntity<Void> updatedResponse = restClient.put()
                .uri("/api/runs/1")
                .body(updatedRun)
                .retrieve()
                .toBodilessEntity();

        assertEquals(204, updatedResponse.getStatusCodeValue());

        // Verify the update
        Run fetchedRun = restClient.get()
                .uri("/api/runs/1")
                .retrieve()
                .body(Run.class);

        assertAll(
                () -> assertEquals("Updated Run", fetchedRun.title()),
                () -> assertEquals(15, fetchedRun.miles()),
                () -> assertEquals(Location.OUTDOOR, fetchedRun.location())
        );
    }

    @Test
    void shouldDeleteRun() {
        ResponseEntity<Void> run = restClient.delete()
                .uri("/api/runs/1")
                .retrieve()
                .toBodilessEntity();

        assertEquals(204, run.getStatusCodeValue());
    }

}
