package com.example.prepics.services.entity.serviceImpl;

import com.example.prepics.entity.Tag;
import com.example.prepics.repositories.TagRepository;
import com.example.prepics.services.elasticsearch.TagESDocumentService;
import com.example.prepics.services.entity.TagService;
import jakarta.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements TagService {

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private TagESDocumentService tagESDocumentService;

  @Override
  public Optional<Tag> delete(Long aLong) throws ChangeSetPersister.NotFoundException {
    return tagRepository.delete(aLong);
  }

  @Override
  public Optional<Tag> update(Tag entity) {
    return tagRepository.update(entity);
  }

  @Override
  public Optional<Tag> create(Tag entity)
      throws EntityExistsException, ChangeSetPersister.NotFoundException {
    return tagRepository.create(entity);
  }

  @Override
  public Optional<Tag> create(String tagName)
      throws EntityExistsException, ChangeSetPersister.NotFoundException {
    Tag tag = new Tag();
    tag.setName(tagName);
    return tagRepository.create(tag);
  }

  @Override
  public Optional<List<Tag>> findAll(Class<Tag> clazz, int page, int size)
      throws ChangeSetPersister.NotFoundException {
    return tagRepository.findAll(clazz, page, size);
  }

  @Override
  public Optional<Tag> findById(Class<Tag> clazz, Long aLong)
      throws ChangeSetPersister.NotFoundException {
    return tagRepository.findById(clazz, aLong);
  }

  @Override
  public Optional<List<Tag>> findAll(Class<Tag> clazz) throws ChangeSetPersister.NotFoundException {
    return tagRepository.findAll(clazz);
  }

  @Override
  public Optional<Tag> findByName(Class<Tag> clazz, String name)
      throws ChangeSetPersister.NotFoundException {
    Optional<Tag> tag = tagRepository.findByNameIgnoreCase(name);

    if (tag.isPresent()) {
      return tag;
    }
    Tag result = create(name).orElseThrow(RuntimeException::new);
    tagESDocumentService.insertTag(result);
    return Optional.of(result);
  }
}