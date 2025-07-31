package com.example.bankcards.service.impl;

import com.example.bankcards.core.exception.RoleNotFoundException;
import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repo;

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
}
