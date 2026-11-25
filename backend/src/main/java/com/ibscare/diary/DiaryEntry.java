package com.ibscare.diary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "demo_diary_entries")
public class DiaryEntry {
    @Id
    @Column(length = 36, nullable = false)
    private String id;
    @Column(nullable = false, length = 20)
    private String kind;
    @Column(nullable = false, length = 120)
    private String title;
    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal severity;
    @Column(nullable = false, length = 1000)
    private String notes;
    @Column(nullable = false)
    private Instant occurredAt;

    protected DiaryEntry() {}

    public DiaryEntry(String id, String kind, String title, BigDecimal severity, String notes, Instant occurredAt) {
        this.id = id;
        this.kind = kind;
        this.title = title;
        this.severity = severity;
        this.notes = notes;
        this.occurredAt = occurredAt;
    }

    public String getId() { return id; }
    public String getKind() { return kind; }
    public String getTitle() { return title; }
    public BigDecimal getSeverity() { return severity; }
    public String getNotes() { return notes; }
    public Instant getOccurredAt() { return occurredAt; }
}
