package com.example.batchgeneralize.controller;

import com.example.batchgeneralize.dto.batch.response.BaseResponse;
import com.example.batchgeneralize.service.IJobExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BatchController implements IBatchController {
    @Autowired
    private IJobExecutionService executionService;

    @Override
    public BaseResponse triggerJobs() {
        executionService.executeAllJobs();
        return BaseResponse.builder().message("Execution of all jobs triggered successfully").build();
    }
}
