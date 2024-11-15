package com.example.prepics.repositories;

import com.example.prepics.entity.Content;
import com.example.prepics.entity.GotTags;
import com.example.prepics.entity.Tag;
import com.example.prepics.interfaces.CRUDInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ContentRepository implements CRUDInterface<Content, String> {
    @PersistenceContext(unitName = "masterEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager masterEntityManager;

    @PersistenceContext(unitName = "slaveEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager slaveEntityManager;

    @Override
    @Transactional("masterTransactionManager")
    public Optional<List<Content>> findAll(Class<Content> clazz) throws ChangeSetPersister.NotFoundException {
        return Optional.ofNullable(masterEntityManager
                .createQuery("SELECT a FROM Content a", Content.class).getResultList());
    }

    @Override
    @Transactional("slaveTransactionManager")
    public Optional<Content> findById(Class<Content> clazz, String id) throws ChangeSetPersister.NotFoundException {
        return Optional.ofNullable(slaveEntityManager.find(clazz, id));
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<Content> create(Content entity) {
        entity.setPublic(true);
        masterEntityManager.persist(entity);
        return Optional.of(entity);
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<Content> update(Content entity) {
        return Optional.ofNullable(masterEntityManager.merge(entity));
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<Content> delete(String id) throws ChangeSetPersister.NotFoundException {
        Optional<Content> result = Optional.ofNullable(slaveEntityManager.find(Content.class, id));
        if (result.isEmpty()) {
            return Optional.empty();
        }
        masterEntityManager.remove(masterEntityManager.contains(result.get()) ? result.get()
                : masterEntityManager.merge(result.get()));
        return result;
    }

    @Transactional("slaveTransactionManager")
    public Optional<List<Content>> findAllByType(boolean type, Integer page, Integer size) {
        String query = "SELECT c FROM Content c WHERE c.type = :type ORDER BY c.dateUpload DESC";
        List<Content> result = slaveEntityManager.createQuery(query, Content.class)
                .setParameter("type", type)
                .setMaxResults(size)
                .setFirstResult((page - 1) * size)
                .getResultList();
        return Optional.of(result);
    }

    @Transactional("slaveTransactionManager")
    public Optional<String> findTagsByContentId(String contentId) {
        String query = "SELECT t.name FROM Tag t " +
                "JOIN GotTags g ON t.id = g.tagId " +
                "WHERE g.contentId = :contentId";
        List<String> result = slaveEntityManager.createQuery(query, String.class)
                .setParameter("contentId", contentId)
                .getResultList();

        return result.isEmpty() ? Optional.empty() : Optional.of(String.join(", ", result));
    }

    @Transactional("slaveTransactionManager")
    public Optional<List<Content>> findContentsByTags(String tags, Integer page, Integer size) {
        String query ="SELECT c FROM Content c " +
                "JOIN GotTags gt on c.id = gt.contentId " +
                "JOIN Tag t on gt.tagId = t.id " +
                "WHERE LOWER(t.name) IN :nameTag " +
                "ORDER BY c.dateUpload DESC";
        return Optional.ofNullable(slaveEntityManager.createQuery(query, Content.class)
                .setParameter("nameTag", tags.toLowerCase())
                .setMaxResults(size)
                .setFirstResult((page - 1) * size)
                .getResultList());
    }
}
