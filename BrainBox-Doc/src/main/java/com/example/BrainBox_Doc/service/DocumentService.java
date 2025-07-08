// src/main/java/com/example/BrainBox_Doc/service/DocumentService.java
package com.example.BrainBox_Doc.service;

import com.example.BrainBox_Doc.model.Document;
import com.example.BrainBox_Doc.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!", e);
        }
    }

    public Document store(MultipartFile file, String title, String description, String tags, String category, String author) throws IOException {
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir).resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Document doc = new Document();
        doc.setTitle(title);
        doc.setDescription(description);
        doc.setFilename(filename);
        doc.setOriginalName(file.getOriginalFilename());
        doc.setUploadDate(LocalDateTime.now());
        doc.setAuthor(author);
        doc.setTags(tags);
        doc.setCategory(category);

        return documentRepository.save(doc);
    }

    public List<Document> search(String query, String category, String author) {
        if (query != null && !query.isEmpty()) {
            return documentRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTagsContainingIgnoreCase(query, query, query);
        } else if (category != null && !category.isEmpty()) {
            return documentRepository.findByCategory(category);
        } else if (author != null && !author.isEmpty()) {
            return documentRepository.findByAuthor(author);
        } else {
            return documentRepository.findAll();
        }
    }

    public Optional<Document> getDocument(Long id) {
        return documentRepository.findById(id);
    }

    public Resource loadFileAsResource(String filename) throws MalformedURLException {
        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new RuntimeException("File not found " + filename);
        }
    }
}