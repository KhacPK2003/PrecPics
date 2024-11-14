package com.example.prepics.services.entity;

import com.example.prepics.entity.Content;
import com.example.prepics.interfaces.CRUDInterface;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ContentService extends CRUDInterface<Content, String> {


    Optional<List<Content>> findAll(Class<Content> clazz, boolean type) throws ChangeSetPersister.NotFoundException;

    Optional<byte[]>  getByteArrayFromImageURL(String url);

    Optional<List<Content>> findAllByTags(List<String> tags);

    Optional<File> changeResolutionForImage(String url, int width, int height) throws IOException;

    Optional<File> changeResolutionForVideo(String url, int width, int height) throws IOException;

}