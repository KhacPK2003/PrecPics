package com.example.prepics.services.api;

import com.example.prepics.entity.Tag;
import com.example.prepics.entity.User;
import com.example.prepics.models.ResponseProperties;
import com.example.prepics.services.entity.TagService;
import com.example.prepics.services.entity.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagApiService {

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    private User getAuthenticatedUser(Authentication authentication) throws ChangeSetPersister.NotFoundException {
        User userDecode = (User) authentication.getPrincipal();
        return userService.findByEmail(User.class, userDecode.getEmail())
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
    }

    public ResponseEntity<?> createTag(Authentication authentication, String tagName) {
        try {
            getAuthenticatedUser(authentication);

            Optional<Tag> existingTag = tagService.findByName(Tag.class, tagName);
            if (existingTag.isPresent()) {
                return ResponseProperties.createResponse(409, "Tag already exists", null);
            }

            Tag tag = new Tag();
            tag.setName(tagName);
            Tag result = tagService.create(tag)
                    .orElseThrow(() -> new RuntimeException("Error creating tag"));
            return ResponseProperties.createResponse(200, "Success", result);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, e.getMessage(), null);
        } catch (RuntimeException e) {
            return ResponseProperties.createResponse(400, e.getMessage(), null);
        }
    }

    public ResponseEntity<?> updateTag(Authentication authentication, Tag model) {
        try {
            getAuthenticatedUser(authentication);

            Tag existingTag = tagService.findById(Tag.class, model.getId())
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            existingTag.setName(model.getName());
            tagService.update(existingTag);

            return ResponseProperties.createResponse(200, "Success", existingTag);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, e.getMessage(), null);
        } catch (RuntimeException e) {
            return ResponseProperties.createResponse(400, e.getMessage(), null);
        }
    }

    public ResponseEntity<?> deleteTag(Authentication authentication, Long tagId) {
        try {
            getAuthenticatedUser(authentication);

            Tag tag = tagService.findById(Tag.class, tagId)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            tagService.delete(tagId);
            return ResponseProperties.createResponse(200, "Success", null);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, e.getMessage(), null);
        } catch (RuntimeException e) {
            return ResponseProperties.createResponse(400, e.getMessage(), null);
        }
    }

    public ResponseEntity<?> getTag(Long tagId) {
        try {
            Tag tag = tagService.findById(Tag.class, tagId)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);
            return ResponseProperties.createResponse(200, "Success", tag);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, e.getMessage(), null);
        }
    }
}