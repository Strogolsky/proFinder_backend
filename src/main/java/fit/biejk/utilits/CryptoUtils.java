package fit.biejk.utilits;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.text.RandomStringGenerator;

@ApplicationScoped
public final class CryptoUtils {
    /**
     * Cost factor for BCrypt password hashing.
     * Higher value increases security but also computation time.
     */
    private static final int BCRYPT_COST = 12;

    public static String generateNumericCode(int length) {
        return new RandomStringGenerator.Builder()
                .withinRange('0', '9')
                .build()
                .generate(length);
    }

    public static String hash(String plain) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, plain.toCharArray());
    }

    public static boolean verify(String plain, String hashed) {
        return BCrypt.verifyer().verify(plain.toCharArray(), hashed).verified;
    }

}
