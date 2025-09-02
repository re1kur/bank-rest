package com.example.bankcards.util;

import com.example.bankcards.core.dto.auth.JwtPair;
import com.example.bankcards.entity.User;
import com.nimbusds.jwt.SignedJWT;

import java.security.interfaces.RSAPublicKey;

public interface JwtProvider {
    JwtPair provide(User user);

    void verify(SignedJWT token);

    String readKidFromFile(String kidPath);

    RSAPublicKey readPublicKeyFromFile(String publicKeyPath);
}
