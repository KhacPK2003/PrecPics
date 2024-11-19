package com.example.prepics.services.api;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.prepics.dto.ContentDTO;
import com.example.prepics.entity.Content;
import com.example.prepics.entity.Tag;
import com.example.prepics.entity.User;
import com.example.prepics.models.ResponseProperties;
import com.example.prepics.models.TagESDocument;
import com.example.prepics.repositories.ContentRepository;
import com.example.prepics.services.cloudinary.CloudinaryService;
import com.example.prepics.services.elasticsearch.ElasticSearchService;
import com.example.prepics.services.elasticsearch.TagESDocumentService;
import com.example.prepics.services.entity.ContentService;
import com.example.prepics.services.entity.GotTagsService;
import com.example.prepics.services.entity.TagService;
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
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private ElasticSearchService elasticSearchService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private TagESDocumentService tagESDocumentService;

    private User getAuthenticatedUser(Authentication authentication) throws ChangeSetPersister.NotFoundException {
        User userDecode = (User) authentication.getPrincipal();
        return userService.findByEmail(User.class, userDecode.getEmail())
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
    }



    public Map<String, Object> uploadContent(Authentication authentication, MultipartFile file, ContentDTO contentDTO)
            throws Exception {
        User user = getAuthenticatedUser(authentication);
        if (file.isEmpty()) {
            return ResponseProperties.createResponse(400, "Error : File is empty", null);
        }
        // Lưu tệp tạm thời
        byte[] fileBytes = file.getBytes();
        boolean isImage = contentDTO.getType() == 0;
        String hashData = isImage
                ? contentService.calculateImageHash(file)
                : contentService.calculateVideoHash(file);
        if ((isImage && contentService.isExistImageData(hashData))
                || (!isImage && contentService.isExistVideoData(hashData))) {
            String fileType = isImage ? "Image" : "Video";
            return ResponseProperties
                    .createResponse(400, "Error: " + fileType + " đã tồn tại", null);
        }

        // Upload file to Cloudinary
        Map<String, Object> fileUpload = isImage ? cloudinaryService.uploadFile(file)
                : cloudinaryService.uploadVideo(fileBytes);

        Content content = new Content();
        content.setId(fileUpload.get("public_id").toString());
        content.setName(file.getOriginalFilename());
        content.setAssetId(fileUpload.get("asset_id").toString());
        content.setHeight((Integer) fileUpload.get("height"));
        content.setWidth((Integer) fileUpload.get("width"));
        content.setDataUrl(fileUpload.get("url").toString());
        content.setDataByte(hashData);
        content.setDescription(contentDTO.getDescription());
        content.setType(isImage);
        content.setDateUpload(BigInteger.valueOf(new Date().getTime()));
//        content.setUserId(user.getId());
        content.setUser(user);

        contentService.create(content);

        List<String> tagNames = List.of(contentDTO.getTags().split(","));
        tagNames.forEach(tagName -> {
            try {
                gotTagsService.addTagByName(content.getId(), tagName);
            } catch (ChangeSetPersister.NotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        return ResponseProperties.createResponse(200, "Success", content);
    }

    public Map<String, Object> deleteContent(Authentication authentication, String id)
            throws IOException, ChangeSetPersister.NotFoundException {

        Content content = contentService.findById(Content.class, id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

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

        return ResponseProperties.createResponse(200, "Success", true);
    }

    public Map<String, Object> findAllContent() throws ChangeSetPersister.NotFoundException {
        List<Content> contents = contentService.findAll(Content.class)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        return ResponseProperties.createResponse(200, "Success", contents);
    }

    public Map<String, Object> findAllByType(boolean type, Integer page, Integer size) throws ChangeSetPersister.NotFoundException {
        List<Content> contents = contentService.findAll(Content.class, type, page, size)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        return ResponseProperties.createResponse(200, "Success", contents);
    }

    public Map<String, Object> findAllByTags(List<String> tags, Integer page, Integer size) throws ChangeSetPersister.NotFoundException {
        List<Content> contents = contentService.findContentsByTags(tags, page, size)
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

    public Map<String, Object> doSearchWithFuzzy(String indexName, String fieldName, String approximates
            , Integer page, Integer size) {
        List<String> tagNames = List.of(approximates.split(","));
        Set<String> tags = new HashSet<>();

        try {
            tagNames.forEach(tag -> {
                try {
                    SearchResponse searchResponse =
                            elasticSearchService.fuzzySearch(TagESDocument.class , indexName, fieldName, tag);
                    List<Hit<TagESDocument>> hitList = searchResponse.hits().hits();
                    hitList.forEach(hit -> tags.add(hit.source().getName()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            List<Content> result = contentService.findContentsByTags(tags.stream().toList(), page, size)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);
            System.out.println(tags.toString());
            return ResponseProperties.createResponse(200, "Success", result);
        } catch (Exception e) {

            return ResponseProperties.createResponse(500, "Unexpected error during fuzzy search", e);
        }
    }

    public Map<String, Object> doInsertTagsIntoElastic(Authentication authentication) {
        try {
            // Lấy danh sách nội dung
            List<Tag> tags = tagService.findAll(Tag.class)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            // Lưu nội dung vào Elasticsearch
            Iterable<TagESDocument> result = tagESDocumentService.saveAll(tags)
                    .orElseThrow(() -> new RuntimeException("Error inserting tags into Elasticsearch"));

            return ResponseProperties.createResponse(200, "Success", result);
        } catch (RuntimeException | ChangeSetPersister.NotFoundException e) {

            return ResponseProperties.createResponse(404, e.getMessage(), null);
        }
    }

    public Map<String, Object> doDeleteTagsInElastic(Authentication authentication) {
        try {
            // Xóa toàn bộ nội dung trong Elasticsearch
            tagESDocumentService.deleteAll();

            return ResponseProperties.createResponse(200, "Success", true);
        } catch (RuntimeException e) {

            return ResponseProperties.createResponse(404, e.getMessage(), null);
        } catch (Exception e) {

            return ResponseProperties.createResponse(500, "Unexpected error occurred", null);
        }
    }
}
