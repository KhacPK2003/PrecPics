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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Override
    public Optional<Content> findById(Class<Content> clazz, String id)
            throws ChangeSetPersister.NotFoundException {
        Optional<Content> content = contentRepository.findById(clazz, id);
        if (content.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        String tagsByContentId = contentRepository.findTagsByContentId(content.get().getId()).orElse(null);
        content.get().setTags(tagsByContentId);
        return content;
    }

    @Override
    public Optional<List<Content>> findAll(Class<Content> clazz) throws ChangeSetPersister.NotFoundException {
        List<Content> result = contentRepository.findAll(clazz).orElseThrow();
        result.forEach(entity -> {
            String tagsByContentId = contentRepository.findTagsByContentId(entity.getId()).orElse(null);
            entity.setTags(tagsByContentId);
        });
        return Optional.of(result);
    }

    @Override
    public Optional<List<Content>> findAll(Class<Content> clazz, boolean type, Integer page, Integer size)
            throws ChangeSetPersister.NotFoundException {
        List<Content> result = contentRepository.findAllByType(type, page, size).orElseThrow();
        result.forEach(entity -> {
            String tagsByContentId = contentRepository.findTagsByContentId(entity.getId()).orElse(null);
            entity.setTags(tagsByContentId);
        });
        return Optional.of(result);
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
    public Optional<List<Content>> findContentsByTags(List<String> tags, Integer page, Integer size) {
        List<Content> result = contentRepository.findContentsByTags(tags, page, size).orElseThrow();
        result.forEach(entity -> {
            String tagsByContentId = contentRepository.findTagsByContentId(entity.getId()).orElse(null);
            entity.setTags(tagsByContentId);
        });
        return Optional.of(result);
    }

    @Override
    public Optional<File> changeResolutionForImage(String contentUrl, int width, int height) {
        String tempDir = System.getProperty("java.io.tmpdir");
        String pathToDst = tempDir + "/"+ UUID.randomUUID() +".jpg";

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
    public Optional<File> changeResolutionForVideo(String contentUrl, int width, int height) {
        String tempDir = System.getProperty("java.io.tmpdir");
        String pathToDst = tempDir + "/"+ UUID.randomUUID() +".mp4";

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
    public String calculateImageHash(File imagePath) throws Exception {
        return PerceptualHash.calculateImagePHash(imagePath);
    }

    @Override
    public String calculateVideoHash(File imagePath) throws Exception {
        return PerceptualHash.processVideo(imagePath);
    }

    @Override
    public boolean isExistImageData(String dataByte) throws ChangeSetPersister.NotFoundException {
        List<Content> contents = contentRepository.findAllByType(true)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        // Kiểm tra nếu tồn tại Content nào có Hamming Distance < 10
        return contents.stream().map(Content::getDataByte).anyMatch(e -> hammingDistance(dataByte, e) < 10);
    }

    public int hammingDistance(String hash1, String hash2) {
        // Kiểm tra null
        if (hash1 == null || hash2 == null) {
            throw new IllegalArgumentException("Hashes must not be null");
        }

        // Chuyển đổi từ hexadecimal sang BigInteger
        BigInteger value1 = new BigInteger(hash1, 16);
        BigInteger value2 = new BigInteger(hash2, 16);

        // Tính XOR giữa hai giá trị
        BigInteger xor = value1.xor(value2);

        // Đếm số bit 1 trong XOR
        int distance = xor.bitCount();

        return distance;
    }

    public int compareVideos(String videoHash1, String videoHash2) {
        // So sánh các video bằng cách tính khoảng cách Hamming giữa hai hash
        int distance = hammingDistance(videoHash1, videoHash2);

        // Tính tỷ lệ phần trăm tương đồng giữa hai video
        int maxDistance = videoHash1.length(); // Max distance = chiều dài của hash
        double similarityPercentage = (1 - (double) distance / maxDistance) * 100;

        return (int) similarityPercentage;
    }


    @Override
    public boolean isExistVideoData(String dataByte) throws ChangeSetPersister.NotFoundException {
        List<Content> contents = contentRepository.findAllByType(false)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        // Tìm kiếm video trùng lặp với dữ liệu input
        return contents.stream()
                .map(Content::getDataByte)
                .anyMatch(existingData -> compareVideos(dataByte, existingData) > 70);
    }

    @Override
    public boolean isDuplicateData(boolean isImage, String hashData) throws ChangeSetPersister.NotFoundException {
        return isImage ? isExistImageData(hashData) :
                    isExistVideoData(hashData);
    }
}
