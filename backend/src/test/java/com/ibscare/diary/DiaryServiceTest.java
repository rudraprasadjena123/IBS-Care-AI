package com.ibscare.diary;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

class DiaryServiceTest {
    @Test
    void acceptsValidValues() {
        assertDoesNotThrow(() -> DiaryService.validateValue("symptom", new BigDecimal("10")));
        assertDoesNotThrow(() -> DiaryService.validateValue("bowel", new BigDecimal("4")));
        assertDoesNotThrow(() -> DiaryService.validateValue("lifestyle", new BigDecimal("7.5")));
    }

    @Test
    void rejectsOutOfRangeAndFractionalBristolValues() {
        assertThrows(ResponseStatusException.class, () -> DiaryService.validateValue("bowel", new BigDecimal("0")));
        assertThrows(ResponseStatusException.class, () -> DiaryService.validateValue("bowel", new BigDecimal("4.5")));
        assertThrows(ResponseStatusException.class, () -> DiaryService.validateValue("symptom", new BigDecimal("11")));
        assertThrows(ResponseStatusException.class, () -> DiaryService.validateValue("lifestyle", new BigDecimal("-1")));
    }
}
