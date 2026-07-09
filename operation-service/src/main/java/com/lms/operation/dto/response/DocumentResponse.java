package com.lms.operation.dto.response;

import com.lms.operation.domain.Document;

import java.time.LocalDateTime;

public record DocumentResponse(
        Integer documentId,
        Integer applicationId,
        String documentType,
        String filePath,
        String originalFileName,
        String mimeType,
        Long fileSize,
        String verificationStatus,
        Integer verifiedBy,
        String verificationComment,
        LocalDateTime verifiedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DocumentResponse from(Document d) {
        return new DocumentResponse(
                d.getDocumentId(),
                d.getApplication() != null ? d.getApplication().getApplicationId() : null,
                d.getDocumentType(),
                d.getFilePath(),
                d.getOriginalFileName(),
                d.getMimeType(),
                d.getFileSize(),
                d.getVerificationStatus(),
                d.getVerifiedBy(),
                d.getVerificationComment(),
                d.getVerifiedAt(),
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }
}