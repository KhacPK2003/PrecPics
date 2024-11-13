package com.example.prepics.repositories;

import com.example.prepics.entity.Followers;
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
public class FollowerRepository implements CRUDInterface<Followers, Long> {

    @PersistenceContext(unitName = "masterEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager masterEntityManager;

    @PersistenceContext(unitName = "slaveEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager slaveEntityManager;

    @Override
    @Transactional("masterTransactionManager")
    public Optional<List<Followers>> findAll(Class<Followers> clazz) throws ChangeSetPersister.NotFoundException {
        return Optional.ofNullable(masterEntityManager
                .createQuery("SELECT a FROM Followers a", Followers.class).getResultList());
    }

    @Override
    @Transactional("slaveTransactionManager")
    public Optional<Followers> findById(Class<Followers> clazz, Long id) throws ChangeSetPersister.NotFoundException {
        return Optional.ofNullable(slaveEntityManager.find(clazz, id));
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<Followers> create(Followers entity) {
        masterEntityManager.persist(entity);
        return Optional.ofNullable(entity);
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<Followers> update(Followers entity) {
        return Optional.ofNullable(masterEntityManager.merge(entity));
    }

    @Override
    @Transactional("masterTransactionManager")
    public Optional<Followers> delete(Long id) throws ChangeSetPersister.NotFoundException {
        Optional<Followers> result = Optional.ofNullable(slaveEntityManager.find(Followers.class, id));
        if (result.isEmpty()) {
            return Optional.empty();
        }
        masterEntityManager.remove(masterEntityManager.contains(result.get()) ? result.get()
                : masterEntityManager.merge(result.get()));
        return result;
    }

    @Transactional("slaveTransactionManager")
    public Optional<Followers> findByUserIdAndFollowerId(Class<Followers> clazz, String userId, String followerId)
            throws ChangeSetPersister.NotFoundException {
        String query = "SELECT a FROM Followers a WHERE a.userId = :userId and a.followerId = :followerId";
        return slaveEntityManager.createQuery(query, Followers.class)
                .setParameter("userId", userId)
                .setParameter("followerId", followerId)
                .getResultStream()
                .findFirst();
    }
}
