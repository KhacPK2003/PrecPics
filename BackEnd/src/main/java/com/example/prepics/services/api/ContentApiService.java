package com.example.prepics.services.api;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.prepics.dto.ContentDTO;
import com.example.prepics.entity.Collection;
import com.example.prepics.entity.Content;
import com.example.prepics.entity.InCols;
import com.example.prepics.entity.Tag;
import com.example.prepics.entity.User;
import com.example.prepics.exception.DuplicateFileException;
import com.example.prepics.models.ResponseProperties;
import com.example.prepics.models.TagESDocument;
import com.example.prepics.repositories.ContentRepository;
import com.example.prepics.services.cloudinary.CloudinaryService;
import com.example.prepics.services.elasticsearch.ElasticSearchService;
import com.example.prepics.services.elasticsearch.TagESDocumentService;
import com.example.prepics.services.entity.CollectionService;
import com.example.prepics.services.entity.ContentService;
import com.example.prepics.services.entity.GotTagsService;
import com.example.prepics.services.entity.TagService;
import com.example.prepics.services.entity.UserService;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
  @Autowired
  private CollectionService collectionService;

  private User getAuthenticatedUser(Authentication authentication)
      throws ChangeSetPersister.NotFoundException {
    User userDecode = (User) authentication.getPrincipal();
    return userService.findByEmail(User.class, userDecode.getEmail())
        .orElseThrow(ChangeSetPersister.NotFoundException::new);
  }

  @Transactional("masterTransactionManager")
  public ResponseEntity<?> uploadContent(Authentication authentication, MultipartFile file,
      ContentDTO contentDTO)
      throws Exception {
    try {
      User user = getAuthenticatedUser(authentication);
      boolean isImage = contentDTO.getType() == 0;

      if (file.isEmpty()) {
        return ResponseProperties.createResponse(400, "Error : File is empty", null);
      }
      File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
      file.transferTo(tempFile);

      String label =
          isImage ? classifyImageWithFlaskAPI(tempFile) : classifyVideoWithFFmpeg(tempFile);
      System.out.println(label);
      if (label != null && label.equals("nsfw")) {
        return ResponseProperties.createResponse(400,
            "Error: Content is classified as NSFW and cannot be uploaded", null);
      }

      String hashData = isImage
          ? contentService.calculateImageHash(tempFile)
          : contentService.calculateVideoHash(tempFile);
      if ((isImage && contentService.isExistImageData(hashData))
          || (!isImage && contentService.isExistVideoData(hashData))) {
        throw new DuplicateFileException(400, "File already exists");
      }

      //         Upload file to Cloudinary
      Map<String, Object> fileUpload = isImage ? cloudinaryService.uploadFile(tempFile)
          : cloudinaryService.uploadVideo(tempFile);

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
      content.setUserId(user.getId());
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
    } catch (ChangeSetPersister.NotFoundException e) {
      return ResponseProperties.createResponse(400, e.getMessage(), null);
    }
  }

  // Method to classify image using Flask API with File input
  private String classifyImageWithFlaskAPI(File file) {
    WebClient webClient = WebClient.builder().baseUrl("http://localhost:5000").build();
    try {
      Mono<Map> response = webClient.post()
          .uri("/classify")
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .body(BodyInserters.fromMultipartData("file",
              new FileSystemResource(file.getAbsolutePath())))
          .retrieve()
          .bodyToMono(Map.class);

      Map<String, Object> responseBody = response.block();
      return responseBody != null ? (String) responseBody.get("label") : "unknown";
    } catch (Exception e) {
      throw new RuntimeException("Error connecting to Flask API: " + e.getMessage());
    }
  }

  // Method to classify video by extracting frames using FFmpeg
  private String classifyVideoWithFFmpeg(File file) {
    // Set up output directory for extracted frames
    File outputDir = new File(System.getProperty("java.io.tmpdir"), "frames");
    outputDir.mkdirs();

    // Execute FFmpeg command to extract frames at 6 fps
    FFmpegResult result = FFmpeg.atPath()
        .addInput(UrlInput.fromUrl(file.getAbsolutePath()))
        .addArgument("-an") // Disable audio
        .addArguments("-vf", "fps=1") // Extract 6 frames per second
        .addOutput(UrlOutput.toUrl(outputDir.getAbsolutePath() + "/frame_%04d.png"))
        .execute();

    // If FFmpeg extraction is successful, classify each frame
    File[] frames = outputDir.listFiles((dir, name) -> name.endsWith(".png"));
    for (File frame : frames) {
      // Send each extracted frame to Flask API for classification
      String label = classifyImageWithFlaskAPI(frame);
      if ("nsfw".equals(label)) {
        // Delete frames after classification to free up space
        for (File f : frames) {
          f.delete();
        }
        return "nsfw"; // Reject video if any frame is NSFW
      }
    }

    // Clean up extracted frames
    for (File frame : outputDir.listFiles()) {
      frame.delete();
    }

    return "normal"; // If no frame is classified as "nsfw", accept the video
  }

  @Transactional("masterTransactionManager")
  public ResponseEntity<?> deleteContent(Authentication authentication, String id) {

    try {
      Content isExist = contentService.findById(Content.class, id)
          .orElseThrow(ChangeSetPersister.NotFoundException::new);

      Map<String, Object> fileUpload = cloudinaryService.deleteFile(isExist.getId());

      contentService.delete(isExist.getId());

      return ResponseProperties.createResponse(200, "Success", true);
    } catch (ChangeSetPersister.NotFoundException e) {
      return ResponseProperties.createResponse(400, "Error : Content does not exist", null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional("masterTransactionManager")
  public ResponseEntity<?> updateTags(Authentication authentication, String contentId,
      String tags) {
    try {
      User user = getAuthenticatedUser(authentication);

      Content isExist = contentRepository.findById(Content.class, contentId)
          .orElseThrow(ChangeSetPersister.NotFoundException::new);

      if (!isExist.getUserId().equals(user.getId())) {

        return ResponseProperties
            .createResponse(
                403,
                "Error : User does not own this content",
                null
            );
      }

      List<String> tagNames = List.of(tags.split(","));
      tagNames.forEach(tagName -> {
        try {
          gotTagsService.addTagByName(contentId, tagName);
        } catch (ChangeSetPersister.NotFoundException e) {
          throw new RuntimeException(e);
        }
      });
    } catch (ChangeSetPersister.NotFoundException e) {
      return ResponseProperties.createResponse(
          403,
          "Error : User does not own this content",
          null
      );
    }

    return ResponseProperties.createResponse(200, "Success", true);
  }

  @Transactional("slaveTransactionManager")
  public ResponseEntity<?> findAllContent(Authentication authentication)
      throws ChangeSetPersister.NotFoundException {
    List<Content> contents = contentService.findAll(Content.class)
        .orElseThrow(ChangeSetPersister.NotFoundException::new);

    return populateLikedContent(authentication, contents);
  }

  public ResponseEntity<?> findAllByType(Authentication authentication, boolean type, Integer page,
      Integer size)
      throws ChangeSetPersister.NotFoundException {
    List<Content> contents = contentService.findAll(Content.class, type, page, size)
        .orElseThrow(ChangeSetPersister.NotFoundException::new);

    return populateLikedContent(authentication, contents);
  }

  public ResponseEntity<?> findAllByTags(Authentication authentication, List<String> tags,
      Integer page, Integer size)
      throws ChangeSetPersister.NotFoundException {
    List<Content> contents = contentService.findContentsByTags(tags, page, size)
        .orElseThrow(ChangeSetPersister.NotFoundException::new);

    return populateLikedContent(authentication, contents);
  }

  public ResponseEntity<?> findContentById(Authentication authentication, String id)
      throws ChangeSetPersister.NotFoundException {
    Content content = contentService.findById(Content.class, id)
        .orElseThrow(() -> new RuntimeException("Content not found"));

    if (authentication != null) {
      User user = getAuthenticatedUser(authentication);
      Optional<Collection> collection = collectionService.getUserCollectionByName(user.getId(),
          "liked");

      collection.ifPresent(col -> {
        Set<String> likedContentIds = col.getInCols().stream()
            .map(InCols::getContentId)
            .collect(Collectors.toSet());
        content.setLiked(likedContentIds.contains(content.getId()));
      });
    }
    return ResponseProperties.createResponse(200, "Success", content);
  }

  public byte[] getContentWithSizeUrl(String id, String W, String H, String type)
      throws NotFoundException, IOException {

    Content content = contentService.findById(Content.class, id)
        .orElseThrow(() -> new RuntimeException("Content not found"));

    int width = Integer.parseInt(W);
    int height = Integer.parseInt(H);

    String url = cloudinaryService.generateTransformedUrl(content.getId(), height, width, type);

    content.setDownloads(content.getDownloads() + 1);
    contentService.update(content);

    return downloadFileAsBytes(url);
  }

  private byte[] downloadFileAsBytes(String fileUrl) throws IOException {
    URL url = new URL(fileUrl);
    try (InputStream inputStream = url.openStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

      byte[] buffer = new byte[1024];
      int bytesRead;

      // Read data into buffer and write to ByteArrayOutputStream
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        byteArrayOutputStream.write(buffer, 0, bytesRead);
      }

      // Return the byte array
      return byteArrayOutputStream.toByteArray();
    }
  }

  public ResponseEntity<?> doSearchWithFuzzy(Authentication authentication, String indexName,
      String fieldName,
      String approximates, Integer page, Integer size) {
    List<String> tagNames = List.of(approximates.split(","));
    Set<String> tags = new HashSet<>();

    try {
      tagNames.forEach(tag -> {
        try {
          SearchResponse searchResponse =
              elasticSearchService.fuzzySearch(TagESDocument.class, indexName, fieldName, tag);
          List<Hit<TagESDocument>> hitList = searchResponse.hits().hits();
          hitList.forEach(hit -> tags.add(hit.source().getName()));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });

      List<Content> result = contentService.findContentsByTags(tags.stream().toList(), page, size)
          .orElseThrow(ChangeSetPersister.NotFoundException::new);

      return populateLikedContent(authentication, result);
    } catch (Exception e) {

      return ResponseProperties.createResponse(500, "Unexpected error during fuzzy search", e);
    }
  }

  public ResponseEntity<?> doInsertTagsIntoElastic(Authentication authentication) {
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

  public ResponseEntity<?> doDeleteTagsInElastic(Authentication authentication) {
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

  private ResponseEntity<?> populateLikedContent(Authentication authentication,
      List<Content> contents)
      throws ChangeSetPersister.NotFoundException {
    if (authentication != null) {
      User user = getAuthenticatedUser(authentication);
      Optional<Collection> collection = collectionService.getUserCollectionByName(user.getId(),
          "liked");

      collection.ifPresent(col -> {
        Set<String> likedContentIds = col.getInCols().stream()
            .map(InCols::getContentId)
            .collect(Collectors.toSet());
        contents.forEach(content -> content.setLiked(likedContentIds.contains(content.getId())));
      });
    }
    return ResponseProperties.createResponse(200, "Success", contents);
  }
}