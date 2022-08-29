package com.example.batchgeneralize.batch.config;

import lombok.Getter;

@Getter
public class JobConfigData {
    private String jobName;
    private String restReadToFilePath;
    private String restReaderName;
    private String fileItemWriterName;
    private String jsonWriteListenerName;
    private String jsonFileReaderName;
    private String dbWriterName;
    private String deleteOutdatedTaskletName;
    private String restResponseClassName;
    private String responseDtoListItemClassName;
    private Boolean requiresRestCallPagination;
    private Integer retries;
    private String baseUrl;
    private String endpointUrl;
    private String retrieveDataToJsonFileStepName;
    private String saveToDbStepName;
    private String saveToDbProcessorName;
    private String mapperClassName;
    private String mapperMethodName;
    private String dbClassName;
    private String repositoryBeanName;
}
