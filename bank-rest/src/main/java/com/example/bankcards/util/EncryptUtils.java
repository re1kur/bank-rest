package com.example.bankcards.util;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultTemplate;

@Component
@RequiredArgsConstructor
public class EncryptUtils {
    private final VaultTemplate vaultTemplate;

    @Value("${vault.key.api.path}")
    private String KEY_NAME;

    public String encrypt(String plaintext) {
        return vaultTemplate.opsForTransit()
                .encrypt(KEY_NAME, plaintext);
    }

    public String decrypt(String ciphertext) {
        return vaultTemplate.opsForTransit()
                .decrypt(KEY_NAME, ciphertext);
    }
}
