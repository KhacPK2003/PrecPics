package com.example.prepics.services.elasticsearch;

import com.example.prepics.entity.Content;
import com.example.prepics.models.ContentElasticSearch;

import java.util.List;
import java.util.Optional;

public interface ContentElasticSearchService {

    Optional<ContentElasticSearch> insertContent(Content entity);

    Optional<ContentElasticSearch> insertContent(ContentElasticSearch entity);

    Optional<Iterable<ContentElasticSearch>> getContent();

    Optional<Iterable<ContentElasticSearch>> getContent(boolean  type);

    Optional<Iterable<ContentElasticSearch>> saveAll(List<Content> Contents);

    Optional<Iterable<ContentElasticSearch>> deleteAll();

    Optional<Iterable<ContentElasticSearch>> deleteAll(List<Content> Contents);

    Optional<ContentElasticSearch> delete(ContentElasticSearch entity);

    Optional<ContentElasticSearch> delete(Content entity);
}
