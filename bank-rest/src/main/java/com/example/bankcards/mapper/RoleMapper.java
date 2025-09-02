package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.role.RoleDto;
import com.example.bankcards.core.dto.role.RolePayload;
import com.example.bankcards.core.dto.role.RoleUpdatePayload;
import com.example.bankcards.entity.Role;
import org.springframework.data.domain.Page;

public interface RoleMapper {
    Role create(RolePayload payload);

    RoleDto read(Role role);

    Role update(Role role, RoleUpdatePayload payload);

    PageDto<RoleDto> readPage(Page<Role> rolesPage);
}
