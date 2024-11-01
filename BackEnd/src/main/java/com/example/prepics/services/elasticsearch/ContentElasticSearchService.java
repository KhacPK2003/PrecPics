package com.example.prepics.services.elasticsearch;

import com.example.prepics.entity.Content;
import com.example.prepics.models.ContentElasticSearch;

import java.util.List;

public interface ContentElasticSearchService {

    ContentElasticSearch insertContent(Content entity);

    ContentElasticSearch insertContent(ContentElasticSearch entity);

    Iterable<ContentElasticSearch> getContent();

    Iterable<ContentElasticSearch> getContent(boolean  type);

    Iterable<ContentElasticSearch> saveAll(List<Content> Contents);

    Iterable<ContentElasticSearch> deleteAll();

    Iterable<ContentElasticSearch> deleteAll(List<Content> Contents);

    ContentElasticSearch delete(ContentElasticSearch entity);

    ContentElasticSearch delete(Content entity);
}
