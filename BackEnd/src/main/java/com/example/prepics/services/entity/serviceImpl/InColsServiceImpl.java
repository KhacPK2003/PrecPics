package com.example.prepics.services.entity.serviceImpl;

import com.example.prepics.entity.InCols;
import com.example.prepics.repositories.InColsRepository;
import com.example.prepics.services.entity.InColsService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InColsServiceImpl implements InColsService {

    @Autowired
    private InColsRepository inColsRepository;

    @Override
    public Optional<InCols> delete(Long aLong) throws ChangeSetPersister.NotFoundException {
        return inColsRepository.delete(aLong);
    }

    @Override
    public Optional<InCols> update(InCols entity) {
        return inColsRepository.update(entity);
    }

    @Override
    public Optional<InCols> create(InCols entity) throws EntityExistsException, ChangeSetPersister.NotFoundException {
        return inColsRepository.create(entity);
    }

    @Override
    public Optional<InCols> findById(Class<InCols> clazz, Long aLong) throws ChangeSetPersister.NotFoundException {
        return inColsRepository.findById(clazz, aLong);
    }

    @Override
    public Optional<List<InCols>> findAll(Class<InCols> clazz) throws ChangeSetPersister.NotFoundException {
        return inColsRepository.findAll(clazz);
    }
}
