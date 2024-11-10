package com.example.prepics.services.elasticsearch.implementations;

import com.example.prepics.entity.Content;
import com.example.prepics.models.ContentElasticSearch;
import com.example.prepics.repositories.ContentRepository;
import com.example.prepics.repositories.elasticsearch.ContentElasticSearchRepository;
import com.example.prepics.services.elasticsearch.ContentElasticSearchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContentElasticSearchServiceImpl implements ContentElasticSearchService {

    @Autowired
    private ContentElasticSearchRepository repository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<ContentElasticSearch> insertContent(Content entity) {
        ContentElasticSearch result = modelMapper.map(entity, ContentElasticSearch.class);
        String tags =  contentRepository.findTagsByContentId(entity.getId()).get();
        entity.setTags(tags);
        return Optional.of(repository.save(result));
    }

    @Override
    public Optional<ContentElasticSearch> insertContent(ContentElasticSearch entity) {
        return Optional.of(repository.save(entity));
    }

    @Override
    public Optional<Iterable<ContentElasticSearch>> getContent() {
        return Optional.of(repository.findAll());
    }

    @Override
    public Optional<Iterable<ContentElasticSearch>> getContent(boolean type) {
        List<ContentElasticSearch> result = new ArrayList<>();
        repository.findAll().forEach(contentElasticSearch -> {
            if(contentElasticSearch.isType() == type) {
                result.add(contentElasticSearch);
            }
        });
        return Optional.of(result);
    }

    @Override
    public Optional<Iterable<ContentElasticSearch>> saveAll(List<Content> Contents) {
        List<ContentElasticSearch> results = new ArrayList<>();
        Contents.forEach(e->{
            String tags =  contentRepository.findTagsByContentId(e.getId()).get();
            e.setTags(tags);
            results.add(modelMapper.map(e, ContentElasticSearch.class));
        });
        return Optional.of(repository.saveAll(results));
    }

    @Override
    public Optional<Iterable<ContentElasticSearch>> deleteAll() {
        repository.deleteAll();
        return Optional.empty();
    }

    @Override
    public Optional<Iterable<ContentElasticSearch>> deleteAll(List<Content> Contents) {
        List<ContentElasticSearch> results = new ArrayList<>();
        Contents.forEach(e->{
            results.add(modelMapper.map(e, ContentElasticSearch.class));
        });
        repository.deleteAll(results);
        return Optional.of(results);
    }

    @Override
    public Optional<ContentElasticSearch> delete(ContentElasticSearch entity) {
        repository.delete(entity);
        return Optional.of(entity);
    }

    @Override
    public Optional<ContentElasticSearch> delete(Content entity) {
        ContentElasticSearch result = modelMapper.map(entity, ContentElasticSearch.class);
        repository.delete(result);
        return Optional.of(result);
    }
}
