package com.example.batchgeneralize.batch.writer;

import com.example.batchgeneralize.batch.config.JobConfigData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class GenericDbWriter<X,Y> implements ItemWriter<List<X>> {

    private final PlatformTransactionManager transactionManager;
    private final Class<X> classTypeX; //represents the dto item corresponding to the entity
    private final Class<Y> classTypeY; //represents the entity

    private final JpaRepository<Y, Object> jpaRepository;

    private final JobConfigData jobConfigData;

    public GenericDbWriter(Class<X> classTypeX, Class<Y> classTypeY, PlatformTransactionManager transactionManager, JpaRepository<Y, Object> jpaRepository, JobConfigData jobConfigData) {
        this.transactionManager = transactionManager;
        this.classTypeX = classTypeX;
        this.classTypeY = classTypeY;
        this.jpaRepository = jpaRepository;
        this.jobConfigData = jobConfigData;
    }

    @Override
    public void write(List<? extends List<X>> list) throws Exception {
        Method method = Class.forName(this.jobConfigData.getMapperClassName()).getMethod(jobConfigData.getMapperMethodName(), classTypeX);
        for (List<X> itemList : list) {
            for (X item : itemList) {
                Y dataItem = (Y) method.invoke(null, item);
                createTransactionTemplate().execute(status -> {
                    try {
                        jpaRepository.save(dataItem);
                    } catch (DataIntegrityViolationException e) {
                        log.error("Integrity constraint violation occurred when saving entity. This entity will be ignored - ", e);
                    }
                    return true;
                });
            }
        }
    }

    private TransactionTemplate createTransactionTemplate() {
        TransactionTemplate trsTemplate = new TransactionTemplate(transactionManager);
        trsTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        trsTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return trsTemplate;
    }
}
