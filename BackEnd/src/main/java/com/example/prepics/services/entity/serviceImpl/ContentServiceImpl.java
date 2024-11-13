package com.example.prepics.services.entity.serviceImpl;

import com.example.prepics.entity.Content;
import com.example.prepics.repositories.ContentRepository;
import com.example.prepics.services.entity.ContentService;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.google.api.client.util.Base64;
import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
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
    public Optional<List<Content>> findAll(Class<Content> clazz, boolean type)
            throws ChangeSetPersister.NotFoundException {
        return contentRepository.findAllByType(type);
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
    public Optional<List<Content>> findAllByTags(List<String> tags) {
        return contentRepository.findAllByTags(tags);
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
}
