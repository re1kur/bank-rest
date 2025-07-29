package com.example.bankcards.service.impl;

import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.exception.UserAlreadyExistsException;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final UserMapper mapper;

    @Override
    public void create(UserPayload payload) {
        String username = payload.username();
        log.info("CREATE USER REQUEST: [{}]", username);

        if (repo.existsByUsername(username))
            throw new UserAlreadyExistsException("User [%s] already exists.".formatted(username));

        User mapped = mapper.create(payload);

        User saved = repo.save(mapped);

        log.info("USER CREATED: [{}]", saved.getId());
    }
}
