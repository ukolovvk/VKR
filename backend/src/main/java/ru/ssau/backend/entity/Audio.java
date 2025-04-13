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

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "uploaded", nullable = false)
    private Long uploadedTimestamp;

    @Column(name = "s3_uuid", unique = true, nullable = false)
    private String s3Uuid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "bucket", nullable = false)
    private String bucket;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "report link", nullable = true)
    private String description;

    @Column(name = "analyze_finished", nullable = true)
    private Long analyzeFinished;

    @Column(name = "analyze_started", nullable = true)
    private Long analyzeStarted;

    public Audio(String name, long size, Long uploadedTimestamp, String s3Uuid, Long userId, String bucket, String status) {
        this.name = name;
        this.size = size;
        this.uploadedTimestamp = uploadedTimestamp;
        this.s3Uuid = s3Uuid;
        this.userId = userId;
        this.bucket = bucket;
        this.status = status;
    }
}
