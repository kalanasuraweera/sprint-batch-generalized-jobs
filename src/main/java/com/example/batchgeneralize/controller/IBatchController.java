package com.example.batchgeneralize.controller;

import com.example.batchgeneralize.dto.batch.response.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/batch")
public interface IBatchController {
    @PostMapping("/trigger")
    BaseResponse triggerJobs();
}
