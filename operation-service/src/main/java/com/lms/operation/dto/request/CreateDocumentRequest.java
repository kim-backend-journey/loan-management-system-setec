package com.lms.operation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDocumentRequest {

    @NotBlank(message = "documentType is required")
    private String documentType;

    @NotBlank(message = "filePath is required")
    private String filePath;

    private String originalFileName;

    private String mimeType;

    private Long fileSize;
}