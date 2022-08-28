package com.example.batchgeneralize.config;

import com.example.batchgeneralize.batch.config.JobConfigData;
import com.example.batchgeneralize.utils.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableTransactionManagement
@Slf4j
public class CommonConfig {
    @Bean(name = Constants.METADATA_JOB_CONFIG_DATA_BEAN_NAME)
    public List<JobConfigData> metaDataJobConfigDatas() {
        ObjectMapper objectMapper = new ObjectMapper();
        Resource resource = new ClassPathResource("jobConfigData.json");
        List<JobConfigData> jobConfigDatas = new ArrayList<>();
        try {
            jobConfigDatas = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<JobConfigData>>() {
            });
        } catch (Exception e) {
            log.error("Could not load jobConfigData -", e);
        }
        return jobConfigDatas;
    }
}
