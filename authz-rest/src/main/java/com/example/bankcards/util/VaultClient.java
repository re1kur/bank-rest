package com.example.bankcards.util;

import java.util.Map;
import java.util.UUID;

public interface VaultClient {
    Map<String, Object> getData();

    String sign(String mappedToken, UUID userId);
}
