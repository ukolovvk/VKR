package ru.ssau.auth.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssau.auth.data.CreateUserReq;
import ru.ssau.auth.data.LoginReq;
import ru.ssau.auth.data.Role;
import ru.ssau.auth.data.User;
import ru.ssau.auth.exceptions.CreateUserException;
import ru.ssau.auth.exceptions.UserNotFoundException;
import ru.ssau.auth.repo.UserRepository;
import ru.ssau.auth.utils.PasswordUtils;

/**
 * @author ukolov-victor
 */

@Service
public class UserService {
    private final UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User createUser(CreateUserReq createUserReq) throws CreateUserException {
        if (this.userRepo.existsByUsername(createUserReq.getUsername())) {
            throw new CreateUserException("user with such username already exists");
        }

        if (this.userRepo.existsByEmail(createUserReq.getPassword())) {
            throw new CreateUserException("user with such email already exists");
        }

        String seed = PasswordUtils.generateSeed();
        String passHash = null;
        try {
            passHash = PasswordUtils.calculatePassHash(createUserReq.getPassword(), seed);
        } catch (Exception ex) {
            throw new CreateUserException("failed to calculate pass hash");
        }
        User user = User.builder()
                .email(createUserReq.getEmail())
                .username(createUserReq.getUsername())
                .password(passHash)
                .seed(seed)
                .role(Role.USER)
                .build();
        return this.userRepo.save(user);
    }

    public boolean checkUser(LoginReq loginReq) throws Exception {
        User user = this.userRepo.findByUsername(loginReq.getUsername())
                .orElseThrow(() -> new UserNotFoundException("user with such username hasn't been found"));
        String gotPassHash = PasswordUtils.calculatePassHash(loginReq.getPassword(), user.getSeed());
        return gotPassHash.equals(user.getPassword());
    }
}
