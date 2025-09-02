package com.example.bankcards.service.impl;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.dto.user.UserUpdatePayload;
import com.example.bankcards.core.exception.UserAlreadyExistsException;
import com.example.bankcards.core.exception.UserNotFoundException;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UUID create(UserPayload payload) {
        String username = payload.username();
        log.info("CREATE USER REQUEST: [{}]", username);

        if (repo.existsByUsername(username))
            throw new UserAlreadyExistsException("User [%s] already exists.".formatted(username));

        User mapped = mapper.create(payload);

        User saved = repo.save(mapped);

        UUID id = saved.getId();
        log.info("USER CREATED: [{}]", id);
        return id;
    }

    @Override
    public UserDto read(UUID userId) {
        return repo.findById(userId)
                .map(mapper::read)
                .orElseThrow(() -> new UserNotFoundException("User [%s] was not found.".formatted(userId)));
    }

    @Override
    @Transactional
    public void update(UUID userId, UserUpdatePayload payload) {
        log.info("UPDATE USER REQUEST: [{}]", userId);

        User found = repo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User [%s] was not found.".formatted(userId)));

        checkConflict(found, payload);

        User mapped = mapper.update(found, payload);

        repo.save(mapped);

        log.info("USER UPDATED: [{}]", payload);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        log.info("DELETE USER REQUEST: [{}]", userId);

        User found = repo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User [%s] was not found.".formatted(userId)));

        repo.delete(found);

        log.info("USER DELETED: [{}]", userId);
    }

    @Override
    public User get(UUID userId) {
        return repo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User [%s] was not found.".formatted(userId)));
    }

    @Override
    public PageDto<UserDto> readAll(Pageable pageable) {
        return mapper.readPage(repo.findAll(pageable));
    }

    private void checkConflict(User found, UserUpdatePayload payload) {
        String username = payload.username();

        if (!found.getUsername().equals(username)) {
            if (repo.existsByUsername(username))
                throw new UserAlreadyExistsException("User [%s] already exists.".formatted(username));
        }
    }
}
