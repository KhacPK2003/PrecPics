package com.example.prepics.services.entity;

import com.example.prepics.entity.GotTags;
import com.example.prepics.interfaces.CRUDInterface;
import org.springframework.data.crossstore.ChangeSetPersister;


public interface GotTagsService extends CRUDInterface<GotTags, Long> {

    boolean addTagByName(String contentId, String tagName) throws ChangeSetPersister.NotFoundException;
}
