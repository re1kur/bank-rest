package com.example.bankcards.service;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.role.RoleDto;
import com.example.bankcards.core.dto.role.RolePayload;
import com.example.bankcards.core.dto.role.RoleUpdatePayload;
import com.example.bankcards.entity.Role;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    Role getByName(String roleName);

    Role getById(Integer role);

    Integer create(RolePayload payload);

    RoleDto read(int id);

    void update(int roleId, RoleUpdatePayload payload);

    void delete(int roleId);

    PageDto<RoleDto> readAll(Pageable pageable);
}
