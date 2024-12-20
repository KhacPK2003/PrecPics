package com.example.prepics.repositories;

import com.example.prepics.entity.Followers;
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
public class FollowerRepository implements CRUDInterface<Followers, Long> {

  @PersistenceContext(unitName = "masterEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
  private EntityManager masterEntityManager;

  @PersistenceContext(unitName = "slaveEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
  private EntityManager slaveEntityManager;

  @Override
  @Transactional("masterTransactionManager")
  public Optional<List<Followers>> findAll(Class<Followers> clazz)
      throws ChangeSetPersister.NotFoundException {
    return Optional.ofNullable(masterEntityManager
        .createQuery("SELECT a FROM Followers a", Followers.class).getResultList());
  }

  @Override
  @Transactional("slaveTransactionManager")
  public Optional<Followers> findById(Class<Followers> clazz, Long id)
      throws ChangeSetPersister.NotFoundException {
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
    Followers result = Optional.ofNullable(slaveEntityManager.find(Followers.class, id))
        .orElseThrow(ChangeSetPersister.NotFoundException::new);

    masterEntityManager.createQuery("DELETE FROM Followers WHERE id = :id")
        .setParameter("id", id)
        .executeUpdate();
    return Optional.of(result);
  }

  @Transactional("slaveTransactionManager")
  public Optional<Followers> findByUserIdAndFollowerId(Class<Followers> clazz, String followeeId,
      String followerId) {
    String query = "SELECT a FROM Followers a WHERE a.userId = :followeeId and a.followerId = :followerId";
    return slaveEntityManager.createQuery(query, clazz)
        .setParameter("followeeId", followeeId)
        .setParameter("followerId", followerId)
        .getResultStream()
        .findFirst();
  }
}
