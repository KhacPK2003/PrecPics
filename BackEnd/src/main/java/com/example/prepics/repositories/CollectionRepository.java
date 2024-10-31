package com.example.prepics.repositories;

import com.example.prepics.entity.Collection;
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
public class CollectionRepository implements CRUDInterface<Collection, Long> {
    @PersistenceContext(unitName = "masterEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager masterEntityManager;

    @PersistenceContext(unitName = "slaveEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager slaveEntityManager;

    @Override
    @Transactional("masterTransactionManager")
    public List<Collection> findAll(Class<Collection> clazz, boolean isActive) throws ChangeSetPersister.NotFoundException {
        Optional<List<Collection>> result = Optional.ofNullable(masterEntityManager
                .createQuery("SELECT a FROM Collection a", Collection.class).getResultList());
        return result.orElse(null);
    }

    @Override
    @Transactional("slaveTransactionManager")
    public Collection findById(Class<Collection> clazz, Long id) throws ChangeSetPersister.NotFoundException {
        Optional<Collection> result = Optional.ofNullable(slaveEntityManager.find(clazz, id));
        return result.orElse(null);
    }

    @Override
    @Transactional("masterTransactionManager")
    public Collection create(Collection entity) {
        masterEntityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional("masterTransactionManager")
    public Collection update(Collection entity) {
        return masterEntityManager.merge(entity);
    }

    @Override
    @Transactional("masterTransactionManager")
    public Collection delete(Long id) throws ChangeSetPersister.NotFoundException {
        Optional<Collection> result = Optional.ofNullable(slaveEntityManager.find(Collection.class, id));
        if (result.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        masterEntityManager.remove(masterEntityManager.contains(result.get()) ? result.get()
                : masterEntityManager.merge(result.get()));
        return result.get();
    }
}
