package com.example.batchgeneralize.batch.reader;

import com.example.batchgeneralize.batch.config.JobConfigData;
import com.example.batchgeneralize.config.EndpointConfig;
import com.example.batchgeneralize.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class RestGenericReader<T> implements ItemReader<T> {

    private final Class<T> classTypeT;
    private final JobConfigData jobConfigData;
    private final WebClient webClient;
    private final Environment environment;

    public RestGenericReader(Class<T> classTypeT, JobConfigData jobConfigData, Environment environment) {
        this.classTypeT = classTypeT;
        this.jobConfigData = jobConfigData;
        this.environment = environment;
        this.webClient = EndpointConfig.getGenericWebClient(jobConfigData.getBaseUrl());
    }

    @Override
    public T read() {
        ExecutionContext executionContext = StepSynchronizationManager.getContext().getStepExecution().getExecutionContext();
        if ((int) executionContext.get(Constants.OFFSET) > 1) {
            //this is used to trigger the API call only once. This logic should be customized if you need to use pagination
            return null;
        }
        Mono<T> responseMono = webClient.get().uri(uriBuilder ->
                uriBuilder.path(jobConfigData.getEndpointUrl()).build()).retrieve().bodyToMono(this.classTypeT).retry(jobConfigData.getRetries());
        T responseDto = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            responseDto = mapper.convertValue(Objects.requireNonNull(responseMono.block()), classTypeT);
        } catch (Exception e) {
            log.error("Error when reading from micro-integrator", e);
            throw new RuntimeException(e);
        }
        //update offset after triggering the API call so the next API call can retrieve the next offset from the execution context
        executionContext.put(Constants.OFFSET, ((int) executionContext.get(Constants.OFFSET)) + 1);
        return responseDto;
    }

    @BeforeStep
    void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution.getExecutionContext();
        ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
        Resource resource = new FileSystemResource(jobConfigData.getRestReadToFilePath());
        //if new job
        executionContext.put(Constants.OFFSET, 1);
        jobExecutionContext.put(Constants.OFFSET, 1);
        try {
            new FileWriter(resource.getFile(), false).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterStep
    void afterStep(StepExecution stepExecution) {
        ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
        ExecutionContext executionContext = stepExecution.getExecutionContext();
        //Update the job offset with the step offset. Needed for job restarts to retain the last successful offset
        jobExecutionContext.put(Constants.OFFSET, executionContext.get(Constants.OFFSET));
    }
}
