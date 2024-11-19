package com.example.prepics.services.entity;

import com.example.prepics.entity.Content;
import com.example.prepics.interfaces.CRUDInterface;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ContentService extends CRUDInterface<Content, String> {


    Optional<List<Content>> findAll(Class<Content> clazz, boolean type, Integer page, Integer size) throws ChangeSetPersister.NotFoundException;

    Optional<List<Content>> findContentsByTags(List<String> tags, Integer page, Integer size);

    Optional<File> changeResolutionForImage(String url, int width, int height) throws IOException;

    Optional<File> changeResolutionForVideo(String url, int width, int height) throws IOException;

    String calculateImageHash(MultipartFile imagePath) throws Exception;

    String calculateVideoHash(MultipartFile imagePath) throws Exception;

    boolean isExistImageData(String dataByte) throws ChangeSetPersister.NotFoundException;

    boolean isExistVideoData(String dataByte) throws ChangeSetPersister.NotFoundException;

}