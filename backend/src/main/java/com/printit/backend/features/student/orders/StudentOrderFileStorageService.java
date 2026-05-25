package com.printit.backend.features.student.orders;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Locale;

@Service
public class StudentOrderFileStorageService {

    private static final String UPLOAD_DIR = "uploads/print-orders";

    public OrderFileUploadResponse uploadOrderFile(MultipartFile file) {
        validateFile(file);

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR)
                    .toAbsolutePath()
                    .normalize();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = file.getOriginalFilename() != null
                    ? file.getOriginalFilename().trim()
                    : "uploaded-file";

            String safeFileName = originalName
                    .replaceAll("[^a-zA-Z0-9._-]", "_")
                    .replaceAll("_+", "_");

            String storedFileName = Instant.now().toEpochMilli() + "-" + safeFileName;

            Path targetPath = uploadPath.resolve(storedFileName).normalize();

            file.transferTo(targetPath.toFile());

            String fileUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/files/print-orders/")
                    .path(storedFileName)
                    .toUriString();

            return new OrderFileUploadResponse(originalName, fileUrl);

        } catch (IOException error) {
            throw new RuntimeException("Failed to upload file.");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required.");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new RuntimeException("Invalid file name.");
        }

        String lowerFileName = fileName.toLowerCase(Locale.ROOT);

        if (!(lowerFileName.endsWith(".pdf") || lowerFileName.endsWith(".docx"))) {
            throw new RuntimeException("Only PDF and DOCX files are allowed.");
        }

        long maxSize = 50L * 1024L * 1024L;

        if (file.getSize() > maxSize) {
            throw new RuntimeException("File must not exceed 50 MB.");
        }
    }
}
