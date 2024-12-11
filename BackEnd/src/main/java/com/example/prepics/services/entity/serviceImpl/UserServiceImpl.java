package com.example.prepics.services.entity.serviceImpl;

import com.example.prepics.dto.UserStatisticsDTO;
import com.example.prepics.entity.User;
import com.example.prepics.repositories.UserRepository;
import com.example.prepics.services.entity.UserService;
import jakarta.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public Optional<User> findByEmail(Class<User> clazz, String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public Optional<UserStatisticsDTO> getUserStatistics(String userId) {
    return userRepository.getUserStatistics(userId);
  }

  @Override
  public Optional<User> delete(String s) throws ChangeSetPersister.NotFoundException {
    return userRepository.delete(s);
  }

  @Override
  public Optional<User> update(User entity) {
    return userRepository.update(entity);
  }

  @Override
  public Optional<User> create(User entity)
      throws EntityExistsException, ChangeSetPersister.NotFoundException {
    return userRepository.create(entity);
  }

  @Override
  public Optional<User> findById(Class<User> clazz, String s)
      throws ChangeSetPersister.NotFoundException {
    return userRepository.findById(clazz, s);
  }

  @Override
  public Optional<List<User>> findAll(Class<User> clazz)
      throws ChangeSetPersister.NotFoundException {
    return userRepository.findAll(clazz);
  }
}