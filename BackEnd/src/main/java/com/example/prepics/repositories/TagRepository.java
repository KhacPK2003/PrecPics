package com.example.prepics.repositories;

import com.example.prepics.entity.Tag;
import com.example.prepics.interfaces.CRUDInterface;
import jakarta.persistence.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class TagRepository implements CRUDInterface<Tag, Long> {
    @PersistenceContext(unitName = "masterEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager masterEntityManager;

    @PersistenceContext(unitName = "slaveEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager slaveEntityManager;

    @Override
    @Transactional("masterTransactionManager")
    public List<Tag> findAll(Class<Tag> clazz, boolean isActive) throws ChangeSetPersister.NotFoundException {
        Optional<List<Tag>> result = Optional.ofNullable(masterEntityManager
                .createQuery("SELECT a FROM Tag a", Tag.class).getResultList());
        return result.orElse(null);
    }

    @Override
    @Transactional("slaveTransactionManager")
    public Tag findById(Class<Tag> clazz, Long id) throws ChangeSetPersister.NotFoundException {
        Optional<Tag> result = Optional.ofNullable(slaveEntityManager.find(clazz, id));
        return result.orElse(null);
    }

    @Override
    @Transactional("masterTransactionManager")
    public Tag create(Tag entity) {
        masterEntityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional("masterTransactionManager")
    public Tag update(Tag entity) {
        return masterEntityManager.merge(entity);
    }

    @Override
    @Transactional("masterTransactionManager")
    public Tag delete(Long id) throws ChangeSetPersister.NotFoundException {
        Optional<Tag> result = Optional.ofNullable(slaveEntityManager.find(Tag.class, id));
        if (result.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        masterEntityManager.remove(masterEntityManager.contains(result.get()) ? result.get()
                : masterEntityManager.merge(result.get()));
        return result.get();
    }
}
