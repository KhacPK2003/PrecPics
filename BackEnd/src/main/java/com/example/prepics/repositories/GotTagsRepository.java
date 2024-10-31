package com.example.prepics.repositories;

import com.example.prepics.entity.GotTags;
import com.example.prepics.interfaces.CRUDInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class GotTagsRepository implements CRUDInterface<GotTags, Long> {
    @PersistenceContext(unitName = "masterEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager masterEntityManager;

    @PersistenceContext(unitName = "slaveEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager slaveEntityManager;

    @Override
    @Transactional("masterTransactionManager")
    public List<GotTags> findAll(Class<GotTags> clazz, boolean isActive) throws ChangeSetPersister.NotFoundException {
        Optional<List<GotTags>> result = Optional.ofNullable(masterEntityManager
                .createQuery("SELECT a FROM GotTags a", GotTags.class).getResultList());
        return result.orElse(null);
    }

    @Override
    @Transactional("slaveTransactionManager")
    public GotTags findById(Class<GotTags> clazz, Long id) throws ChangeSetPersister.NotFoundException {
        Optional<GotTags> result = Optional.ofNullable(slaveEntityManager.find(clazz, id));
        return result.orElse(null);
    }

    @Override
    @Transactional("masterTransactionManager")
    public GotTags create(GotTags entity) {
        masterEntityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional("masterTransactionManager")
    public GotTags update(GotTags entity) {
        return masterEntityManager.merge(entity);
    }

    @Override
    @Transactional("masterTransactionManager")
    public GotTags delete(Long id) throws ChangeSetPersister.NotFoundException {
        Optional<GotTags> result = Optional.ofNullable(slaveEntityManager.find(GotTags.class, id));
        if (result.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        masterEntityManager.remove(masterEntityManager.contains(result.get()) ? result.get()
                : masterEntityManager.merge(result.get()));
        return result.get();
    }
}
