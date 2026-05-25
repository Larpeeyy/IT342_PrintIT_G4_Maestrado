package com.printit.backend.features.files;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    private static final String PRINT_ORDER_UPLOAD_DIR = "uploads/print-orders";

    @GetMapping("/print-orders/{fileName:.+}")
    public ResponseEntity<Resource> downloadPrintOrderFile(
            @PathVariable String fileName
    ) {
        try {
            Path filePath = Paths.get(PRINT_ORDER_UPLOAD_DIR)
                    .toAbsolutePath()
                    .normalize()
                    .resolve(fileName)
                    .normalize();

            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found.");
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File is not readable.");
            }

            String contentType = Files.probeContentType(filePath);

            if (contentType == null || contentType.isBlank()) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename(filePath.getFileName().toString())
                                    .build()
                                    .toString()
                    )
                    .body(resource);
        } catch (Exception error) {
            throw new RuntimeException("Unable to download file.");
        }
    }
}