package com.example.bankcards.service.impl;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.role.RoleDto;
import com.example.bankcards.core.dto.role.RolePayload;
import com.example.bankcards.core.dto.role.RoleUpdatePayload;
import com.example.bankcards.core.exception.RoleAlreadyExistsException;
import com.example.bankcards.core.exception.RoleNotFoundException;
import com.example.bankcards.entity.Role;
import com.example.bankcards.mapper.RoleMapper;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repo;
    private final RoleMapper mapper;

    @Override
    public Role get(String roleName) {
        return repo.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role [%s] was not found.".formatted(roleName)));
    }

    @Override
    public Role get(Integer roleId) {
        return repo.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role [%s] was not found.".formatted(roleId)));
    }

    @Override
    public Integer create(RolePayload payload) {
        log.info("CREATE ROLE REQUEST: [{}]", payload.name());

        if (repo.existsByName(payload.name()))
            throw new RoleAlreadyExistsException("Role [%s] already exists.".formatted(payload.name()));

        Role mapped = mapper.create(payload);

        Role saved = repo.save(mapped);
        Integer savedId = saved.getId();

        log.info("ROLE CREATED: [{}]", savedId);
        return savedId;
    }

    @Override
    public RoleDto read(int roleId) {
        return repo.findById(roleId)
                .map(mapper::read)
                .orElseThrow(() -> new RoleNotFoundException("Role [%s] was not found.".formatted(roleId)));
    }

    @Override
    @Transactional
    public void update(int roleId, RoleUpdatePayload payload) {
        log.info("UPDATE ROLE REQUEST: [{}]", roleId);

        Role found = repo.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role [%s] was not found.".formatted(roleId)));

        checkConflicts(found, payload);

        Role mapped = mapper.update(found, payload);

        Role saved = repo.save(mapped);

        log.info("UPDATED ROLE: [{}]", saved.getId());
    }

    private void checkConflicts(Role found, RoleUpdatePayload payload) {
        String name = payload.name();
        if (!found.getName().equals(name))
            if (repo.existsByName(name))
                throw new RoleAlreadyExistsException("Role [%s] already exists.".formatted(name));
    }

    @Override
    @Transactional
    public void delete(int roleId) {
        log.info("DELETE ROLE REQUEST: [{}]", roleId);

        Role found = repo.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role [%s] was not found.".formatted(roleId)));

        repo.delete(found);

        log.info("DELETED ROLE: [{}]", roleId);
    }

    @Override
    public PageDto<RoleDto> readAll(Pageable pageable) {
        return mapper.readPage(repo.findAll(pageable));
    }
}
