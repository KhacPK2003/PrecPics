package com.example.prepics.services.entity.serviceImpl;

import com.example.prepics.entity.GotTags;
import com.example.prepics.repositories.GotTagsRepository;
import com.example.prepics.services.entity.GotTagsService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GotTagsServiceImpl implements GotTagsService {

    @Autowired
    private GotTagsRepository gotTagsRepository;

    @Override
    public Optional<GotTags> delete(Long aLong) throws ChangeSetPersister.NotFoundException {
        return gotTagsRepository.delete(aLong);
    }

    @Override
    public Optional<GotTags> update(GotTags entity) {
        return gotTagsRepository.update(entity);
    }

    @Override
    public Optional<GotTags> create(GotTags entity) throws EntityExistsException, ChangeSetPersister.NotFoundException {
        return gotTagsRepository.create(entity);
    }

    @Override
    public Optional<GotTags> findById(Class<GotTags> clazz, Long aLong) throws ChangeSetPersister.NotFoundException {
        return gotTagsRepository.findById(clazz, aLong);
    }

    @Override
    public Optional<List<GotTags>> findAll(Class<GotTags> clazz) throws ChangeSetPersister.NotFoundException {
        return gotTagsRepository.findAll(clazz);
    }
}
