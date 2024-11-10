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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.IOException;

import java.math.BigInteger;
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

    public Map<String, Object> uploadContent(Authentication authentication, MultipartFile file,
                                 Map<String, Object> model) throws ChangeSetPersister.NotFoundException, IOException {
        User userDecode = (User) authentication.getPrincipal();
        Optional<User> resultUser = userService.findByEmail(User.class, userDecode.getEmail());

        if (resultUser.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : User not found", null);
        }

        if (file.isEmpty()) {
            return ResponseProperties.createResponse(400, "Error : File is empty", null);
        }

        boolean type = (Integer.parseInt(model.get("type").toString()) == 0) ;
        Map<String, Object> fileUpload = type
                ? cloudinaryService.uploadFile(file)
                : cloudinaryService.uploadVideo(file);

        Content content = new Content();

        content.setId(fileUpload.get("public_id").toString());
        content.setName(file.getOriginalFilename());
        content.setAssetId(fileUpload.get("asset_id").toString());
        content.setHeight((Integer) fileUpload.get("height"));
        content.setWidth((Integer) fileUpload.get("width"));
        content.setDataUrl(fileUpload.get("url").toString());
        content.setType(type);
        content.setDateUpload(BigInteger.valueOf(new Date().getTime()));
        content.setUserId(resultUser.get().getId());

        contentService.create(content);

        List<String> tagNames = List.of(model.get("tags").toString().split(","));

        tagNames.forEach(tagName -> {
            try {
                gotTagsService.addTagByName(fileUpload.get("public_id").toString(), tagName);
            } catch (ChangeSetPersister.NotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        contentElasticSearchService.insertContent(content);

        return  ResponseProperties.createResponse(200, "Success", content);
    }

    public Map<String, Object> deleteContent(Authentication authentication, String id)
            throws ChangeSetPersister.NotFoundException, IOException {
        User userDecode = (User) authentication.getPrincipal();
        Optional<User> resultUser = userService.findByEmail(User.class, userDecode.getEmail());

        if (resultUser.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : User not found", null);
        }

        Optional<Content> content =  contentService.findById(Content.class, id);

        if (content.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : Content not found", null);
        }

        ContentElasticSearch contentElasticSearch = new ContentElasticSearch();
        modelMapper.map(content, contentElasticSearch);

        contentElasticSearchService.delete(contentElasticSearch);

        Map<String, Object> fileUpload = cloudinaryService.deleteFile(id);

        return ResponseProperties.createResponse(200, "Success", fileUpload);
    }

    public Map<String, Object> updateTags(Authentication authentication, String contentId, String tags)
            throws ChangeSetPersister.NotFoundException {
        User userDecode = (User) authentication.getPrincipal();
        Optional<User> resultUser = userService.findByEmail(User.class, userDecode.getEmail());

        if (resultUser.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : User not found", null);
        }

        Optional<Content> content = contentRepository.findById(Content.class, contentId);

        if (content.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : Content not found", null);
        }

        if (!content.get().getUserId().equals(userDecode.getId())) {
            return ResponseProperties
                    .createResponse(403, "Error : User does not belong to this content", null);
        }

        List<String> tagNames = List.of(tags.split(","));

        for (String tagName : tagNames) {
            gotTagsService.addTagByName(contentId, tagName);
        }

        contentElasticSearchService.insertContent(content.get());

        return ResponseProperties.createResponse(200, "Success", true);
    }

    public Map<String, Object> findAllContent() throws ChangeSetPersister.NotFoundException {
        Optional<List<Content>> contents = contentService.findAll(Content.class);
        if (contents.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : Content not found", null);
        }
        return ResponseProperties.createResponse(200, "Success", contents);
    }

    public Map<String, Object> findAllByType(boolean type) throws ChangeSetPersister.NotFoundException {
        Optional<List<Content>> contents = contentService.findAll(Content.class, type);
        if (contents.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : Content not found", null);
        }
        return ResponseProperties.createResponse(200, "Success", contents);
    }

    public Map<String, Object> findAllByTags(String tags) throws ChangeSetPersister.NotFoundException {
        List<String> tagNames = List.of(tags.split(","));
        Optional<List<Content>> contents = contentService.findAllByTags(tagNames);
        if (contents.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : Content not found", null);
        }
        return ResponseProperties.createResponse(200, "Success", contents);
    }

    public Map<String, Object> findContentById(String id) throws ChangeSetPersister.NotFoundException {
        Optional<Content> content = contentService.findById(Content.class, id);
        if (content.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : Content not found", null);
        }
        return ResponseProperties.createResponse(200, "Success", content);
    }

    public Map<String, Object> getImageWithSize(Authentication authentication, Map<String, Object> model)
            throws IOException, ChangeSetPersister.NotFoundException {
        User userDecode = (User) authentication.getPrincipal();
        Optional<User> resultUser = userService.findByEmail(User.class, userDecode.getEmail());

        if (resultUser.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : Login to use service", null);
        }
        Content content = new Content();
        modelMapper.map(model.get("content"), content);

        if (!content.isType()) {
            return ResponseProperties.createResponse(404, "Error: Content type not supported", null);
        }

        int width = Integer.parseInt(model.get("width").toString());
        int height = Integer.parseInt(model.get("height").toString());

        Optional<File> result = contentService.changeResolutionForImage(content.getDataUrl(), width, height);

        if (result.isPresent()) {
            Resource fileResource = new FileSystemResource(result.get());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + result.get().getName());
            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

            ResponseEntity<Resource> fileResponse = ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(result.get().length())
                    .body(fileResource);

            return ResponseProperties.createResponse(200, "Success", fileResponse);
        }

        return ResponseProperties.createResponse(404, "Error: File not found", null);
    }

    public Map<String, Object> getVideoWithSize(Authentication authentication, Map<String, Object> model)
            throws IOException, ChangeSetPersister.NotFoundException {
        User userDecode = (User) authentication.getPrincipal();
        Optional<User> resultUser = userService.findByEmail(User.class, userDecode.getEmail());

        if (resultUser.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : Login to use service", null);
        }
        Content content = new Content();
        modelMapper.map(model.get("content"), content);

        if (content.isType()) {
            return ResponseProperties.createResponse(404, "Error: Content type not supported", null);
        }

        int width = Integer.parseInt(model.get("width").toString());
        int height = Integer.parseInt(model.get("height").toString());

        Optional<File> result = contentService.changeResolutionForVideo(content.getDataUrl(), width, height);

        if (result.isPresent()) {
            Resource videoResource = new FileSystemResource(result.get());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + result.get().getName());
            headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");

            ResponseEntity<Resource> fileResponse = ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(result.get().length())
                    .body(videoResource);

            return ResponseProperties.createResponse(200, "Success", fileResponse);
        }

        return ResponseProperties.createResponse(404, "Error: Video file not found", null);
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
                    System.err.println("ElasticSearch search failed for tag: " + tag + ", error: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            return ResponseProperties.createResponse(500, "Unexpected error during fuzzy search", e);
        }

        return ResponseProperties.createResponse(200, "Success", result);
    }

    public Map<String, Object> doInsertContentsIntoElastic(Authentication authentication)
            throws ChangeSetPersister.NotFoundException {
        User userDecode = (User) authentication.getPrincipal();
        Optional<User> resultUser = userService.findByEmail(User.class, userDecode.getEmail());

        if (resultUser.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : User not found", null);
        }

        if (!resultUser.get().getIsAdmin()){
            return  ResponseProperties.createResponse(403, "Error : User is not admin", null);
        }

        Optional<List<Content>> contents = contentService.findAll(Content.class);
        if (contents.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : Content not found", null);
        }

        Optional<Iterable<ContentElasticSearch>> result = contentElasticSearchService.saveAll(contents.get());
        if (result.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : Content not found", null);
        }
        return ResponseProperties.createResponse(200, "Success", result);
    }

    public Map<String, Object> doDeleteContentsInElastic(Authentication authentication)
            throws ChangeSetPersister.NotFoundException {
        User userDecode = (User) authentication.getPrincipal();
        Optional<User> resultUser = userService.findByEmail(User.class, userDecode.getEmail());

        if (resultUser.isEmpty()) {
            return  ResponseProperties.createResponse(404, "Error : User not found", null);
        }

        if (!resultUser.get().getIsAdmin()){
            return  ResponseProperties.createResponse(403, "Error : User is not admin", null);
        }

        try{
            contentElasticSearchService.deleteAll();
        }catch (Exception e){
            return ResponseProperties.createResponse(404, "Error : Content not found", null);
        }
        return ResponseProperties.createResponse(200, "Success", true);
    }
}
