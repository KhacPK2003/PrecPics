package com.example.prepics.services.elasticsearch.implementations;

import com.example.prepics.entity.Content;
import com.example.prepics.models.ContentESDocument;
import com.example.prepics.repositories.ContentRepository;
import com.example.prepics.repositories.elasticsearch.ContentElasticSearchRepository;
import com.example.prepics.services.elasticsearch.ContentElasticSearchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContentESDocumentServiceImpl implements ContentElasticSearchService {

    @Autowired
    private ContentElasticSearchRepository repository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<ContentESDocument> insertContent(Content entity) {
        ContentESDocument result = modelMapper.map(entity, ContentESDocument.class);
        String tags =  contentRepository.findTagsByContentId(entity.getId()).get();
        entity.setTags(tags);
        return Optional.of(repository.save(result));
    }

    @Override
    public Optional<ContentESDocument> insertContent(ContentESDocument entity) {
        return Optional.of(repository.save(entity));
    }

    @Override
    public Optional<Iterable<ContentESDocument>> getContent() {
        return Optional.of(repository.findAll());
    }

    @Override
    public Optional<Iterable<ContentESDocument>> getContent(boolean type) {
        List<ContentESDocument> result = new ArrayList<>();
        repository.findAll().forEach(ContentESDocument -> {
            if(ContentESDocument.isType() == type) {
                result.add(ContentESDocument);
            }
        });
        return Optional.of(result);
    }

    @Override
    public Optional<Iterable<ContentESDocument>> saveAll(List<Content> Contents) {
        List<ContentESDocument> results = new ArrayList<>();
        Contents.forEach(e->{
            String tags =  contentRepository.findTagsByContentId(e.getId()).get();
            e.setTags(tags);
            results.add(modelMapper.map(e, ContentESDocument.class));
        });
        return Optional.of(repository.saveAll(results));
    }

    @Override
    public Optional<Iterable<ContentESDocument>> deleteAll() {
        repository.deleteAll();
        return Optional.empty();
    }

    @Override
    public Optional<Iterable<ContentESDocument>> deleteAll(List<Content> Contents) {
        List<ContentESDocument> results = new ArrayList<>();
        Contents.forEach(e->{
            results.add(modelMapper.map(e, ContentESDocument.class));
        });
        repository.deleteAll(results);
        return Optional.of(results);
    }

    @Override
    public Optional<ContentESDocument> delete(ContentESDocument entity) {
        repository.delete(entity);
        return Optional.of(entity);
    }

    @Override
    public Optional<ContentESDocument> delete(Content entity) {
        ContentESDocument result = modelMapper.map(entity, ContentESDocument.class);
        repository.delete(result);
        return Optional.of(result);
    }
}
