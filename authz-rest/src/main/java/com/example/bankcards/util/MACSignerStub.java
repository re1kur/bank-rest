package com.example.bankcards.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.util.Base64URL;

import java.util.Set;

public class MACSignerStub implements JWSSigner {
    private final Base64URL signature;

    public MACSignerStub(Base64URL signature) {
        this.signature = signature;
    }

    @Override
    public Base64URL sign(JWSHeader header, byte[] signingInput) {
        return signature;
    }

    @Override
    public Set<JWSAlgorithm> supportedJWSAlgorithms() {
        return Set.of(JWSAlgorithm.RS256);
    }

    @Override
    public JCAContext getJCAContext() {
        return new JCAContext();
    }
}

