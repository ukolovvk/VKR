package ru.ssau.auth.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ukolov-victor
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserReq {
    private String username;
    private String password;
    private String email;
}
