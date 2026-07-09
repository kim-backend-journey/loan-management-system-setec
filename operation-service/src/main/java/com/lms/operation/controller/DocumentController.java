package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.request.CreateDocumentRequest;
import com.lms.operation.dto.request.VerifyDocumentRequest;
import com.lms.operation.dto.response.DocumentResponse;
import com.lms.operation.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications/{id}/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DocumentResponse>> createDocument(
            @PathVariable("id") Integer applicationId,
            @Valid @RequestBody CreateDocumentRequest request) {
        DocumentResponse response = documentService.createDocument(applicationId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Document created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocuments(
            @PathVariable("id") Integer applicationId) {
        List<DocumentResponse> response = documentService.getDocuments(applicationId);
        return ResponseEntity.ok(ApiResponse.success(response, "Documents retrieved"));
    }

    @PatchMapping("/{docId}/verify")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DocumentResponse>> verifyDocument(
            @PathVariable("id") Integer applicationId,
            @PathVariable("docId") Integer documentId,
            @Valid @RequestBody VerifyDocumentRequest request) {
        DocumentResponse response = documentService.verifyDocument(applicationId, documentId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Document verified"));
    }
}