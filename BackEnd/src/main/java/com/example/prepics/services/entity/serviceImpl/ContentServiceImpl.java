package com.example.prepics.services.entity.serviceImpl;

import com.example.prepics.config.PerceptualHash;
import com.example.prepics.entity.Content;
import com.example.prepics.repositories.ContentRepository;
import com.example.prepics.services.entity.ContentService;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Optional;

@Service
public class ContentServiceImpl implements ContentService {

    private static final Logger log = LoggerFactory.getLogger(ContentServiceImpl.class);

    @Value("${video.path}")
    private String VIDEO_DIR;

    @Autowired
    private ContentRepository contentRepository;

    @Override
    public Optional<Content> findById(Class<Content> clazz, String id)
            throws ChangeSetPersister.NotFoundException {
        Optional<Content> content = contentRepository.findById(clazz, id);
        if (content.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        return content;
    }

    @Override
    public Optional<List<Content>> findAll(Class<Content> clazz) throws ChangeSetPersister.NotFoundException {
        return contentRepository.findAll(clazz);
    }

    @Override
    public Optional<List<Content>> findAll(Class<Content> clazz, boolean type, Integer page, Integer size)
            throws ChangeSetPersister.NotFoundException {
        return contentRepository.findAllByType(type, page, size);
    }

    @Override
    public Optional<Content> delete(String id) throws ChangeSetPersister.NotFoundException {
        Optional<Content> content = this.findById(Content.class, id);
        return contentRepository.delete(id);
    }

    @Override
    public Optional<Content> update(Content entity) throws ChangeSetPersister.NotFoundException {
        return contentRepository.update(entity);
    }

    @Override
    public Optional<Content> create(Content entity)
            throws EntityExistsException, ChangeSetPersister.NotFoundException {
        return contentRepository.create(entity);
    }

    @Override
    public Optional<List<Content>> findContentsByTags(String tags, Integer page, Integer size) {
        return contentRepository.findContentsByTags(tags, page, size);
    }


    @Override
    public Optional<File> changeResolutionForImage(String contentUrl, int width, int height) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        String pathToDst = tempDir + "/output.jpg";

        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(contentUrl))
                .setFilter(StreamType.VIDEO, "scale=" + width + ":" + height)
                .setOverwriteOutput(true)
                .addArguments("-q:v", "2")
                .addOutput(UrlOutput.toUrl(pathToDst))
                .execute();

        File result = new File(pathToDst);
        result.deleteOnExit();
        return Optional.of(result);
    }

    @Override
    public Optional<File> changeResolutionForVideo(String contentUrl, int width, int height) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        String pathToDst = tempDir + "/output.mp4";

        FFmpeg.atPath()
                .addInput(
                        UrlInput.fromUrl(contentUrl)
                )
                .setFilter(StreamType.VIDEO, "scale=" + width + ":" + height)
                .setOverwriteOutput(true)
                .addArguments("-movflags", "faststart")
                .addOutput(
                        UrlOutput.toUrl(pathToDst)
                )
                .execute();

        File result = new File(pathToDst);
        result.deleteOnExit();
        return Optional.of(result);
    }

    @Override
    public String calculateHash(File imagePath) throws Exception {
        return PerceptualHash.calculatePHash(imagePath);
    }

    @Override
    public boolean isExistContentData(String dataByte) throws ChangeSetPersister.NotFoundException {
        List<Content> contents = contentRepository.findAll(Content.class)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        // Kiểm tra nếu tồn tại Content nào có Hamming Distance < 10
        return contents.stream().anyMatch(e -> hammingDistance(e.getDataByte(), dataByte) < 10);
    }

    public int hammingDistance(String hash1, String hash2) {
        if (hash1.length() != hash2.length()) {
            throw new IllegalArgumentException("Hashes must have the same length");
        }
        int distance = 0;
        for (int i = 0; i < hash1.length(); i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }
}
