    package com.example.prepics.services.api;

    import co.elastic.clients.elasticsearch.core.SearchResponse;
    import co.elastic.clients.elasticsearch.core.search.Hit;
    import com.example.prepics.dto.ContentDTO;
    import com.example.prepics.entity.*;
    import com.example.prepics.entity.Collection;
    import com.example.prepics.exception.DuplicateFileException;
    import com.example.prepics.models.ResponseProperties;
    import com.example.prepics.models.TagESDocument;
    import com.example.prepics.repositories.ContentRepository;
    import com.example.prepics.services.cloudinary.CloudinaryService;
    import com.example.prepics.services.elasticsearch.ElasticSearchService;
    import com.example.prepics.services.elasticsearch.TagESDocumentService;
    import com.example.prepics.services.entity.*;
    import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
    import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
    import com.github.kokorin.jaffree.ffmpeg.UrlInput;
    import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
    import org.modelmapper.ModelMapper;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.crossstore.ChangeSetPersister;
    import org.springframework.http.*;
    import org.springframework.mock.web.MockMultipartFile;
    import org.springframework.security.core.Authentication;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.util.LinkedMultiValueMap;
    import org.springframework.util.MultiValueMap;
    import org.springframework.web.client.RestTemplate;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.File;
    import java.io.FileInputStream;
    import java.io.IOException;

    import java.math.BigInteger;
    import java.nio.file.Files;
    import java.util.*;
    import java.util.stream.Collectors;

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
        private CommentService commentService;

        @Autowired
        private TagESDocumentService tagESDocumentService;
        @Autowired
        private CollectionService collectionService;

        private User getAuthenticatedUser(Authentication authentication) throws ChangeSetPersister.NotFoundException {
            User userDecode = (User) authentication.getPrincipal();
            return userService.findByEmail(User.class, userDecode.getEmail())
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);
        }

        @Transactional("masterTransactionManager")
        public ResponseEntity<?> uploadContent(Authentication authentication, MultipartFile file, ContentDTO contentDTO)
                throws Exception {
            try {
                User user = getAuthenticatedUser(authentication);
                boolean isImage = contentDTO.getType() == 0;

                if (file.isEmpty()) {
                    return ResponseProperties.createResponse(400, "Error : File is empty", null);
                }

                String label = isImage ? classifyImageWithFlaskAPI(file) : classifyVideoWithFFmpeg(file);

                if (label == null) {
                    return ResponseProperties.createResponse(400, "Error: Unable to classify image", null);
                }

                if (label.equals("nsfw")) {
                    return ResponseProperties.createResponse(400,
                                    "Error: Content is classified as NSFW and cannot be uploaded", null);
                }

                // Lưu tệp tạm thời
                byte[] fileBytes = file.getBytes();

                String hashData = isImage
                        ? contentService.calculateImageHash(file)
                        : contentService.calculateVideoHash(file);
                if ((isImage && contentService.isExistImageData(hashData))
                    || (!isImage && contentService.isExistVideoData(hashData))) {
                    throw new DuplicateFileException(400, "File already exists");
                }

                //         Upload file to Cloudinary
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

                Content result = contentService.findById(Content.class, content.getId())
                        .orElseThrow(ChangeSetPersister.NotFoundException::new);

                return ResponseProperties.createResponse(200, "Success", result);
            } catch (ChangeSetPersister.NotFoundException e) {
                return ResponseProperties.createResponse(400, e.getMessage(), null);
            }
        }

        private String classifyImageWithFlaskAPI(MultipartFile file) {
            try {
                RestTemplate restTemplate = new RestTemplate();

                // Tạo HttpHeaders và thiết lập Content-Type là multipart/form-data
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                // Tạo fileMap để thêm Content-Disposition vào request
                MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
                ContentDisposition contentDisposition = ContentDisposition
                        .builder("form-data")
                        .name("file")
                        .filename(file.getOriginalFilename())
                        .build();
                fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

                // Chuyển đổi MultipartFile thành byte[]
                byte[] fileBytes = file.getBytes();

                // Tạo HttpEntity chứa byte[] và metadata trong fileMap
                HttpEntity<byte[]> fileEntity = new HttpEntity<>(fileBytes, fileMap);

                // Tạo body request chứa fileEntity
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", fileEntity);

                // Tạo HttpEntity chứa body và headers
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                // URL của Flask API
                String flaskApiUrl = "http://localhost:5000/classify";

                // Gửi yêu cầu POST đến Flask API
                ResponseEntity<Map> response = restTemplate.exchange(
                        flaskApiUrl,
                        HttpMethod.POST,
                        requestEntity,
                        Map.class
                );

                // Xử lý phản hồi
                if (response.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> responseBody = response.getBody();
                    return responseBody != null ? (String) responseBody.get("label") : null;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // Method to classify image using Flask API with File input
        private String classifyImageWithFlaskAPI(File frame) {
            try {
                RestTemplate restTemplate = new RestTemplate();

                // Tạo HttpHeaders và thiết lập Content-Type là multipart/form-data
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                // Tạo fileMap để thêm Content-Disposition vào request
                MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
                ContentDisposition contentDisposition = ContentDisposition
                        .builder("form-data")
                        .name("file")
                        .filename(frame.getName())
                        .build();
                fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

                // Chuyển đổi File thành byte[]
                byte[] frameBytes = Files.readAllBytes(frame.toPath());

                // Tạo HttpEntity chứa byte[] và metadata trong fileMap
                HttpEntity<byte[]> fileEntity = new HttpEntity<>(frameBytes, fileMap);

                // Tạo body request chứa fileEntity
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", fileEntity);

                // Tạo HttpEntity chứa body và headers
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                // URL của Flask API
                String flaskApiUrl = "http://localhost:5000/classify";

                // Gửi yêu cầu POST đến Flask API
                ResponseEntity<Map> response = restTemplate.exchange(
                        flaskApiUrl,
                        HttpMethod.POST,
                        requestEntity,
                        Map.class
                );

                // Xử lý phản hồi
                if (response.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> responseBody = response.getBody();
                    return responseBody != null ? (String) responseBody.get("label") : null;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // Method to classify video by extracting frames using FFmpeg
        private String classifyVideoWithFFmpeg(MultipartFile file) {
            try {
                // Save video file temporarily
                File tempFile = File.createTempFile("video_", ".mp4");
                file.transferTo(tempFile);

                // Set up output directory for extracted frames
                File outputDir = new File(System.getProperty("java.io.tmpdir"), "frames");
                outputDir.mkdirs();

                // Execute FFmpeg command to extract frames at 6 fps
                FFmpegResult result = FFmpeg.atPath()
                        .addInput(UrlInput.fromUrl(tempFile.getAbsolutePath()))
                        .addArgument("-an") // Disable audio
                        .addArguments("-vf", "fps=6") // Extract 6 frames per second
                        .addOutput(UrlOutput.toUrl(outputDir.getAbsolutePath() + "/frame_%04d.png"))
                        .execute();

                // If FFmpeg extraction is successful, classify each frame
                if (result.hashCode() == 0) {
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
                }

                // Clean up extracted frames
                for (File frame : outputDir.listFiles()) {
                    frame.delete();
                }

                return "normal"; // If no frame is classified as "nsfw", accept the video
            } catch (IOException e) {
                e.printStackTrace();
                return null; // Error during video processing
            }
        }

        @Transactional("masterTransactionManager")
        public ResponseEntity<?> deleteContent(Authentication authentication, String id)
                throws IOException, ChangeSetPersister.NotFoundException {

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
        public ResponseEntity<?> updateTags(Authentication authentication, String contentId, String tags)
                throws ChangeSetPersister.NotFoundException {
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
        public ResponseEntity<?> findAllContent(Authentication authentication) throws ChangeSetPersister.NotFoundException {
            List<Content> contents = contentService.findAll(Content.class)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            return populateLikedContent(authentication, contents);
        }

        public ResponseEntity<?> findAllByType(Authentication authentication, boolean type, Integer page, Integer size)
                throws ChangeSetPersister.NotFoundException {
            List<Content> contents = contentService.findAll(Content.class, type, page, size)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            return populateLikedContent(authentication, contents);
        }

        public ResponseEntity<?> findAllByTags(Authentication authentication, List<String> tags, Integer page, Integer size)
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
                Optional<Collection> collection = collectionService.getUserCollectionByName(user.getId(), "liked");

                collection.ifPresent(col -> {
                    Set<String> likedContentIds = col.getInCols().stream()
                            .map(InCols::getContentId)
                            .collect(Collectors.toSet());
                    content.setLiked(likedContentIds.contains(content.getId()));
                });
            }
            return ResponseProperties.createResponse(200, "Success", content);
        }

        private byte[] getContentWithSize(Authentication authentication, Map<String, Object> model, boolean isImage)
                throws IOException, ChangeSetPersister.NotFoundException {

            Content content = modelMapper.map(model.get("content"), Content.class);

            int width = Integer.parseInt(model.get("width").toString());
            int height = Integer.parseInt(model.get("height").toString());

            Optional<File> result = isImage
                    ? contentService.changeResolutionForImage(content.getDataUrl(), width, height)
                    : contentService.changeResolutionForVideo(content.getDataUrl(), width, height);

            content.setDownloads(content.getDownloads() + 1);
            contentService.update(content);

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

        @Transactional("slaveTransactionManager")
        public byte[] getImageWithSize(Authentication authentication, Map<String, Object> model)
                throws IOException, ChangeSetPersister.NotFoundException {

            return getContentWithSize(authentication, model, true);
        }

        @Transactional("slaveTransactionManager")
        public byte[] getVideoWithSize(Authentication authentication, Map<String, Object> model)
                throws IOException, ChangeSetPersister.NotFoundException {

            return getContentWithSize(authentication, model, false);
        }

        public ResponseEntity<?> doSearchWithFuzzy(Authentication authentication, String indexName, String fieldName,
                                                   String approximates, Integer page, Integer size) {
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

        private ResponseEntity<?> populateLikedContent(Authentication authentication, List<Content> contents)
                throws ChangeSetPersister.NotFoundException {
            if (authentication != null) {
                User user = getAuthenticatedUser(authentication);
                Optional<Collection> collection = collectionService.getUserCollectionByName(user.getId(), "liked");

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
