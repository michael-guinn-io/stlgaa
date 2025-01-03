package org.stlgaa.security;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.nio.charset.StandardCharsets;

public class SLGHash {

    public static String createHash(String plainText, String salt) {

        final int ITERATIONS = 9;
        final int MEMORY_SIZE = 8374;
        final int PARALLELISM = 1;
        final int LENGTH = 51;

        Argon2Parameters hashParams = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt.getBytes(StandardCharsets.UTF_8))
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY_SIZE)
                .withParallelism(PARALLELISM)
                .build();

        Argon2BytesGenerator hashGenerator = new Argon2BytesGenerator();
        hashGenerator.init(hashParams);

        byte[] result = new byte[LENGTH];
        hashGenerator.generateBytes(plainText.getBytes(StandardCharsets.UTF_8), result);

        return new String(result, StandardCharsets.UTF_8);
    }
}
