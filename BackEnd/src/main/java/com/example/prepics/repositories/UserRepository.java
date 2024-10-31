package com.example.prepics.repositories;

import com.example.prepics.entity.User;
import com.example.prepics.interfaces.CRUDInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import jakarta.persistence.TypedQuery;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements CRUDInterface<User, String> {

    @PersistenceContext(unitName = "masterEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager masterEntityManager;

    @PersistenceContext(unitName = "slaveEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
    private EntityManager slaveEntityManager;

    @Override
    @Transactional("masterTransactionManager")
    public List<User> findAll(Class<User> clazz, boolean isActive) throws ChangeSetPersister.NotFoundException {
        Optional<List<User>> result = Optional.ofNullable(masterEntityManager
                .createQuery("SELECT a FROM User a", User.class).getResultList());
        if (result.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        return result.orElse(null);
    }

    @Override
    @Transactional("slaveTransactionManager")
    public User findById(Class<User> clazz, String id) throws ChangeSetPersister.NotFoundException {
        Optional<User> result = Optional.ofNullable(slaveEntityManager.find(clazz, id));
        if (result.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        return result.orElse(null);
    }

    @Override
    @Transactional("masterTransactionManager")
    public User create(User entity) {
        entity.setIsAdmin(false);
        entity.setIsActive(true);
        masterEntityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional("masterTransactionManager")
    public User update(User entity) {
        return masterEntityManager.merge(entity);
    }

    @Override
    @Transactional("masterTransactionManager")
    public User delete(String id) throws ChangeSetPersister.NotFoundException {
        Optional<User> result = Optional.ofNullable(slaveEntityManager.find(User.class, id));
        if (result.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        masterEntityManager.remove(masterEntityManager.contains(result.get()) ? result.get()
                : masterEntityManager.merge(result.get()));
        return result.get();
    }

    @Transactional("masterTransactionManager")
    public User findByEmail(Class<User> clazz, String email) {
        TypedQuery<User> typedQuery = slaveEntityManager.createQuery("SELECT a FROM User a WHERE a.email = :email", clazz);
        typedQuery.setParameter("email", email);
        Optional<List<User>> result = Optional.ofNullable(typedQuery.getResultList());
        if (result.isPresent() && !result.get().isEmpty()) {
            return result.get().get(0);
        } else {
            return null;
        }
    }
}
