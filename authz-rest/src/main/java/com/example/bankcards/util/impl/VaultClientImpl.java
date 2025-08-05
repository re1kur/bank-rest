package com.example.bankcards.util.impl;

import com.example.bankcards.util.VaultClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class VaultClientImpl implements VaultClient {
    private final RestTemplate template;

    @Value("${vault.uri}")
    private String vaultBaseUri;

    @Value("${vault.jwt-sign-key.path}")
    private String jwtSignerPath;

    @Value("${vault.token}")
    private String vaultToken;

    @Override
    public Map<String, Object> getData() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Vault-Token", vaultToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = vaultBaseUri + "/v1/transit/keys/" + jwtSignerPath;

        ResponseEntity<Map> response = template.exchange(url, HttpMethod.GET, entity, Map.class);

        return (Map<String, Object>) response.getBody().get("data");
    }


    @Override
    public String sign(String unsignedToken, UUID userId) {
        try {
            String input = Base64.getEncoder().encodeToString(unsignedToken.getBytes(StandardCharsets.UTF_8));
            log.info("BASE64 INPUT: {}", input);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Vault-Token", vaultToken);

            Map<String, Object> body = Map.of("input", input);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            String url = vaultBaseUri + "/v1/transit/sign/" + jwtSignerPath;
            ResponseEntity<Map> response = template.postForEntity(url, request, Map.class);

            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            String vaultSignature = (String) data.get("signature");
            log.info("VAULT SIGNATURE: {}", vaultSignature);

            String signatureRaw = vaultSignature.replace("vault:v1:", "");
            log.info("RAW SIGNATURE: {}", signatureRaw);

            byte[] sigBytes = Base64.getDecoder().decode(signatureRaw);
            String signatureBase64Url = Base64.getUrlEncoder().withoutPadding().encodeToString(sigBytes);

            log.info("BASE64URL SIGNATURE: {}", signatureBase64Url);
            return unsignedToken + "." + signatureBase64Url;
        } catch (Exception e) {
            log.info("Failed to sign token: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
