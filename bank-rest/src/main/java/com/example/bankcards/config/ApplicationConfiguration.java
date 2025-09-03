package com.example.bankcards.config;

import com.example.bankcards.repository.cache.RefreshTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableRedisRepositories(basePackageClasses = RefreshTokenRepository.class)
@EnableScheduling
public class ApplicationConfiguration {
}
