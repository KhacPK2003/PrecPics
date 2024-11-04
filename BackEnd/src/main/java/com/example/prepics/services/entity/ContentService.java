package com.example.prepics.services.entity;

import com.example.prepics.entity.Content;
import jakarta.persistence.EntityExistsException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ContentService {

    Optional<Content> delete(Long Long, boolean type) throws ChangeSetPersister.NotFoundException, IOException;

    Optional<Content> update(Content entity, boolean type) throws ChangeSetPersister.NotFoundException;

    Optional<Content> create(Content entity, boolean type) throws EntityExistsException, ChangeSetPersister.NotFoundException;

    Optional<Content> findById(Class<Content> clazz, Long Long, boolean type) throws ChangeSetPersister.NotFoundException;

    Optional<List<Content>> findAll(Class<Content> clazz, boolean type) throws ChangeSetPersister.NotFoundException;

    Optional<byte[]>  getByteArrayFromImageURL(String url);

    Optional<String> processAndSaveVideo(MultipartFile file) throws IOException;

    Optional<byte[]> getVideo(String fileName) throws IOException;

    void deleteVideo(String fileName) throws IOException;

    Optional<byte[]> extractImageAsBase64(String fileName);
}