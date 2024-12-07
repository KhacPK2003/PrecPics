package com.example.prepics.services.api;

import com.example.prepics.entity.Collection;
import com.example.prepics.entity.Content;
import com.example.prepics.entity.InCols;
import com.example.prepics.entity.User;
import com.example.prepics.models.ResponseProperties;
import com.example.prepics.services.entity.CollectionService;
import com.example.prepics.services.entity.ContentService;
import com.example.prepics.services.entity.InColsService;
import com.example.prepics.services.entity.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CollectionApiService {

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private InColsService inColsService;

    private User getAuthenticatedUser(Authentication authentication) throws ChangeSetPersister.NotFoundException {
        User userDecode = (User) authentication.getPrincipal();
        return userService.findByEmail(User.class, userDecode.getEmail())
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
    }


    public ResponseEntity<?> createCollection(Authentication authentication, String collectionName) {
        try {
            User user = getAuthenticatedUser(authentication);

            Optional<Collection> existingCollection = collectionService.getUserCollectionByName(user.getId(), collectionName);
            if (existingCollection.isPresent()) {
                return ResponseProperties.createResponse(409, "Collection already exists", null);
            }

            Collection collection = new Collection();
            collection.setName(collectionName);
            collection.setUserId(user.getId());
            Collection result = collectionService.create(collection)
                    .orElseThrow(() -> new RuntimeException("Error creating collection"));
            return ResponseProperties.createResponse(200, "Success", result);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, e.getMessage(), null);
        } catch (RuntimeException e) {
            return ResponseProperties.createResponse(400, e.getMessage(), null);
        }
    }

    public ResponseEntity<?> updateCollection(Authentication authentication, Collection model) {
        try {
            User user = getAuthenticatedUser(authentication);

            Collection existingCollection = collectionService.findById(Collection.class, model.getId())
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            if (!existingCollection.getUserId().equals(user.getId())) {
                return ResponseProperties.createResponse(403, "Unauthorized", null);
            }

            existingCollection.setName(model.getName());
            collectionService.update(existingCollection);

            return ResponseProperties.createResponse(200, "Success", existingCollection);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, e.getMessage(), null);
        } catch (RuntimeException e) {
            return ResponseProperties.createResponse(400, e.getMessage(), null);
        }
    }

    public ResponseEntity<?> deleteCollection(Authentication authentication, Long collectionId) {
        try {
            User user = getAuthenticatedUser(authentication);

//            Collection collection = collectionService.findById(Collection.class, collectionId)
//                    .orElseThrow(ChangeSetPersister.NotFoundException::new);
//
//            if (!collection.getUserId().equals(user.getId())) {
//                return ResponseProperties.createResponse(403, "Unauthorized", null);
//            }
//
//            if (collection.getName().equalsIgnoreCase("liked")) {
//                return ResponseProperties.createResponse(403, "Unauthorized", null);
//            }

            collectionService.delete(collectionId);
            return ResponseProperties.createResponse(200, "Success", null);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, e.getMessage(), null);
        } catch (RuntimeException e) {
            return ResponseProperties.createResponse(400, e.getMessage(), null);
        }
    }

    public ResponseEntity<?> getCollection(Long collectionId) {
        try {
            Collection collection = collectionService.findById(Collection.class, collectionId)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);
            return ResponseProperties.createResponse(200, "Success", collection);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, e.getMessage(), null);
        }
    }

    @Transactional("masterTransactionManager")
    public ResponseEntity<?> addContentToCollection(Authentication authentication, Long collectionId, String contentId) {
        try {
            User user = getAuthenticatedUser(authentication);

            Collection collection = collectionService.findById(Collection.class, collectionId)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            if (!collection.getUserId().equals(user.getId())) {
                return ResponseProperties.createResponse(403, "Unauthorized", null);
            }

            Content content = contentService.findById(Content.class, contentId)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            InCols inCols = new InCols();
            inCols.setCollectionId(collection.getId());
            inCols.setContentId(content.getId());
            InCols result = inColsService.create(inCols)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);
            if (collection.getName().equalsIgnoreCase("liked")){
                content.setLikeds(content.getLikeds() + 1);
                contentService.update(content);
            }

            return ResponseProperties.createResponse(200, "Success", result);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, e.getMessage(), null);
        } catch (RuntimeException e) {
            return ResponseProperties.createResponse(400, e.getMessage(), null);
        }
    }

    @Transactional("masterTransactionManager")
    public ResponseEntity<?> removeContentToCollection(Authentication authentication, Long collectionId, String contentId) {
        try {
            User user = getAuthenticatedUser(authentication);

            Collection collection = collectionService.findById(Collection.class, collectionId)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            if (!collection.getUserId().equals(user.getId())) {
                return ResponseProperties.createResponse(403, "Unauthorized", null);
            }

            Content content = contentService.findById(Content.class, contentId)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            InCols inCols = inColsService.findByContentIdAndCollectionId(contentId, collectionId)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            inColsService.delete(inCols.getId());
            if (collection.getName().equalsIgnoreCase("liked")){
                content.setLikeds(content.getLikeds() + 1);
                contentService.update(content);
            }
            return ResponseProperties.createResponse(200, "Success", null);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, e.getMessage(), null);
        } catch (RuntimeException e) {
            return ResponseProperties.createResponse(400, e.getMessage(), null);
        }
    }

}
