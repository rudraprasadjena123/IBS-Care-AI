-- This deliberately separate demo table is NOT the final normalized patient schema.
CREATE TABLE demo_diary_entries (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    kind VARCHAR(20) NOT NULL,
    title VARCHAR(120) NOT NULL,
    severity DECIMAL(4,1) NOT NULL,
    notes VARCHAR(1000) NOT NULL,
    occurred_at DATETIME(6) NOT NULL,
    CONSTRAINT chk_demo_kind CHECK (kind IN ('symptom', 'food', 'lifestyle', 'bowel')),
    CONSTRAINT chk_demo_value CHECK (
        (kind IN ('symptom', 'food') AND severity BETWEEN 0 AND 10 AND severity = FLOOR(severity))
        OR (kind = 'bowel' AND severity BETWEEN 1 AND 7 AND severity = FLOOR(severity))
        OR (kind = 'lifestyle' AND severity BETWEEN 0 AND 24)
    ),
    INDEX idx_demo_occurred_at (occurred_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
