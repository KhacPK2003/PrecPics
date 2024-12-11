package com.example.prepics.services.entity;

import com.example.prepics.entity.InCols;
import com.example.prepics.interfaces.CRUDInterface;
import java.util.Optional;
import org.springframework.data.crossstore.ChangeSetPersister;

public interface InColsService extends CRUDInterface<InCols, Long> {

  Optional<InCols> findByContentIdAndCollectionId(String contentId, Long collectionId)
      throws ChangeSetPersister.NotFoundException;
}