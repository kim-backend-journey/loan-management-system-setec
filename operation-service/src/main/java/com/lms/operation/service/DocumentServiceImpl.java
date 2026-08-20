package com.lms.operation.service;

import com.lms.operation.domain.Document;
import com.lms.operation.domain.LoanApplication;
import com.lms.operation.dto.request.CreateDocumentRequest;
import com.lms.operation.dto.request.VerifyDocumentRequest;
import com.lms.operation.dto.response.DocumentResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.DocumentRepository;
import com.lms.operation.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public DocumentResponse createDocument(Integer applicationId, CreateDocumentRequest request) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        Document document = new Document();
        document.setApplication(application);
        document.setDocumentType(request.getDocumentType());
        document.setFilePath(request.getFilePath());
        document.setOriginalFileName(request.getOriginalFileName());
        document.setMimeType(request.getMimeType());
        document.setFileSize(request.getFileSize());
        document.setVerificationStatus("Pending");

        Document saved = documentRepository.save(document);
        return DocumentResponse.from(saved);
    }

    @Override
    @Transactional
    public DocumentResponse uploadDocument(Integer applicationId, MultipartFile file, String documentType) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        String storedFileName;
        try {
            storedFileName = fileStorageService.saveFile(file);
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        Document document = new Document();
        document.setApplication(application);
        document.setDocumentType(documentType);
        document.setFilePath(storedFileName);
        document.setOriginalFileName(file.getOriginalFilename());
        document.setMimeType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setVerificationStatus("Pending");

        Document saved = documentRepository.save(document);
        return DocumentResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocuments(Integer applicationId) {
        return documentRepository.findByApplication_ApplicationId(applicationId).stream()
                .map(DocumentResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public DocumentResponse verifyDocument(Integer applicationId, Integer documentId, VerifyDocumentRequest request) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (document.getApplication() == null
                || !document.getApplication().getApplicationId().equals(applicationId)) {
            throw new AppException(ErrorCode.DOCUMENT_NOT_FOUND);
        }

        document.setVerificationStatus("Verified");
        document.setVerifiedBy(getCurrentUserId());
        document.setVerificationComment(request.getVerificationComment());
        document.setVerifiedAt(LocalDateTime.now());

        Document saved = documentRepository.save(document);
        return DocumentResponse.from(saved);
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken token) {
            Object details = token.getDetails();
            if (details instanceof Integer userId) {
                return userId;
            }
        }
        return null;
    }
}