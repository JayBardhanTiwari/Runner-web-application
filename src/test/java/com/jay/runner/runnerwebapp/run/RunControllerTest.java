package com.jay.runner.runnerwebapp.run;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

//slice test
@WebMvcTest(Runcontroller.class)
public class RunControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    Runrepository repository;

    private final List<Run> runs = new ArrayList<>();

    @BeforeEach
    void setUp() {
        runs.add(new Run(1,
                "Monday Morning Run",
                LocalDateTime.now(),
                LocalDateTime.now().plus(30, ChronoUnit.MINUTES),
                3,
                Location.INDOOR));
    }

    @Test
    void shouldFindAllRuns() throws Exception {
        when(repository.findAll()).thenReturn(runs);
        mvc.perform(get("/api/runs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(runs.size())));
    }

    @Test
    void shouldFindOneRun() throws Exception {
        Run run = runs.get(0);
        when(repository.findById(ArgumentMatchers.anyInt())).thenReturn(Optional.of(run));
        mvc.perform(get("/api/runs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(run.id())))
                .andExpect(jsonPath("$.title", is(run.title())))
                .andExpect(jsonPath("$.miles", is(run.miles())))
                .andExpect(jsonPath("$.location", is(run.location().toString())));
    }

    @Test
    void shouldReturnNotFoundWithInvalidId() throws Exception {
        mvc.perform(get("/api/runs/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewRun() throws Exception {
        var run = new Run(null, "test", LocalDateTime.now(), LocalDateTime.now().plus(30, ChronoUnit.MINUTES), 1, Location.INDOOR);
        mvc.perform(post("/api/runs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(run))
        )
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateRun() throws Exception {
        var run = new Run(null, "test", LocalDateTime.now(), LocalDateTime.now().plus(30, ChronoUnit.MINUTES), 1, Location.INDOOR);
        mvc.perform(put("/api/runs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(run))
        )
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldDeleteRun() throws Exception {

        when(repository.findById(1)).thenReturn(Optional.of(runs.get(0)));
        mvc.perform(delete("/api/runs/1"))
                .andExpect(status().isNoContent());

        //     mvc.perform(delete("/api/runs/1")
        //        .contentType(MediaType.APPLICATION_JSON)
        //        .content(objectMapper.writeValueAsString(runs.get(0)))         
        //     )
        //             .andExpect(status().isNoContent());
        // }
    }
}
