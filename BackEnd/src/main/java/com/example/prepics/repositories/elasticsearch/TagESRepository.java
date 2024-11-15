package com.example.prepics.repositories.elasticsearch;

import com.example.prepics.models.TagESDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TagESRepository extends ElasticsearchRepository<TagESDocument, Long> {
}
