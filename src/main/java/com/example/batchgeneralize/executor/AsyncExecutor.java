package com.example.batchgeneralize.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AsyncExecutor {

    @Autowired
    private JobLauncher jobLauncher;

    @Async
    public void executeJobs(List<Job> jobsToExecute, JobParameters jobParameters) {
        try {
            for (Job job : jobsToExecute) {
                jobLauncher.run(job, jobParameters);
            }
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException | JobRestartException |
                 JobParametersInvalidException e) {
            log.error("Exception: ", e);
            throw new RuntimeException(e);
        }
    }
}
