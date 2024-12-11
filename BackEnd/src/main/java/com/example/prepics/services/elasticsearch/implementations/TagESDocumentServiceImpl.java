package com.example.prepics.services.elasticsearch.implementations;

import com.example.prepics.entity.Tag;
import com.example.prepics.models.TagESDocument;
import com.example.prepics.repositories.elasticsearch.TagESRepository;
import com.example.prepics.services.elasticsearch.TagESDocumentService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagESDocumentServiceImpl implements TagESDocumentService {

  @Autowired
  private TagESRepository repository;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public Optional<TagESDocument> insertTag(Tag entity) {
    TagESDocument result = modelMapper.map(entity, TagESDocument.class);
    return Optional.of(repository.save(result));
  }

  @Override
  public Optional<TagESDocument> insertTag(TagESDocument entity) {
    return Optional.of(repository.save(entity));
  }

  @Override
  public Optional<Iterable<TagESDocument>> getTag() {
    return Optional.of(repository.findAll());
  }

  @Override
  public Optional<Iterable<TagESDocument>> saveAll(List<Tag> Tags) {
    List<TagESDocument> results = new ArrayList<>();
    Tags.forEach(e -> {
      results.add(modelMapper.map(e, TagESDocument.class));
    });
    return Optional.of(repository.saveAll(results));
  }

  @Override
  public Optional<Iterable<TagESDocument>> deleteAll() {
    repository.deleteAll();
    return Optional.empty();
  }

  @Override
  public Optional<Iterable<TagESDocument>> deleteAll(List<Tag> Tags) {
    List<TagESDocument> results = new ArrayList<>();
    Tags.forEach(e -> {
      results.add(modelMapper.map(e, TagESDocument.class));
    });
    repository.deleteAll(results);
    return Optional.of(results);
  }

  @Override
  public Optional<TagESDocument> delete(TagESDocument entity) {
    repository.delete(entity);
    return Optional.of(entity);
  }

  @Override
  public Optional<TagESDocument> delete(Tag entity) {
    TagESDocument result = modelMapper.map(entity, TagESDocument.class);
    repository.delete(result);
    return Optional.of(result);
  }
}