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
    public Optional<List<Tag>> findAll(Class<Tag> clazz) throws ChangeSetPersister.NotFoundException {
        return Optional.ofNullable(masterEntityManager
                .createQuery("SELECT a FROM Tag a", Tag.class).getResultList());
    }

    @Override
    @Transactional("slaveTransactionManager")
    public Optional<Tag> findById(Class<Tag> clazz, Long id) throws ChangeSetPersister.NotFoundException {
        return Optional.ofNullable(slaveEntityManager.find(clazz, id));
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<Tag> create(Tag entity) {
        masterEntityManager.persist(entity);
        return Optional.ofNullable(entity);
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<Tag> update(Tag entity) {
        return Optional.ofNullable(masterEntityManager.merge(entity));
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<Tag> delete(Long id) throws ChangeSetPersister.NotFoundException {
        Optional<Tag> result = Optional.ofNullable(slaveEntityManager.find(Tag.class, id));
        if (result.isEmpty()) {
           return Optional.empty();
        }
        masterEntityManager.remove(masterEntityManager.contains(result.get()) ? result.get()
                : masterEntityManager.merge(result.get()));
        return result;
    }

    @Transactional("slaveTransactionManager")
    public Optional<Tag> findByNameIgnoreCase(String name) {
        String query = "SELECT t FROM Tag t WHERE LOWER(t.name) = LOWER(:name)";
        return slaveEntityManager.createQuery(query, Tag.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    @Transactional("slaveTransactionManager")
    public Optional<List<Tag>> findByNamesIgnoreCase(List<String> names) {
        if (names == null || names.isEmpty()) {
            return Optional.empty();
        }

        String query = "SELECT t FROM Tag t WHERE LOWER(t.name) IN :names";
        return Optional.ofNullable(slaveEntityManager.createQuery(query, Tag.class)
                .setParameter("names", names.stream()
                        .map(String::toLowerCase)
                        .toList())
                .getResultList());
    }
}
