package com.example.prepics.services.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.prepics.models.TagESDocument;
import com.example.prepics.utils.ElasticSearchUtil;
import java.io.IOException;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticSearchService {

  @Autowired
  private ElasticsearchClient elasticsearchClient;

  public SearchResponse<TagESDocument> fuzzySearch(Class<TagESDocument> clazz, String indexName,
      String fieldName, String approximate) throws IOException {
    Supplier<Query> supplier = ElasticSearchUtil.createSupplierQuery(fieldName, approximate);
    SearchResponse<TagESDocument> response = elasticsearchClient
        .search(s -> s.index(indexName).query(supplier.get()), clazz);
    return response;
  }
}