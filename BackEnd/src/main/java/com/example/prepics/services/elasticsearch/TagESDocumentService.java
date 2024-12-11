package com.example.prepics.services.elasticsearch;

import com.example.prepics.entity.Tag;
import com.example.prepics.models.TagESDocument;
import java.util.List;
import java.util.Optional;

public interface TagESDocumentService {

  Optional<TagESDocument> insertTag(Tag entity);

  Optional<TagESDocument> insertTag(TagESDocument entity);

  Optional<Iterable<TagESDocument>> getTag();

  Optional<Iterable<TagESDocument>> saveAll(List<Tag> Tags);

  Optional<Iterable<TagESDocument>> deleteAll();

  Optional<Iterable<TagESDocument>> deleteAll(List<Tag> Tags);

  Optional<TagESDocument> delete(TagESDocument entity);

  Optional<TagESDocument> delete(Tag entity);
}
