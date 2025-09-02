//package com.example.bankcards.util;
//
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class VaultKeyInitializer {
//    private final RestTemplate restTemplate;
//
//    @Value("${spring.vault.uri}")
//    private String URI;
//
//    @Value("${spring.vault.token}")
//    private String TOKEN;
//
//    @Value("${vault.key.api.path}")
//    private String KEY;
//
//    @PostConstruct
//    private void generateKey() {
//        log.info("POST CONSTRUCT: GENERATING KEY");
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-Vault-Token", TOKEN);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> entity = new HttpEntity<>("{}", headers);
//
//        try {
//            restTemplate.exchange(URI + "/v1/transit/keys/" + KEY, HttpMethod.POST, entity, String.class);
//            log.info("VAULT ENCRYPTION KEY INITIALIZED.");
//        } catch (HttpClientErrorException e) {
//            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
//                log.info("POST CONSTRUCT: KEY ALREADY EXISTS OR CANNOT BE CREATED.");
//            } else {
//                throw e;
//            }
//        }
//    }
//}
