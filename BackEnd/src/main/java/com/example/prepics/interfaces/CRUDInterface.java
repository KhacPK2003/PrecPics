package com.example.prepics.interfaces;

import jakarta.persistence.EntityExistsException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import java.util.List;
import java.util.Optional;

public interface CRUDInterface<Type, Id> {

	Optional<Type> delete(Id id) throws NotFoundException;

	Optional<Type> update(Type entity);

	Optional<Type> create(Type entity) throws EntityExistsException, NotFoundException;

	Optional<Type> findById(Class<Type> clazz, Id id) throws NotFoundException;

	Optional<List<Type>> findAll(Class<Type> clazz) throws NotFoundException;

}
