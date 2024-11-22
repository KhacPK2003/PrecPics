package com.example.prepics.services.entity;

import com.example.prepics.entity.Comment;
import com.example.prepics.interfaces.CRUDInterface;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.List;
import java.util.Optional;

public interface CommentService extends CRUDInterface<Comment, Long> {
    Optional<List<Comment>> findAllByContentId(String contentId) throws ChangeSetPersister.NotFoundException;
}
