package com.example.prepics.repositories;

import com.example.prepics.entity.Comment;
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
public class CommentRepository implements CRUDInterface<Comment, Long> {

  @PersistenceContext(unitName = "masterEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
  private EntityManager masterEntityManager;

  @PersistenceContext(unitName = "slaveEntityManagerFactory", type = PersistenceContextType.TRANSACTION)
  private EntityManager slaveEntityManager;

  @Override
  @Transactional("masterTransactionManager")
  public Optional<List<Comment>> findAll(Class<Comment> clazz)
      throws ChangeSetPersister.NotFoundException {
    return Optional.ofNullable(masterEntityManager
        .createQuery("SELECT a FROM Comment a  ORDER BY a.dateCreate DESC", Comment.class)
        .getResultList());
  }

  @Override
  @Transactional("slaveTransactionManager")
  public Optional<Comment> findById(Class<Comment> clazz, Long id)
      throws ChangeSetPersister.NotFoundException {
    return Optional.ofNullable(slaveEntityManager.find(clazz, id));
  }

  @Override
  @Transactional("masterTransactionManager")
  public Optional<Comment> create(Comment entity) {
    User user = masterEntityManager.find(User.class, entity.getUserId());
    entity.setUser(user);
    masterEntityManager.persist(entity);
    masterEntityManager.flush();
    return Optional.ofNullable(masterEntityManager.find(Comment.class, entity.getId()));
  }

  @Override
  @Transactional("masterTransactionManager")
  public Optional<Comment> update(Comment entity) {
    return Optional.ofNullable(masterEntityManager.merge(entity));
  }

  @Override
  @Transactional("masterTransactionManager")
  public Optional<Comment> delete(Long id) throws ChangeSetPersister.NotFoundException {
    Comment result = Optional.ofNullable(slaveEntityManager.find(Comment.class, id))
        .orElseThrow(ChangeSetPersister.NotFoundException::new);

    masterEntityManager.createQuery("DELETE FROM Comment WHERE id = :id")
        .setParameter("id", id)
        .executeUpdate();

    return Optional.ofNullable(result);
  }

  @Transactional("slaveTransactionManager")
  public Optional<List<Comment>> findAllByContentId(String contentId)
      throws ChangeSetPersister.NotFoundException {
    String query = "SELECT a FROM Comment a WHERE a.contentId = :contentId  ORDER BY a.dateCreate ASC";
    return Optional.ofNullable(slaveEntityManager.createQuery(query, Comment.class)
        .setParameter("contentId", contentId)
        .getResultList());
  }
}