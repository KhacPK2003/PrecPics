package com.example.prepics.repositories.elasticsearch;

import com.example.prepics.models.ContentElasticSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ContentElasticSearchRepository extends ElasticsearchRepository<ContentElasticSearch, Long> {
}
