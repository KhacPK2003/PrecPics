package com.example.prepics.services.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.prepics.models.ContentESDocument;
import com.example.prepics.models.TagESDocument;
import com.example.prepics.utils.ElasticSearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Supplier;

@Service
public class ElasticSearchService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

//    public SearchResponse<ContentESDocument> fuzzySearch(Class<ContentESDocument> clazz , String indexName , String fieldName, String approximate) throws IOException {
//        Supplier<Query>  supplier = ElasticSearchUtil.createSupplierQuery(fieldName, approximate);
//        SearchResponse<ContentESDocument> response = elasticsearchClient
//                .search(s->s.index(indexName).query(supplier.get()),ContentESDocument.class);
//        return response;
//    }

    public SearchResponse<?> fuzzySearch(Class<?> clazz , String indexName , String fieldName, String approximate) throws IOException {
        Supplier<Query>  supplier = ElasticSearchUtil.createSupplierQuery(fieldName, approximate);
        SearchResponse<?> response = elasticsearchClient
                .search(s->s.index(indexName).query(supplier.get()),clazz);
        return response;
    }
}
