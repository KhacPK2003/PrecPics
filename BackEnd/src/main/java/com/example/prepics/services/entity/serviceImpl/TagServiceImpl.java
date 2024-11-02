package com.example.prepics.services.entity.serviceImpl;

import com.example.prepics.entity.Tag;
import com.example.prepics.repositories.TagRepository;
import com.example.prepics.services.entity.TagService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public Optional<Tag> delete(Long aLong) throws ChangeSetPersister.NotFoundException {
        return tagRepository.delete(aLong);
    }

    @Override
    public Optional<Tag> update(Tag entity) {
        return tagRepository.update(entity);
    }

    @Override
    public Optional<Tag> create(Tag entity) throws EntityExistsException, ChangeSetPersister.NotFoundException {
        return tagRepository.create(entity);
    }

    @Override
    public Optional<Tag> findById(Class<Tag> clazz, Long aLong) throws ChangeSetPersister.NotFoundException {
        return tagRepository.findById(clazz, aLong);
    }

    @Override
    public Optional<List<Tag>> findAll(Class<Tag> clazz) throws ChangeSetPersister.NotFoundException {
        return tagRepository.findAll(clazz);
    }
}
