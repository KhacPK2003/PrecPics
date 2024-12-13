package com.example.prepics.services.api;

import com.example.prepics.entity.Comment;
import com.example.prepics.entity.User;
import com.example.prepics.models.ResponseProperties;
import com.example.prepics.services.entity.CommentService;
import com.example.prepics.services.entity.UserService;
import java.math.BigInteger;
import java.util.Date;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CommentApiService {

  @Autowired
  private CommentService commentService;

  @Autowired
  private UserService userService;

  @Autowired
  private ModelMapper modelMapper;

  private User getAuthenticatedUser(Authentication authentication)
      throws ChangeSetPersister.NotFoundException {
    User userDecode = (User) authentication.getPrincipal();
    return userService.findByEmail(User.class, userDecode.getEmail())
        .orElseThrow(ChangeSetPersister.NotFoundException::new);
  }

  public ResponseEntity<?> createComment(Authentication authentication, Comment commentModel) {
    try {
      User user = getAuthenticatedUser(authentication);
      commentModel.setUserId(user.getId());
      commentModel.setDateCreate(BigInteger.valueOf(new Date().getTime()));
      Comment savedComment = commentService.create(commentModel)
          .orElseThrow(() -> new RuntimeException("Error creating comment"));
      return ResponseProperties.createResponse(200, "Success", savedComment);
    } catch (ChangeSetPersister.NotFoundException e) {
      return ResponseProperties.createResponse(404, e.getMessage(), null);
    } catch (RuntimeException e) {
      return ResponseProperties.createResponse(400, e.getMessage(), null);
    }
  }

  public ResponseEntity<?> updateComment(Authentication authentication, Comment commentModel) {
    try {
      User user = getAuthenticatedUser(authentication);

      Comment existingComment = commentService.findById(Comment.class, commentModel.getId())
          .orElseThrow(ChangeSetPersister.NotFoundException::new);

      if (!existingComment.getUserId().equals(user.getId())) {
        return ResponseProperties.createResponse(403, "Unauthorized", null);
      }

      existingComment.setContentValue(commentModel.getContentValue());
      commentService.update(existingComment);

      return ResponseProperties.createResponse(200, "Success", existingComment);
    } catch (ChangeSetPersister.NotFoundException e) {
      return ResponseProperties.createResponse(404, e.getMessage(), null);
    } catch (RuntimeException e) {
      return ResponseProperties.createResponse(400, e.getMessage(), null);
    }
  }

  public ResponseEntity<?> deleteComment(Authentication authentication, Long commentId) {
    try {
      User user = getAuthenticatedUser(authentication);

      Comment comment = commentService.findById(Comment.class, commentId)
          .orElseThrow(ChangeSetPersister.NotFoundException::new);

      if (!comment.getUserId().equals(user.getId())) {
        return ResponseProperties.createResponse(403, "Unauthorized", null);
      }

      commentService.delete(commentId);
      return ResponseProperties.createResponse(200, "Success", null);
    } catch (ChangeSetPersister.NotFoundException e) {
      return ResponseProperties.createResponse(404, e.getMessage(), null);
    } catch (RuntimeException e) {
      return ResponseProperties.createResponse(400, e.getMessage(), null);
    }
  }

  public ResponseEntity<?> getComment(Long commentId) {
    try {
      Comment comment = commentService.findById(Comment.class, commentId)
          .orElseThrow(ChangeSetPersister.NotFoundException::new);
      return ResponseProperties.createResponse(200, "Success", comment);
    } catch (ChangeSetPersister.NotFoundException e) {
      return ResponseProperties.createResponse(404, e.getMessage(), null);
    }
  }
}