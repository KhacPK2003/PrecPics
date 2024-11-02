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
    public Optional<List<Collection>> findAll(Class<Collection> clazz) throws ChangeSetPersister.NotFoundException {
        return Optional.ofNullable(masterEntityManager
                .createQuery("SELECT a FROM Collection a", Collection.class).getResultList());
    }

    @Override
    @Transactional("slaveTransactionManager")
    public Optional<Collection> findById(Class<Collection> clazz, Long id) throws ChangeSetPersister.NotFoundException {
        return Optional.ofNullable(slaveEntityManager.find(clazz, id));
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<Collection> create(Collection entity) {
        masterEntityManager.persist(entity);
        return Optional.ofNullable(entity);
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<Collection> update(Collection entity) {
        return Optional.ofNullable(masterEntityManager.merge(entity));
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<Collection> delete(Long id) throws ChangeSetPersister.NotFoundException {
        Optional<Collection> result = Optional.ofNullable(slaveEntityManager.find(Collection.class, id));
        if (result.isEmpty()) {
           return Optional.empty();
        }
        masterEntityManager.remove(masterEntityManager.contains(result.get()) ? result.get()
                : masterEntityManager.merge(result.get()));
        return result;
    }
}
