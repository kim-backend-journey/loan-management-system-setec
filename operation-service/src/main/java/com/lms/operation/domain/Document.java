package com.lms.operation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Integer documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private LoanApplication application;

    @Column(name = "document_type", length = 100)
    private String documentType;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "verification_status", length = 20)
    @Builder.Default
    private String verificationStatus = "Pending";

    @Column(name = "verified_by")
    private Integer verifiedBy;

    @Column(name = "verification_comment", columnDefinition = "TEXT")
    private String verificationComment;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}