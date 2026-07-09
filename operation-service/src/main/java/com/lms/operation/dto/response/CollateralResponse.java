package com.lms.operation.dto.response;

import com.lms.operation.domain.Collateral;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CollateralResponse(
        Integer collateralId,
        Integer applicationId,
        String collateralType,
        String description,
        BigDecimal estimatedValue,
        String ownerName,
        String ownerNationalId,
        String verificationStatus,
        Integer verifiedBy,
        LocalDateTime verifiedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CollateralResponse from(Collateral c) {
        return new CollateralResponse(
                c.getCollateralId(),
                c.getApplication() != null ? c.getApplication().getApplicationId() : null,
                c.getCollateralType(),
                c.getDescription(),
                c.getEstimatedValue(),
                c.getOwnerName(),
                c.getOwnerNationalId(),
                c.getVerificationStatus(),
                c.getVerifiedBy(),
                c.getVerifiedAt(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}