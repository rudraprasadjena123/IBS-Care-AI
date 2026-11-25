package com.ibscare.diary;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class DiaryService {
    private final DiaryRepository repository;

    public DiaryService(DiaryRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<DiaryDto> list() {
        return repository.findTop500ByOrderByOccurredAtDesc().stream().map(DiaryDto::from).toList();
    }

    public DiaryDto create(DiaryDto request) {
        validateValue(request.kind(), request.severity());
        // The server assigns identity; client IDs cannot overwrite existing records.
        var entry = new DiaryEntry(UUID.randomUUID().toString(), request.kind(), request.title().trim(),
            request.severity(), request.notes(), request.occurredAt());
        return DiaryDto.from(repository.save(entry));
    }

    public static void validateValue(String kind, BigDecimal value) {
        boolean valid = switch (kind) {
            case "bowel" -> value.compareTo(BigDecimal.ONE) >= 0
                && value.compareTo(BigDecimal.valueOf(7)) <= 0 && value.stripTrailingZeros().scale() <= 0;
            case "symptom", "food" -> value.compareTo(BigDecimal.ZERO) >= 0
                && value.compareTo(BigDecimal.TEN) <= 0 && value.stripTrailingZeros().scale() <= 0;
            case "lifestyle" -> value.compareTo(BigDecimal.ZERO) >= 0 && value.compareTo(BigDecimal.valueOf(24)) <= 0;
            default -> false;
        };
        if (!valid) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Value outside the allowed range for this entry type");
    }

    public void delete(String id) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        repository.deleteById(id);
    }
}
