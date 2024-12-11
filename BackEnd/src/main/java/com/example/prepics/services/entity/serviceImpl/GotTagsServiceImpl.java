package com.example.prepics.services.entity.serviceImpl;

import com.example.prepics.entity.GotTags;
import com.example.prepics.entity.Tag;
import com.example.prepics.repositories.GotTagsRepository;
import com.example.prepics.services.elasticsearch.TagESDocumentService;
import com.example.prepics.services.entity.GotTagsService;
import com.example.prepics.services.entity.TagService;
import jakarta.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

@Service
public class GotTagsServiceImpl implements GotTagsService {

  @Autowired
  private GotTagsRepository gotTagsRepository;

  @Autowired
  private TagService tagService;

  @Autowired
  private TagESDocumentService tagESDocumentService;

  @Override
  public Optional<GotTags> delete(Long aLong) throws ChangeSetPersister.NotFoundException {
    return gotTagsRepository.delete(aLong);
  }

  @Override
  public Optional<GotTags> update(GotTags entity) {
    return gotTagsRepository.update(entity);
  }

  @Override
  public Optional<GotTags> create(GotTags entity)
      throws EntityExistsException, ChangeSetPersister.NotFoundException {
    return gotTagsRepository.create(entity);
  }

  @Override
  public Optional<GotTags> findById(Class<GotTags> clazz, Long aLong)
      throws ChangeSetPersister.NotFoundException {
    return gotTagsRepository.findById(clazz, aLong);
  }

  @Override
  public Optional<List<GotTags>> findAll(Class<GotTags> clazz)
      throws ChangeSetPersister.NotFoundException {
    return gotTagsRepository.findAll(clazz);
  }

  @Override
  public boolean addTagByName(String contentId, String tagName)
      throws ChangeSetPersister.NotFoundException {
    Tag tag = tagService.findByName(Tag.class, tagName).orElseThrow();
    GotTags isExits = gotTagsRepository.findByContentIdAndTagId(contentId, tag.getId())
        .orElse(null);
    if (isExits == null) {
      isExits = new GotTags();
      isExits.setTagId(tag.getId());
      isExits.setContentId(contentId);
      gotTagsRepository.create(isExits);
      return true;
    }
    return false;
  }
}
