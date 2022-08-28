package com.example.batchgeneralize.service;

import com.example.batchgeneralize.batch.config.JobConfigData;
import com.example.batchgeneralize.executor.AsyncExecutor;
import com.example.batchgeneralize.utils.Constants;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.util.*;

@Service
public class JobExecutionService implements IJobExecutionService {

    @Autowired
    private GenericWebApplicationContext applicationContext;

    @Autowired
    @Qualifier(value = Constants.METADATA_JOB_CONFIG_DATA_BEAN_NAME)
    private List<JobConfigData> jobConfigDataList;

    @Autowired
    private AsyncExecutor asyncExecutor;

    @Override
    public void executeAllJobs() {
        List<Job> jobs = new ArrayList<>();
        jobConfigDataList.forEach(jobData -> jobs.add((Job) applicationContext.getBean(jobData.getJobName())));
        asyncExecutor.executeJobs(jobs, getJobParameters(new Date()));
    }
    private JobParameters getJobParameters(Date currentDate) {
        Map<String, JobParameter> parameters = new HashMap<>();
        JobParameter parameter = new JobParameter(currentDate);
        parameters.put("currentTime", parameter);
        return new JobParameters(parameters);
    }

}
