package com.example.prepics.apis;

import com.example.prepics.annotations.Admin;
import com.example.prepics.annotations.User;
import com.example.prepics.entity.Comment;
import com.example.prepics.services.api.CommentApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public/api/comments")
public class CommentApi {

    @Autowired
    private CommentApiService commentApiService;

    @User
    @PostMapping
    public ResponseEntity<?> createComment(Authentication authentication, @RequestBody Comment comment) {
        return commentApiService.createComment(authentication, comment);
    }

    @User
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(Authentication authentication, @PathVariable Long id,
                                           @RequestBody Comment comment) {
        comment.setId(id); // Đảm bảo ID chính xác
        return commentApiService.updateComment(authentication, comment);
    }

    @User
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(Authentication authentication, @PathVariable Long id) {
        return commentApiService.deleteComment(authentication, id);
    }
    
    @Admin
    @GetMapping("/{id}")
    public ResponseEntity<?> getComment(@PathVariable Long id) {
        return commentApiService.getComment(id);
    }
}