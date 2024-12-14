package ru.ssau.auth.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * @author ukolov-victor
 */
public class PasswordUtils {

    public static String generateSeed() {
        SecureRandom random = new SecureRandom();
        byte[] seed = new byte[16];
        random.nextBytes(seed);
        return Base64.getEncoder().encodeToString(seed);
    }

    public static String calculatePassHash(String password, String seed) throws Exception {
        int iteration = 65536;
        int keyLength = 256;
        byte[] seedBin = Base64.getDecoder().decode(seed);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), seedBin, iteration, keyLength);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new Exception("Failed to calculate password hash");
        }
    }
}
