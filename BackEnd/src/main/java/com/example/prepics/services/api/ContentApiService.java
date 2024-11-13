package com.example.prepics.services.api;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.prepics.entity.Content;
import com.example.prepics.entity.User;
import com.example.prepics.models.ContentElasticSearch;
import com.example.prepics.models.ResponseProperties;
import com.example.prepics.repositories.ContentRepository;
import com.example.prepics.services.cloudinary.CloudinaryService;
import com.example.prepics.services.elasticsearch.ContentElasticSearchService;
import com.example.prepics.services.elasticsearch.ElasticSearchService;
import com.example.prepics.services.entity.ContentService;
import com.example.prepics.services.entity.GotTagsService;
import com.example.prepics.services.entity.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import java.math.BigInteger;
import java.nio.file.Files;
import java.util.*;

@Service
public class ContentApiService {

    @Autowired
    private ContentService contentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private GotTagsService gotTagsService;

    @Autowired
    private ContentElasticSearchService contentElasticSearchService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private ContentRepository contentRepository;

    private User getAuthenticatedUser(Authentication authentication) throws ChangeSetPersister.NotFoundException {
        User userDecode = (User) authentication.getPrincipal();
        return userService.findByEmail(User.class, userDecode.getEmail())
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
    }

    private void getAdminUser(Authentication authentication) throws ChangeSetPersister.NotFoundException {
        User user = getAuthenticatedUser(authentication);
        if (!user.getIsAdmin()) {
            throw new RuntimeException("User is not an admin");
        }
    }

    public Map<String, Object> uploadContent(Authentication authentication, MultipartFile file, Map<String, Object> model)
            throws IOException, ChangeSetPersister.NotFoundException {
        User user = getAuthenticatedUser(authentication);

        if (file.isEmpty()) {
            return ResponseProperties.createResponse(400, "Error : File is empty", null);
        }

        boolean isImage = Integer.parseInt(model.get("type").toString()) == 0;
        Map<String, Object> fileUpload =
                isImage ? cloudinaryService.uploadFile(file) : cloudinaryService.uploadVideo(file);

        Content content = new Content();
        content.setId(fileUpload.get("public_id").toString());
        content.setName(file.getOriginalFilename());
        content.setAssetId(fileUpload.get("asset_id").toString());
        content.setHeight((Integer) fileUpload.get("height"));
        content.setWidth((Integer) fileUpload.get("width"));
        content.setDataUrl(fileUpload.get("url").toString());
        content.setType(isImage);
        content.setDateUpload(BigInteger.valueOf(new Date().getTime()));
        content.setUserId(user.getId());

        contentService.create(content);

        List<String> tagNames = List.of(model.get("tags").toString().split(","));
        tagNames.forEach(tagName -> {
            try {
                gotTagsService.addTagByName(content.getId(), tagName);
            } catch (ChangeSetPersister.NotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        contentElasticSearchService.insertContent(content);

        return ResponseProperties.createResponse(200, "Success", content);
    }

    public Map<String, Object> deleteContent(Authentication authentication, String id)
            throws IOException, ChangeSetPersister.NotFoundException {
        getAuthenticatedUser(authentication);

        Content content = contentService.findById(Content.class, id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        contentElasticSearchService.delete(modelMapper.map(content, ContentElasticSearch.class));

        Map<String, Object> fileUpload = cloudinaryService.deleteFile(id);

        return ResponseProperties.createResponse(200, "Success", fileUpload);
    }

    public Map<String, Object> updateTags(Authentication authentication, String contentId, String tags)
            throws ChangeSetPersister.NotFoundException {
        User user = getAuthenticatedUser(authentication);

        Content content = contentRepository.findById(Content.class, contentId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        if (!content.getUserId().equals(user.getId())) {

            return ResponseProperties
                    .createResponse(403, "Error : User does not own this content", null);
        }

        List<String> tagNames = List.of(tags.split(","));
        tagNames.forEach(tagName -> {
            try {
                gotTagsService.addTagByName(contentId, tagName);
            } catch (ChangeSetPersister.NotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        contentElasticSearchService.insertContent(content);

        return ResponseProperties.createResponse(200, "Success", true);
    }

    public Map<String, Object> findAllContent() throws ChangeSetPersister.NotFoundException {
        List<Content> contents = contentService.findAll(Content.class)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        return ResponseProperties.createResponse(200, "Success", contents);
    }

    public Map<String, Object> findAllByType(boolean type) throws ChangeSetPersister.NotFoundException {
        List<Content> contents = contentService.findAll(Content.class, type)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        return ResponseProperties.createResponse(200, "Success", contents);
    }

    public Map<String, Object> findAllByTags(String tags) throws ChangeSetPersister.NotFoundException {
        List<String> tagNames = List.of(tags.split(","));
        List<Content> contents = contentService.findAllByTags(tagNames)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        return ResponseProperties.createResponse(200, "Success", contents);
    }

    public Map<String, Object> findContentById(String id) throws ChangeSetPersister.NotFoundException {
        Content content = contentService.findById(Content.class, id)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        return ResponseProperties.createResponse(200, "Success", content);
    }

    private byte[] getContentWithSize(Authentication authentication, Map<String, Object> model, boolean isImage)
            throws IOException, ChangeSetPersister.NotFoundException {
        User user = getAuthenticatedUser(authentication);

        Content content = modelMapper.map(model.get("content"), Content.class);

        int width = Integer.parseInt(model.get("width").toString());
        int height = Integer.parseInt(model.get("height").toString());

        Optional<File> result = isImage
                ? contentService.changeResolutionForImage(content.getDataUrl(), width, height)
                : contentService.changeResolutionForVideo(content.getDataUrl(), width, height);

        return result.map(file -> {
            try {
                byte[] data = Files.readAllBytes(file.toPath());
                file.deleteOnExit();

                return data;
            } catch (IOException e) {
                throw new RuntimeException("Failed to read file", e);
            }
        }).orElse(null);
    }

    public byte[] getImageWithSize(Authentication authentication, Map<String, Object> model)
            throws IOException, ChangeSetPersister.NotFoundException {

        return getContentWithSize(authentication, model, true);
    }

    public byte[] getVideoWithSize(Authentication authentication, Map<String, Object> model)
            throws IOException, ChangeSetPersister.NotFoundException {

        return getContentWithSize(authentication, model, false);
    }

    public Map<String, Object> doSearchWithFuzzy(String indexName, String fieldName, String approximates) {
        List<String> tags = List.of(approximates.split(","));
        Set<ContentElasticSearch> result = new HashSet<>();

        try {
            tags.forEach(tag -> {
                try {
                    SearchResponse searchResponse = elasticSearchService.fuzzySearch(indexName, fieldName, tag);
                    List<Hit<ContentElasticSearch>> hitList = searchResponse.hits().hits();
                    hitList.forEach(hit -> result.add(hit.source()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {

            return ResponseProperties.createResponse(500, "Unexpected error during fuzzy search", e);
        }

        return ResponseProperties.createResponse(200, "Success", result);
    }

    public Map<String, Object> doInsertContentsIntoElastic(Authentication authentication) {
        try {
            // Kiểm tra quyền Admin
            getAdminUser(authentication);

            // Lấy danh sách nội dung
            List<Content> contents = contentService.findAll(Content.class)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            // Lưu nội dung vào Elasticsearch
            Iterable<ContentElasticSearch> result = contentElasticSearchService.saveAll(contents)
                    .orElseThrow(() -> new RuntimeException("Error inserting contents into Elasticsearch"));

            return ResponseProperties.createResponse(200, "Success", result);
        } catch (RuntimeException | ChangeSetPersister.NotFoundException e) {

            return ResponseProperties.createResponse(404, e.getMessage(), null);
        }
    }

    public Map<String, Object> doDeleteContentsInElastic(Authentication authentication) {
        try {
            // Kiểm tra quyền Admin
            getAdminUser(authentication);

            // Xóa toàn bộ nội dung trong Elasticsearch
            contentElasticSearchService.deleteAll();

            return ResponseProperties.createResponse(200, "Success", true);
        } catch (RuntimeException e) {

            return ResponseProperties.createResponse(404, e.getMessage(), null);
        } catch (Exception e) {

            return ResponseProperties.createResponse(500, "Unexpected error occurred", null);
        }
    }
}
