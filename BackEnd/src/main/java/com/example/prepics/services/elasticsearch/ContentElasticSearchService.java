package com.example.prepics.services.elasticsearch;

import com.example.prepics.entity.Content;
import com.example.prepics.models.ContentESDocument;

import java.util.List;
import java.util.Optional;

public interface ContentElasticSearchService {

    Optional<ContentESDocument> insertContent(Content entity);

    Optional<ContentESDocument> insertContent(ContentESDocument entity);

    Optional<Iterable<ContentESDocument>> getContent();

    Optional<Iterable<ContentESDocument>> getContent(boolean  type);

    Optional<Iterable<ContentESDocument>> saveAll(List<Content> Contents);

    Optional<Iterable<ContentESDocument>> deleteAll();

    Optional<Iterable<ContentESDocument>> deleteAll(List<Content> Contents);

    Optional<ContentESDocument> delete(ContentESDocument entity);

    Optional<ContentESDocument> delete(Content entity);
}
