package com.example.prepics.repositories;

import com.example.prepics.entity.Collection;
import com.example.prepics.interfaces.CRUDInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CollectionRepository implements CRUDInterface<Collection, Long> {

  @PersistenceContext(unitName = "masterEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
  private EntityManager masterEntityManager;

  @PersistenceContext(unitName = "slaveEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
  private EntityManager slaveEntityManager;

  @Override
  @Transactional("masterTransactionManager")
  public Optional<List<Collection>> findAll(Class<Collection> clazz)
      throws ChangeSetPersister.NotFoundException {
    return Optional.ofNullable(masterEntityManager
        .createQuery("SELECT a FROM Collection a", Collection.class).getResultList());
  }

  @Override
  @Transactional("slaveTransactionManager")
  public Optional<Collection> findById(Class<Collection> clazz, Long id)
      throws ChangeSetPersister.NotFoundException {
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
    Collection result = Optional.ofNullable(slaveEntityManager.find(Collection.class, id))
        .orElseThrow(ChangeSetPersister.NotFoundException::new);

    masterEntityManager.createQuery("DELETE FROM Collection WHERE id = :id")
        .setParameter("id", id)
        .executeUpdate();
    return Optional.ofNullable(result);
  }

  @Transactional("slaveTransactionManager")
  public Optional<List<Collection>> getUserCollection(String userId)
      throws ChangeSetPersister.NotFoundException {
    String query = "SELECT c FROM Collection c WHERE c.userId = :userId";
    List<Collection> result = slaveEntityManager.createQuery(query, Collection.class)
        .setParameter("userId", userId)
        .getResultList();
    return Optional.of(result);
  }

  @Transactional("slaveTransactionManager")
  public Optional<Collection> getUserCollectionByName(String userId, String collectionName)
      throws ChangeSetPersister.NotFoundException {
    String query = "SELECT c FROM Collection c WHERE c.userId = :userId and c.name = :collectionName";
    return slaveEntityManager.createQuery(query, Collection.class)
        .setParameter("userId", userId)
        .setParameter("collectionName", collectionName)
        .getResultStream()
        .findFirst();
  }
}