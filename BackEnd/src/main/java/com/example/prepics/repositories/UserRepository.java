package com.example.prepics.repositories;

import com.example.prepics.dto.UserStatisticsDTO;
import com.example.prepics.entity.User;
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
public class UserRepository implements CRUDInterface<User, String> {

  @PersistenceContext(unitName = "masterEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
  private EntityManager masterEntityManager;

  @PersistenceContext(unitName = "slaveEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
  private EntityManager slaveEntityManager;

  @Override
  @Transactional("masterTransactionManager")
  public Optional<List<User>> findAll(Class<User> clazz)
      throws ChangeSetPersister.NotFoundException {
    return Optional.ofNullable(masterEntityManager
        .createQuery("SELECT a FROM User a", User.class).getResultList());
  }

  @Override
  @Transactional("slaveTransactionManager")
  public Optional<User> findById(Class<User> clazz, String id)
      throws ChangeSetPersister.NotFoundException {
    return Optional.ofNullable(slaveEntityManager.find(clazz, id));
  }

  @Override
  @Transactional("masterTransactionManager")
  public Optional<User> create(User entity) {
    entity.setIsAdmin(false);
    entity.setIsActive(true);
    masterEntityManager.persist(entity);
    return Optional.of(entity);
  }

  @Override
  @Transactional("masterTransactionManager")
  public Optional<User> update(User entity) {
    return Optional.ofNullable(masterEntityManager.merge(entity));
  }

  @Override
  @Transactional("masterTransactionManager")
  public Optional<User> delete(String id) throws ChangeSetPersister.NotFoundException {
    User result = Optional.ofNullable(slaveEntityManager.find(User.class, id))
        .orElseThrow(ChangeSetPersister.NotFoundException::new);

    masterEntityManager.createQuery("DELETE FROM User u WHERE u.id = :id")
        .setParameter("id", id)
        .executeUpdate();
    return Optional.ofNullable(result);
  }

  @Transactional("slaveTransactionManager")
  public Optional<User> findByEmail(String email) {
    String query = "SELECT u FROM User u WHERE u.email = :email";
    return slaveEntityManager.createQuery(query, User.class)
        .setParameter("email", email)
        .getResultStream()
        .findFirst();
  }

  public Optional<UserStatisticsDTO> getUserStatistics(String userId) {
    String sql = """
        SELECT 
            (SELECT COUNT(*) FROM content WHERE user_id = ?1) AS totalContents,
            (SELECT COUNT(*) FROM followers WHERE user_id = ?1) AS totalFollowers,
            (SELECT COUNT(*) FROM followees WHERE user_id = ?1) AS totalFollowing,
            COALESCE((SELECT SUM(liked) FROM content WHERE user_id = ?1), 0) AS totalLikes
        """;

    return Optional.ofNullable(
        (UserStatisticsDTO) slaveEntityManager.createNativeQuery(sql, "UserStatisticsMapping")
            .setParameter(1, userId)
            .getSingleResult());
  }
}