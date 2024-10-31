package com.example.prepics.repositories;

import com.example.prepics.entity.InCols;
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
public class InColsRepository implements CRUDInterface<InCols, Long> {
    @PersistenceContext(unitName = "masterEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager masterEntityManager;

    @PersistenceContext(unitName = "slaveEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager slaveEntityManager;

    @Override
    @Transactional("masterTransactionManager")
    public List<InCols> findAll(Class<InCols> clazz, boolean isActive) throws ChangeSetPersister.NotFoundException {
        Optional<List<InCols>> result = Optional.ofNullable(masterEntityManager
                .createQuery("SELECT a FROM InCols a", InCols.class).getResultList());
        return result.orElse(null);
    }

    @Override
    @Transactional("slaveTransactionManager")
    public InCols findById(Class<InCols> clazz, Long id) throws ChangeSetPersister.NotFoundException {
        Optional<InCols> result = Optional.ofNullable(slaveEntityManager.find(clazz, id));
        return result.orElse(null);
    }

    @Override
    @Transactional("masterTransactionManager")
    public InCols create(InCols entity) {
        masterEntityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional("masterTransactionManager")
    public InCols update(InCols entity) {
        return masterEntityManager.merge(entity);
    }

    @Override
    @Transactional("masterTransactionManager")
    public InCols delete(Long id) throws ChangeSetPersister.NotFoundException {
        Optional<InCols> result = Optional.ofNullable(slaveEntityManager.find(InCols.class, id));
        if (result.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        masterEntityManager.remove(masterEntityManager.contains(result.get()) ? result.get()
                : masterEntityManager.merge(result.get()));
        return result.get();
    }
}
