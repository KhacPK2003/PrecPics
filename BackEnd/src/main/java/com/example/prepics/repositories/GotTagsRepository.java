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
    public Optional<List<GotTags>> findAll(Class<GotTags> clazz) throws ChangeSetPersister.NotFoundException {
        return Optional.ofNullable(masterEntityManager
                .createQuery("SELECT a FROM GotTags a", GotTags.class).getResultList());
    }

    @Override
    @Transactional("slaveTransactionManager")
    public Optional<GotTags> findById(Class<GotTags> clazz, Long id) throws ChangeSetPersister.NotFoundException {
        return Optional.ofNullable(slaveEntityManager.find(clazz, id));
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<GotTags> create(GotTags entity) {
        masterEntityManager.persist(entity);
        return Optional.ofNullable(entity);
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<GotTags> update(GotTags entity) {
        return Optional.ofNullable(masterEntityManager.merge(entity));
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<GotTags> delete(Long id) throws ChangeSetPersister.NotFoundException {
        GotTags result = Optional.ofNullable(slaveEntityManager.find(GotTags.class, id))
                        .orElseThrow(ChangeSetPersister.NotFoundException::new);
        masterEntityManager.createQuery("DELETE FROM GotTags WHERE id = :id")
                .setParameter("id", id)
                .executeUpdate();
        return Optional.ofNullable(result);
    }

    @Transactional("slaveTransactionManager")
    public Optional<GotTags> findByContentIdAndTagId(String contentId, Long tagId)
            throws ChangeSetPersister.NotFoundException {
        String query = "SELECT g FROM GotTags g WHERE g.contentId = :contentId AND g.tagId = :tagId";
        return slaveEntityManager.createQuery(query, GotTags.class)
                .setParameter("contentId", contentId)
                .setParameter("tagId", tagId)
                .getResultStream()
                .findFirst();
    }

}
