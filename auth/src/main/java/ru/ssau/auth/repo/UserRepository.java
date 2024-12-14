package ru.ssau.auth.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.auth.data.User;

/**
 * @author ukolov-victor
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
