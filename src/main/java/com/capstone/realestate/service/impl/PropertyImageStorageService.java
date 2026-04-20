package com.capstone.realestate.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PropertyImageStorageService {

    private final Path imageDirectory;

    public PropertyImageStorageService(@Value("${app.upload.base-dir:uploads}") String uploadBaseDir) {
        this.imageDirectory = Path.of(uploadBaseDir, "property-images").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.imageDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not initialize property image storage directory", exception);
        }
    }

    public List<String> storeImages(List<MultipartFile> images) {
        List<String> storedUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                continue;
            }

            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed.");
            }

            String extension = resolveExtension(image.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension;
            Path target = imageDirectory.resolve(fileName).normalize();

            if (!target.startsWith(imageDirectory)) {
                throw new IllegalArgumentException("Invalid file path.");
            }

            try {
                Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to store image: " + image.getOriginalFilename(), exception);
            }

            storedUrls.add("/uploads/property-images/" + fileName);
        }

        if (storedUrls.isEmpty()) {
            throw new IllegalArgumentException("Please upload at least one image.");
        }

        return storedUrls;
    }

    private String resolveExtension(String originalFileName) {
        if (originalFileName == null || !originalFileName.contains(".")) {
            return ".jpg";
        }

        String extension = originalFileName.substring(originalFileName.lastIndexOf('.')).toLowerCase();
        return extension.matches("\\.[a-z0-9]{2,6}") ? extension : ".jpg";
    }
}
