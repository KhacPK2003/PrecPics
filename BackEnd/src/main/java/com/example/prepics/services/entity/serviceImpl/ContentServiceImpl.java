package com.example.prepics.services.entity.serviceImpl;

import com.example.prepics.entity.Content;
import com.example.prepics.models.VideoInfo;
import com.example.prepics.repositories.ContentRepository;
import com.example.prepics.services.entity.ContentService;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.google.api.client.util.Base64;
import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContentServiceImpl implements ContentService {

    private static final Logger log = LoggerFactory.getLogger(ContentServiceImpl.class);

    @Value("${video.path}")
    private String VIDEO_DIR;

    @Autowired
    private ContentRepository contentRepository;

    @Override
    public Optional<Content> findById(Class<Content> clazz, Long aLong, boolean type)
            throws ChangeSetPersister.NotFoundException {
        Optional<Content> content = contentRepository.findById(clazz, aLong);
        if (content.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        return content;
    }

    @Override
    public Optional<List<Content>> findAll(Class<Content> clazz, boolean type)
            throws ChangeSetPersister.NotFoundException {
        return contentRepository.findAll(clazz);
    }

    @Override
    public Optional<Content> delete(Long aLong, boolean type) throws ChangeSetPersister.NotFoundException, IOException {
        Optional<Content> content = this.findById(Content.class, aLong, type);
        if (type) {
            return contentRepository.delete(aLong);
        }
        deleteVideo(content.get().getVideoData());

        return contentRepository.delete(aLong);
    }

    @Override
    public Optional<Content> update(Content entity, boolean type) throws ChangeSetPersister.NotFoundException {
        Optional<Content> content = contentRepository.findById(Content.class, entity.getId());
        if (content.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        return contentRepository.update(entity);
    }

    @Override
    public Optional<Content> create(Content entity, boolean type)
            throws EntityExistsException, ChangeSetPersister.NotFoundException {
        return contentRepository.create(entity);
    }

    @Override
    public Optional<byte[]> getByteArrayFromImageURL(String url) {
        try {
            URL imageUrl = new URL(url);
            URLConnection connection = imageUrl.openConnection();

            try (InputStream is = connection.getInputStream();
                 ByteArrayOutputStream stream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    stream.write(buffer, 0, read);
                }

                stream.flush();
                return Optional.ofNullable(Base64.encodeBase64(stream.toByteArray()));
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> processAndSaveVideo(MultipartFile file) throws IOException {
        validateVideo(file);

        String fileName = UUID.randomUUID().toString() + ".mp4";
        Path tempPath = Files.createTempFile("temp_", ".mp4");
        Path finalPath = Paths.get(VIDEO_DIR + fileName);

        Files.copy(file.getInputStream(), tempPath);

        try {
            Files.createDirectories(Paths.get(VIDEO_DIR));

            VideoInfo videoInfo = analyzeVideo(tempPath.toString());
            compressVideo(tempPath.toString(), finalPath.toString(), videoInfo);

            return fileName.describeConstable() ;

        } finally {
            Files.deleteIfExists(tempPath);
        }
    }

    @Override
    public Optional<byte[]> extractImageAsBase64(String fileName) {
        try {
            Path finalPath = Paths.get(VIDEO_DIR + fileName);
            Path tempThumbnailPath = Files.createTempFile("temp_thumb_", ".jpg");

            FFmpeg.atPath()
                    .addInput(UrlInput.fromPath(finalPath))
                    .addOutput(UrlOutput.toPath(tempThumbnailPath))
                    .addArguments("-ss", "1") // Lấy frame tại giây thứ 1
                    .addArguments("-vframes", "1") // Chỉ lấy 1 frame
                    .addArguments("-q:v", "2") // Chất lượng thumbnail
                    .addArguments("-vf", "scale=480:-1") // Resize thumbnail
                    .execute();

            Optional<byte[]> base64Thumbnail =
                    Optional.ofNullable(Base64.encodeBase64(Files.readAllBytes(tempThumbnailPath)));
            Files.deleteIfExists(tempThumbnailPath);
            return base64Thumbnail;

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract thumbnail: " + e.getMessage());
        }
    }

    private void validateVideo(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        // 500MB
        long MAX_FILE_SIZE = 500 * 1024 * 1024;
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit");
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("video/")) {
            throw new IllegalArgumentException("File is not a video");
        }
    }

    private VideoInfo analyzeVideo(String inputPath) {
        FFprobe ffprobe = FFprobe.atPath();
        FFprobeResult result = ffprobe.setInput(inputPath).execute();

        VideoInfo info = new VideoInfo();
        info.setWidth(result.getStreams().getFirst().getWidth());
        info.setHeight(result.getStreams().getFirst().getHeight());
        info.setBitrate(result.getStreams().getFirst().getBitRate());
        info.setDuration(result.getStreams().getFirst().getDuration());

        return info;
    }

    private void compressVideo(String inputPath, String outputPath, VideoInfo info) {
        try {
            FFmpeg.atPath()
                    .addInput(UrlInput.fromPath(Paths.get(inputPath)))
                    .addOutput(UrlOutput.toPath(Paths.get(outputPath)))
                    .addArguments("-c:v", "libx264") // Sử dụng codec video libx264
                    .addArguments("-crf", "17") // Chất lượng video với CRF 17 (cao)
                    .addArguments("-preset", "slow") // Thiết lập tốc độ nén (chất lượng cao hơn)
                    .addArguments("-vf", "scale=iw:ih") // Giữ nguyên kích thước video
                    .addArguments("-r", "original") // Giữ nguyên tần số khung hình gốc
                    .addArguments("-c:a", "aac") // Sử dụng codec âm thanh AAC
                    .addArguments("-b:a", "320k") // Bitrate âm thanh 320 kbps
                    .addArguments("-profile:v", "high") // Thiết lập profile video cao
                    .addArguments("-level", "5.2") // Thiết lập level video cho tương thích
                    .addArguments("-tune", "film") // Tối ưu hóa cho nội dung phim
                    .addArguments("-threads", "0") // Sử dụng tất cả luồng có sẵn
                    .addArguments("-movflags", "+faststart") // Tối ưu hóa cho phát lại nhanh
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<byte[]> getVideo(String fileName) throws IOException {
        Path path = Paths.get(VIDEO_DIR + fileName);
        if (!Files.exists(path)) {
            throw new IOException("Video not found: " + fileName);
        }
        return Optional.of(Files.readAllBytes(path));
    }

    @Override
    public void deleteVideo(String fileName) throws IOException {
        Path path = Paths.get(VIDEO_DIR + fileName);
        if (!Files.exists(path)) {
            throw new IOException("Video not exits: " + fileName);
        }
        Files.deleteIfExists(path);
    }

    public ByteArrayOutputStream changeImageSize(byte[] base64Image, int width, int height) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(base64Image);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            BufferedImage originalImage = ImageIO.read(inputStream);
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, width, height, null);
            g.dispose();

            ImageIO.write(resizedImage, "png", outputStream);
            return outputStream;
        }
    }
}
