package com.example.prepics.repositories.elasticsearch;

import com.example.prepics.models.ContentESDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ContentElasticSearchRepository extends ElasticsearchRepository<ContentESDocument, Long> {
}
