package com.example.prepics.services.cloudinary.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.prepics.services.cloudinary.CloudinaryService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

  @Autowired
  private Cloudinary cloudinary;

  @Override
  public Map<String, Object> uploadFile(MultipartFile file) throws IOException {
    return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
  }

  @Override
  public Map<String, Object> uploadFile(File file) throws IOException {
    return cloudinary.uploader().upload(Files.readAllBytes(file.toPath()), ObjectUtils.emptyMap());
  }

  @Override
  public Map<String, Object> uploadVideo(File file) throws IOException {
    return cloudinary.uploader().upload(Files.readAllBytes(file.toPath()),
        ObjectUtils.asMap(
            "resource_type", "video"
        ));
  }

  @Override
  public Map<String, Object> uploadVideo(byte[] file) throws IOException {
    return cloudinary.uploader().upload(file,
        ObjectUtils.asMap(
            "resource_type", "video"
        ));
  }

  @Override
  public Map<String, Object> getFileDetails(String assetId) throws Exception {
    return cloudinary.api()
        .resourcesByAssetIDs(Collections.singleton(assetId), ObjectUtils.emptyMap());
  }

  @Override
  public Map<String, Object> deleteFile(String publicId) throws Exception {
    return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
  }

  @Override
  public String generateTransformedUrl(String publicId, int height, int width,
      String resourceType) {
    return cloudinary.url()
        .resourceType(resourceType) // Chỉ định loại tài nguyên: "image" hoặc "video"
        .transformation(new Transformation().height(height).width(width).crop("scale"))
        .generate(publicId);
  }


}
