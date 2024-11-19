package com.example.prepics.services.cloudinary;


import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {

    Map<String, Object> uploadFile(MultipartFile file) throws IOException;

    Map<String, Object> uploadFile(File file) throws IOException;

    Map<String, Object> uploadVideo(MultipartFile file) throws IOException;

    Map<String, Object> uploadVideo(byte[] file) throws IOException;

    Map<String, Object> getFileDetails(String assetId) throws Exception;

    Map<String, Object> deleteFile(String publicId) throws IOException;
}
