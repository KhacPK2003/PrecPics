package com.example.prepics.interfaces;

import jakarta.persistence.EntityExistsException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import java.util.List;

public interface CRUDInterface<Type, Id> {

	Type delete(Id id) throws NotFoundException;

	Type update(Type entity);

	Type create(Type entity) throws EntityExistsException, NotFoundException;

	Type findById(Class<Type> clazz, Id id) throws NotFoundException;

	List<Type> findAll(Class<Type> clazz, boolean isDelete) throws NotFoundException;

}
