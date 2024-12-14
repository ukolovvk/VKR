package ru.ssau.auth.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ssau.auth.data.CreateUserReq;
import ru.ssau.auth.data.LoginReq;
import ru.ssau.auth.exceptions.CreateUserException;
import ru.ssau.auth.service.JwtTokenService;
import ru.ssau.auth.service.UserService;

/**
 * @author ukolov-victor
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserService userService;
    private JwtTokenService jwtService;

    @Autowired
    public AuthController(UserService userService, JwtTokenService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> signup(@RequestBody CreateUserReq createUserReq) {
        try {
            userService.createUser(createUserReq);
            return new ResponseEntity<>(new HashMap<>(){{ put("message", "success"); }}, HttpStatus.OK);
        } catch (CreateUserException ex) {
            return new ResponseEntity<>(new HashMap<>(){{ put("message", ex.getMessage()); }}, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> signin(@RequestBody LoginReq loginReq) {
        try {
            if (userService.checkUser(loginReq)) {
                String token = jwtService.createToken(loginReq.getUsername());
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.SET_COOKIE, "token=" + token + "; HttpOnly; Path=/; Secure;");
                return ResponseEntity.ok().headers(headers).build();
            }
            else
                return new ResponseEntity<>(new HashMap<>(){{ put("message", "invalid username or password"); }}, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(new HashMap<>(){{ put("message", ex.getMessage()); }}, HttpStatus.BAD_REQUEST);
        }
    }
}
