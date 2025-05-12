package ru.ssau.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ukolov-victor
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Audio {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audio_seq")
    @SequenceGenerator(name = "audio_seq", sequenceName = "audio_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "uploaded", nullable = false)
    private Long uploadedTimestamp;

    @Column(name = "etag", nullable = false)
    private String etag;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "bucket", nullable = false)
    private String bucket;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "report_link", nullable = true)
    private String reportLink;

    @Column(name = "analyze_finished", nullable = true)
    private Long analyzeFinished;

    @Column(name = "analyze_started", nullable = true)
    private Long analyzeStarted;

    @Column(name = "model", nullable = false)
    private String model;

    public Audio(String uuid, String originalFilename, long size, Long uploadedTimestamp, String etag, Long userId, String bucket, String status, String model) {
        this.uuid = uuid;
        this.originalFilename = originalFilename;
        this.size = size;
        this.uploadedTimestamp = uploadedTimestamp;
        this.etag = etag;
        this.userId = userId;
        this.bucket = bucket;
        this.status = status;
        this.model = model;
    }
}
