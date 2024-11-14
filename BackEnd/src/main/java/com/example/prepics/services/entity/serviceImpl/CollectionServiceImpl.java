package com.example.prepics.services.entity.serviceImpl;

import com.example.prepics.entity.Collection;
import com.example.prepics.repositories.CollectionRepository;
import com.example.prepics.services.entity.CollectionService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    @Override
    public Optional<Collection> delete(Long aLong) throws ChangeSetPersister.NotFoundException {
        return collectionRepository.delete(aLong);
    }

    @Override
    public Optional<Collection> update(Collection entity) {
        return collectionRepository.update(entity);
    }

    @Override
    public Optional<Collection> create(Collection entity)
            throws EntityExistsException, ChangeSetPersister.NotFoundException {
        return collectionRepository.create(entity);
    }

    @Override
    public Optional<Collection> findById(Class<Collection> clazz, Long aLong) throws ChangeSetPersister.NotFoundException {
        return collectionRepository.findById(clazz, aLong);
    }

    @Override
    public Optional<List<Collection>> findAll(Class<Collection> clazz) throws ChangeSetPersister.NotFoundException {
        return collectionRepository.findAll(clazz);
    }

    @Override
    public Optional<List<Collection>> getUserCollection(String userId) throws ChangeSetPersister.NotFoundException {
        return collectionRepository.getUserCollection(userId);
    }

    @Override
    public Optional<Collection> getUserCollectionByName(String userId, String collectionName)
            throws ChangeSetPersister.NotFoundException {
        return collectionRepository.getUserCollectionByName(userId, collectionName);
    }
}
