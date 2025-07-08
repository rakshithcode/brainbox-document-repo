package com.example.BrainBox_Doc.repository;

import com.example.BrainBox_Doc.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTagsContainingIgnoreCase(
        String title, String description, String tags
    );
    List<Document> findByCategory(String category);
    List<Document> findByAuthor(String author);
}
