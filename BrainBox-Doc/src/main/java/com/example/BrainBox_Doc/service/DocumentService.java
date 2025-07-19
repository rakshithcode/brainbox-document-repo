package com.example.BrainBox_Doc.service;

import com.example.BrainBox_Doc.model.Document;
import com.example.BrainBox_Doc.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public Document saveDocument(Document document) {
        return documentRepository.save(document);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    public Optional<Document> updateDocument(Long id, Document updatedDocument) {
        return documentRepository.findById(id).map(document -> {
            document.setTitle(updatedDocument.getTitle());
            document.setDescription(updatedDocument.getDescription());
            // Add other fields as needed
            return documentRepository.save(document);
        });
    }

    public boolean deleteDocument(Long id) {
        if (documentRepository.existsById(id)) {
            documentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Document store(MultipartFile file, String title, String description) {
        System.out.println("Received file: " + (file != null ? file.getOriginalFilename() : "null"));
        Document doc = new Document();
        doc.setTitle(title);
        doc.setDescription(description);

        if (file != null && !file.isEmpty()) {
            try {
                String uploadDir = "uploads";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                file.transferTo(filePath.toFile());
                // Optionally, save file name/path in the Document entity
                // doc.setFileName(fileName);
            } catch (Exception e) {
                throw new RuntimeException("File saving failed", e);
            }
        }
        return documentRepository.save(doc);
    }
}
