package com.example.prepics.services.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.prepics.models.ContentElasticSearch;
import com.example.prepics.utils.ElasticSearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Supplier;

@Service
public class ElasticSearchService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public SearchResponse<ContentElasticSearch> fuzzySearch(String indexName , String fieldName, String approximate) throws IOException {
        Supplier<Query>  supplier = ElasticSearchUtil.createSupplierQuery(fieldName, approximate);
        SearchResponse<ContentElasticSearch> response = elasticsearchClient
                .search(s->s.index(indexName).query(supplier.get()),ContentElasticSearch.class);
        return response;
    }
}
