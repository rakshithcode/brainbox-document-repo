// src/main/java/com/example/BrainBox_Doc/controller/DocumentController.java
package com.example.BrainBox_Doc.controller;

import com.example.BrainBox_Doc.model.Document;
import com.example.BrainBox_Doc.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String tags,
            @RequestParam String category,
            @RequestParam String author) throws IOException {
        Document doc = documentService.store(file, title, description, tags, category, author);
        return ResponseEntity.ok(doc);
    }

    @GetMapping
    public List<Document> searchDocuments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String author) {
        return documentService.search(query, category, author);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) throws MalformedURLException {
        Document doc = documentService.getDocument(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        Resource resource = documentService.loadFileAsResource(doc.getFilename());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getOriginalName() + "\"")
                .body(resource);
    }
}