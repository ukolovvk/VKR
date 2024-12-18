package ru.ssau.backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.backend.entity.Audio;

/**
 * @author ukolov-victor
 */
public interface AudioRepository extends JpaRepository<Audio, Long> {
    List<Audio> findByUserId(Long userId);
}
