package com.lms.operation.service;

import com.lms.operation.dto.request.CreateDocumentRequest;
import com.lms.operation.dto.request.VerifyDocumentRequest;
import com.lms.operation.dto.response.DocumentResponse;

import java.util.List;

public interface DocumentService {

    DocumentResponse createDocument(Integer applicationId, CreateDocumentRequest request);

    List<DocumentResponse> getDocuments(Integer applicationId);

    DocumentResponse verifyDocument(Integer applicationId, Integer documentId, VerifyDocumentRequest request);
}