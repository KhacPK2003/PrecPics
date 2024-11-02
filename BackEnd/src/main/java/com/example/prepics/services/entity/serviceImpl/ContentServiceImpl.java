package com.example.prepics.services.entity.serviceImpl;

import com.example.prepics.entity.Content;
import com.example.prepics.repositories.ContentRepository;
import com.example.prepics.services.entity.ContentService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Override
    public Optional<Content> delete(Long aLong) throws ChangeSetPersister.NotFoundException {
        return contentRepository.delete(aLong);
    }

    @Override
    public Optional<Content> update(Content entity) {
        return contentRepository.update(entity);
    }

    @Override
    public Optional<Content> create(Content entity) throws EntityExistsException, ChangeSetPersister.NotFoundException {
        return contentRepository.create(entity);
    }

    @Override
    public Optional<Content> findById(Class<Content> clazz, Long aLong) throws ChangeSetPersister.NotFoundException {
        return contentRepository.findById(clazz, aLong);
    }

    @Override
    public Optional<List<Content>> findAll(Class<Content> clazz) throws ChangeSetPersister.NotFoundException {
        return contentRepository.findAll(clazz);
    }
}
