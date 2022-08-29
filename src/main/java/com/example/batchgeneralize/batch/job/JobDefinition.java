package com.example.batchgeneralize.batch.job;

import com.example.batchgeneralize.batch.config.JobConfigData;
import com.example.batchgeneralize.batch.processor.GenericSaveToDbProcessor;
import com.example.batchgeneralize.batch.reader.RestGenericReader;
import com.example.batchgeneralize.batch.writer.GenericDbWriter;
import com.example.batchgeneralize.dto.external.ListResponse;
import com.example.batchgeneralize.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.util.List;

@Configuration
@Slf4j
public class JobDefinition {
    @Autowired
    private GenericWebApplicationContext applicationContext;
    @Autowired
    private Environment environment;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public void initializeMetaDataJobs() {
        List<JobConfigData> jobConfigDatas = (List) applicationContext.getBean(Constants.METADATA_JOB_CONFIG_DATA_BEAN_NAME);
        log.info("Number of job config datas found: {}", jobConfigDatas.size());
        jobConfigDatas.forEach(this::registerJob);
    }

    private void registerJob(JobConfigData jobConfigData) {
        final Class restResponseClazz;
        final Class responseListItemClazz;
        final Class dbClazz;
        try {
            restResponseClazz = Class.forName(jobConfigData.getRestResponseClassName());
            responseListItemClazz = Class.forName(jobConfigData.getResponseDtoListItemClassName());
            dbClazz = Class.forName(jobConfigData.getDbClassName());
        } catch (Exception e) {
            log.error("Could not register job - ", e);
            throw new RuntimeException(e);
        }
        applicationContext.registerBean(jobConfigData.getRestReaderName(), ItemReader.class, () -> new RestGenericReader<>(restResponseClazz, jobConfigData, environment));
        applicationContext.registerBean(jobConfigData.getFileItemWriterName(), ItemWriter.class, () -> createJsonFileItemWriter(restResponseClazz, jobConfigData));
        applicationContext.registerBean(jobConfigData.getJsonFileReaderName(), ItemReader.class, () -> createJsonItemReader(restResponseClazz, jobConfigData));
        applicationContext.registerBean(jobConfigData.getSaveToDbProcessorName(), ItemProcessor.class, GenericSaveToDbProcessor::new);
        applicationContext.registerBean(jobConfigData.getDbWriterName(), ItemWriter.class, () -> new GenericDbWriter<>(responseListItemClazz, dbClazz, transactionManager, (JpaRepository) applicationContext.getBean(jobConfigData.getRepositoryBeanName()), jobConfigData));
        Step retrieveDataToJsonFileStep = retrieveDataToJsonFileStep(restResponseClazz, jobConfigData, (ItemReader) applicationContext.getBean(jobConfigData.getRestReaderName()), (ItemWriter) applicationContext.getBean(jobConfigData.getFileItemWriterName()));
        Step saveToDbStep = saveToDbStep(jobConfigData, (ItemReader) applicationContext.getBean(jobConfigData.getJsonFileReaderName()), (ItemWriter) applicationContext.getBean(jobConfigData.getDbWriterName()), (ItemProcessor) applicationContext.getBean(jobConfigData.getSaveToDbProcessorName()));
        applicationContext.registerBean(jobConfigData.getJobName(), Job.class, () -> createJob(jobConfigData, retrieveDataToJsonFileStep, saveToDbStep));
        log.info("Job {} has been registered successfully", jobConfigData.getJobName());
    }

    private <T> JsonFileItemWriter<T> createJsonFileItemWriter(Class<T> classType, JobConfigData jobConfigData) {
        return new JsonFileItemWriterBuilder<T>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileSystemResource(jobConfigData.getRestReadToFilePath()))
                .name(jobConfigData.getFileItemWriterName())
                .build();
    }

    private <T> JsonItemReader<T> createJsonItemReader(Class<T> classType, JobConfigData jobConfigData) {
        return new JsonItemReaderBuilder<T>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(classType))
                .resource(new FileSystemResource(jobConfigData.getRestReadToFilePath()))
                .name(jobConfigData.getJsonFileReaderName())
                .build();
    }

    private <T> Step retrieveDataToJsonFileStep(Class<T> classType, JobConfigData jobConfigData, ItemReader<T> reader, ItemWriter<T> writer) {
        return stepBuilderFactory.get(jobConfigData.getRetrieveDataToJsonFileStepName()).<T, T>chunk(2).reader(reader).writer(writer).build();
    }

    private <X extends ListResponse<Y>, Y> Step saveToDbStep(JobConfigData jobConfigData, ItemReader<X> itemReader, ItemWriter<List<Y>> itemWriter, ItemProcessor<X, List<Y>> processor) {
        return stepBuilderFactory.get(jobConfigData.getSaveToDbStepName()).<X, List<Y>>chunk(1).reader(itemReader).processor(processor).writer(itemWriter).build();
    }

    private Job createJob(JobConfigData jobConfigData, Step retrieveDataToJsonFileStep, Step saveToDbStep) {
        return jobBuilderFactory.get(jobConfigData.getJobName()).incrementer(new RunIdIncrementer()).flow(retrieveDataToJsonFileStep).next(saveToDbStep).end().build();
    }
}
