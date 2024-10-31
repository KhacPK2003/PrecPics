package com.example.prepics.repositories;

import com.example.prepics.entity.Content;
import com.example.prepics.interfaces.CRUDInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class ContentRepository implements CRUDInterface<Content, Long> {
    @PersistenceContext(unitName = "masterEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager masterEntityManager;

    @PersistenceContext(unitName = "slaveEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager slaveEntityManager;

    @Override
    @Transactional("masterTransactionManager")
    public List<Content> findAll(Class<Content> clazz, boolean isActive) throws ChangeSetPersister.NotFoundException {
        Optional<List<Content>> result = Optional.ofNullable(masterEntityManager
                .createQuery("SELECT a FROM Content a", Content.class).getResultList());
        return result.orElse(null);
    }

    @Override
    @Transactional("slaveTransactionManager")
    public Content findById(Class<Content> clazz, Long id) throws ChangeSetPersister.NotFoundException {
        Optional<Content> result = Optional.ofNullable(slaveEntityManager.find(clazz, id));
        return result.orElse(null);
    }

    @Override
    @Transactional("masterTransactionManager")
    public Content create(Content entity) {
        masterEntityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional("masterTransactionManager")
    public Content update(Content entity) {
        return masterEntityManager.merge(entity);
    }

    @Override
    @Transactional("masterTransactionManager")
    public Content delete(Long id) throws ChangeSetPersister.NotFoundException {
        Optional<Content> result = Optional.ofNullable(slaveEntityManager.find(Content.class, id));
        if (result.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        masterEntityManager.remove(masterEntityManager.contains(result.get()) ? result.get()
                : masterEntityManager.merge(result.get()));
        return result.get();
    }
}
