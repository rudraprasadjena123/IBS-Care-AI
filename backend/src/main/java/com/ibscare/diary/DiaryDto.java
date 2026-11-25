package com.ibscare.diary;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public record DiaryDto(
    @NotBlank @Pattern(regexp = "[0-9a-fA-F-]{36}") String id,
    @NotBlank @Pattern(regexp = "symptom|food|lifestyle|bowel") String kind,
    @NotBlank @Size(max = 120) String title,
    @NotNull @DecimalMin("0") @DecimalMax("24") @Digits(integer = 2, fraction = 1) BigDecimal severity,
    @NotNull @Size(max = 1000) String notes,
    @NotNull @PastOrPresent Instant occurredAt
) {
    public static DiaryDto from(DiaryEntry entry) {
        return new DiaryDto(entry.getId(), entry.getKind(), entry.getTitle(), entry.getSeverity(), entry.getNotes(), entry.getOccurredAt());
    }
}
