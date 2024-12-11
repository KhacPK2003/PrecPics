package com.example.prepics.interfaces;

import jakarta.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

public interface CRUDInterface<Type, Id> {

  Optional<Type> delete(Id id) throws NotFoundException;

  Optional<Type> update(Type entity) throws NotFoundException;

  Optional<Type> create(Type entity) throws EntityExistsException, NotFoundException;

  Optional<Type> findById(Class<Type> clazz, Id id) throws NotFoundException;

  Optional<List<Type>> findAll(Class<Type> clazz) throws NotFoundException;

}