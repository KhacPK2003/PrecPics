package com.example.prepics.apis;

import com.example.prepics.services.api.ContentApiService;
import com.example.prepics.services.entity.ContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/public/api/contents")
public class ContentApi {
    @Autowired
    ContentService contentService;

    @Autowired
    ContentApiService contentApiService;

    @PostMapping("/content/upload")
    public ResponseEntity<?> uploadContent(Authentication authentication, @RequestParam("file") MultipartFile file
            , @RequestBody Map<String, Object> model) throws ChangeSetPersister.NotFoundException, IOException {
        return ResponseEntity.ok(contentApiService.uploadContent(authentication, file, model));
    }

    @DeleteMapping("/content/{id}")
    public ResponseEntity<?> deleteContent(Authentication authentication, @PathVariable("id") String id)
            throws ChangeSetPersister.NotFoundException, IOException {
        return ResponseEntity.ok(contentApiService.deleteContent(authentication, id));
    }

    @PostMapping("/{id}/{tags}")
    public ResponseEntity<?> updateTags(Authentication authentication, @PathVariable("id") String contenId
            , @PathVariable("tags") String tags) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.updateTags(authentication, contenId, tags));
    }

    @GetMapping
    public ResponseEntity<?> findAllContents() throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.findAllContent());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> findAllByType(@PathVariable("type") String type)
            throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.findAllByType((Integer.parseInt(type)==0)));
    }

    @GetMapping("/tags/{tags}")
    public ResponseEntity<?> findAllByTag(@PathVariable("tags") String tags)
            throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.findAllByTags(tags));
    }

    @GetMapping("/content/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.findContentById(id));
    }

    @GetMapping("/content/0/download") // 0 is image
    public ResponseEntity<?> getImageWithSize(Authentication authentication, @RequestBody Map<String, Object> model)
            throws IOException, ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.getImageWithSize(authentication, model));
    }

    @GetMapping("/content/1/download") //1 is video
    public ResponseEntity<?> getVideoWithSize(Authentication authentication, @RequestBody Map<String, Object> model)
            throws IOException, ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.getVideoWithSize(authentication,model));
    }

    //indexName is name of class (contents); fieldName is name of property (tags)
    @GetMapping("/search/{indexName}/{fieldName}/{approximateTagNames}")
    public ResponseEntity<?> doSearchWithFuzzy(@PathVariable("indexName") String indexName,
                                               @PathVariable("fieldName") String fieldName,
                                               @PathVariable("approximateTagNames") String approximateTagNames)
            throws IOException {
        return ResponseEntity.ok(contentApiService.doSearchWithFuzzy(indexName, fieldName, approximateTagNames));
    }

    @PostMapping("/elasticsearch")
    public ResponseEntity<?> doInsertContentsIntoElastic(Authentication authentication) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.doInsertContentsIntoElastic(authentication));
    }

    @DeleteMapping("/elasticsearch")
    public ResponseEntity<?> doDeleteContentsInElastic(Authentication authentication) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.doDeleteContentsInElastic(authentication));
    }
}
