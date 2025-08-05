package com.example.bankcards.util.impl;

import com.example.bankcards.core.dto.auth.JwtPair;
import com.example.bankcards.core.exception.TokenSignatureIsInvalidException;
import com.example.bankcards.entity.sql.Role;
import com.example.bankcards.entity.sql.User;
import com.example.bankcards.service.RefreshTokenService;
import com.example.bankcards.util.JwtProvider;
import com.example.bankcards.util.VaultClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider {
    //todo: tests
    private final VaultClient vaultClient;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.privateKeyPath}")
    private String privateKeyPath;

    @Value("${jwt.publicKeyPath}")
    private String publicKeyPath;

    @Value("${jwt.kidPath}")
    private String kidPath;

    @Value("${jwt.keySize}")
    private int keySize;

    @Value("${jwt.ttl.access}")
    private Integer jwtAccessTtl;

    @Value("${jwt.ttl.refresh}")
    private Integer jwtRefreshTtl;

    private String kid;
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private RSASSASigner signer;

//    private final ObjectMapper objectMapper;

//    @PostConstruct
//    private void setUp() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        log.info("POST CONSTRUCT: GET KID REQUEST");
//
//        Map<String, Object> data = vaultClient.getData();
//
//        Integer latestVersion = (Integer) data.get("latest_version");
//        kid = String.valueOf(latestVersion);
//        log.info("POST CONSTRUCT: KID REQUEST SUCCESS: {}", kid);
//
//        log.info("POST CONSTRUCT: GET PUBLIC KEY");
//
//        Map<String, Map<String, Object>> keys = (Map<String, Map<String, Object>>) data.get("keys");
//        Map<String, Object> latestKey = keys.get(kid);
//
//        if (latestKey == null) {
//            throw new IllegalStateException("No key found for kid=" + kid);
//        }
//
//        String publicKeyPem = (String) latestKey.get("public_key");
//        publicKey = parsePublicKey(publicKeyPem);
//        log.info("POST CONSTRUCT: PUBLIC KEY REQUEST SUCCESS");
//    }


//    private RSAPublicKey parsePublicKey(String pem) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        String cleanedPem = pem
//                .replace("-----BEGIN PUBLIC KEY-----", "")
//                .replace("-----END PUBLIC KEY-----", "")
//                .replaceAll("\\s", "");
//
//        byte[] decoded = Base64.getDecoder().decode(cleanedPem);
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
//
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//
//        log.info("PEM: \n{}", pem);
//        log.info("PEM Cleaned Base64: {}", cleanedPem);
//        log.info("PEM Decoded Length: {}", decoded.length);
//        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
//    }


    @Override
    public JwtPair provide(User user) {
        log.info("PROVIDING JWT: [{}]", user.getId());
        checkKeys();

        String accessToken = generateAccessToken(user);

        String refreshToken = generateRefreshToken(user);

        JwtPair response = new JwtPair(accessToken, refreshToken);

        log.info("SUCCESSFULLY PROVIDED JWT: [{}]", user.getId());
        return response;
    }

    @SneakyThrows
    private void generateKeyPair() {
        kid = UUID.randomUUID().toString();
        KeyPairGenerator pairGenerator = KeyPairGenerator.getInstance("RSA");
        pairGenerator.initialize(keySize);
        KeyPair keyPair = pairGenerator.generateKeyPair();

        Files.deleteIfExists(Paths.get(publicKeyPath));
        Files.deleteIfExists(Paths.get(privateKeyPath));
        Files.deleteIfExists(Paths.get(kidPath));

        Files.write(
                Paths.get(publicKeyPath),
                keyPair.getPublic().getEncoded(),
                StandardOpenOption.CREATE_NEW
        );

        Files.write(
                Paths.get(privateKeyPath),
                keyPair.getPrivate().getEncoded(),
                StandardOpenOption.CREATE_NEW
        );

        Files.write(
                Paths.get(kidPath),
                kid.getBytes(),
                StandardOpenOption.CREATE_NEW
        );
    }

    @SneakyThrows
    private RSAPrivateKey readPrivateKeyFromFile(String path) {
        byte[] keyContent = Files.readAllBytes(Paths.get(path));
        return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(keyContent));
    }

    @SneakyThrows
    @Override
    public RSAPublicKey readPublicKeyFromFile(String path) {
        byte[] keyContent = Files.readAllBytes(Paths.get(path));
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(keyContent));
    }

    @SneakyThrows
    @Override
    public String readKidFromFile(String kidPath) {
        byte[] bytes = Files.readAllBytes(Paths.get(kidPath));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @PostConstruct
    private void checkKeys() {
        if (!Files.exists(Paths.get(publicKeyPath)) || !Files.exists(Paths.get(kidPath))) {
            generateKeyPair();
        }

        kid = readKidFromFile(kidPath);
        publicKey = readPublicKeyFromFile(publicKeyPath);
        privateKey = readPrivateKeyFromFile(privateKeyPath);
        signer = new RSASSASigner(privateKey);
    }

    @Override
    public void verify(SignedJWT token) {
        RSASSAVerifier verifier = new RSASSAVerifier(publicKey);
        String serialized = token.serialize();

        try {
            boolean verified = token.verify(verifier);
            if (!verified) {
                throw new TokenSignatureIsInvalidException(
                        "Refresh token [%s] has invalid signature.".formatted(serialized)
                );
            }
        } catch (JOSEException e) {
            throw new TokenSignatureIsInvalidException(
                    "Failed to verify refresh token [%s] due to internal error.".formatted(serialized)
            );
        }
    }

    @SneakyThrows
    private String generateAccessToken(User user) {
        JWSHeader accessHeader = new JWSHeader
                .Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .keyID(kid)
                .build();

        UUID userId = user.getId();
        String roleNames = user.getRoles().stream()
                .map(Role::getName).toList()
                .toString().replace("[", "")
                .replace("]", "");
        JWTClaimsSet accessPayload = new JWTClaimsSet.Builder()
                .subject(userId.toString())
                .claim("roles", roleNames)
                .claim("token_type", "access")
                .issueTime(new Date())
                .expirationTime(Date.from(LocalDateTime.now().plusMinutes(jwtAccessTtl).toInstant(ZoneOffset.UTC)))
                .build();


//        String headerJson = objectMapper.writeValueAsString(accessHeader.toJSONObject());
//        String payloadJson = objectMapper.writeValueAsString(accessPayload.toJSONObject());
//
//        String encodedHeader = Base64URL.encode(headerJson).toString();
//        String encodedPayload = Base64URL.encode(payloadJson).toString();
//
//        String unsignedToken = encodedHeader + "." + encodedPayload;

//        log.info("UNSIGNED: {}", unsignedToken);
//
//        String signedAccessToken = vaultClient.sign(unsignedToken, userId);

        SignedJWT accessToken = new SignedJWT(accessHeader, accessPayload);
        accessToken.sign(signer);

        log.info("GENERATED ACCESS TOKEN: [{}]", userId);
        return accessToken.serialize();
    }

    @SneakyThrows
    private String generateRefreshToken(User user) {
        JWSHeader refreshHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .keyID(kid)
                .build();

        UUID userId = user.getId();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(jwtRefreshTtl);
        JWTClaimsSet refreshPayload = new JWTClaimsSet.Builder()
                .subject(userId.toString())
                .claim("token_type", "refresh")
                .issueTime(new Date())
                .expirationTime(Date.from(expiresAt.toInstant(ZoneOffset.UTC)))
                .build();

//        String headerJson = objectMapper.writeValueAsString(refreshHeader.toJSONObject());
//        String payloadJson = objectMapper.writeValueAsString(refreshPayload.toJSONObject());
//
//        String encodedHeader = Base64URL.encode(headerJson).toString();
//        String encodedPayload = Base64URL.encode(payloadJson).toString();
//
//        String unsignedToken = encodedHeader + "." + encodedPayload;
//        String signedRefreshToken = vaultClient.sign(unsignedToken, userId);

        SignedJWT refreshToken = new SignedJWT(refreshHeader, refreshPayload);
        refreshToken.sign(signer);

        String signedRefreshToken = refreshToken.serialize();

        refreshTokenService.create(signedRefreshToken, userId, expiresAt);

        log.info("GENERATED REFRESH TOKEN: [{}]", userId);
        return signedRefreshToken;
    }
}
