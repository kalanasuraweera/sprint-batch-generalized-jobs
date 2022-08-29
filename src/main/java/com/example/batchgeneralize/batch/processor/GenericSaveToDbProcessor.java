package com.example.batchgeneralize.batch.processor;

import com.example.batchgeneralize.dto.external.ListResponse;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

public class GenericSaveToDbProcessor<Y, X extends ListResponse<Y>> implements ItemProcessor<X, List<Y>> {
    @Override
    public List<Y> process(X x) throws Exception {
        return x.getList();
    }

}
