package com.example.prepics.services.elasticsearch.implementations;

import com.example.prepics.entity.Content;
import com.example.prepics.models.ContentElasticSearch;
import com.example.prepics.repositories.elasticsearch.ContentElasticSearchRepository;
import com.example.prepics.services.elasticsearch.ContentElasticSearchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContentElasticSearchServiceImpl implements ContentElasticSearchService {

    @Autowired
    private ContentElasticSearchRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ContentElasticSearch insertContent(Content entity) {
        ContentElasticSearch result = modelMapper.map(entity, ContentElasticSearch.class);
        return repository.save(result);
    }

    @Override
    public ContentElasticSearch insertContent(ContentElasticSearch entity) {
        return repository.save(entity);
    }

    @Override
    public Iterable<ContentElasticSearch> getContent() {
        return repository.findAll();
    }

    @Override
    public Iterable<ContentElasticSearch> getContent(boolean type) {
        List<ContentElasticSearch> result = new ArrayList<>();
        repository.findAll().forEach(contentElasticSearch -> {
            if(contentElasticSearch.isType() == type) {
                result.add(contentElasticSearch);
            }
        });
        return result;
    }

    @Override
    public Iterable<ContentElasticSearch> saveAll(List<Content> Contents) {
        List<ContentElasticSearch> results = new ArrayList<>();
        Contents.forEach(e->{
            results.add(modelMapper.map(e, ContentElasticSearch.class));
        });
        return repository.saveAll(results);
    }

    @Override
    public Iterable<ContentElasticSearch> deleteAll() {
        repository.deleteAll();
        return null;
    }

    @Override
    public Iterable<ContentElasticSearch> deleteAll(List<Content> Contents) {
        List<ContentElasticSearch> results = new ArrayList<>();
        Contents.forEach(e->{
            results.add(modelMapper.map(e, ContentElasticSearch.class));
        });
        repository.deleteAll(results);
        return results;
    }

    @Override
    public ContentElasticSearch delete(ContentElasticSearch entity) {
        repository.delete(entity);
        return entity;
    }

    @Override
    public ContentElasticSearch delete(Content entity) {
        ContentElasticSearch result = modelMapper.map(entity, ContentElasticSearch.class);
        repository.delete(result);
        return result;
    }
}
