package com.example.bankcards.mapper.impl;

import com.example.bankcards.core.annotation.Mapper;
import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.role.RoleDto;
import com.example.bankcards.core.dto.role.RolePayload;
import com.example.bankcards.core.dto.role.RoleUpdatePayload;
import com.example.bankcards.entity.Role;
import com.example.bankcards.mapper.RoleMapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper
public class RoleMapperImpl implements RoleMapper {
    @Override
    public Role create(RolePayload payload) {
        return Role.builder()
                .name(payload.name())
                .build();
    }

    @Override
    public RoleDto read(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    @Override
    public Role update(Role role, RoleUpdatePayload payload) {
        role.setName(payload.name());

        return role;
    }

    @Override
    public PageDto<RoleDto> readPage(Page<Role> rolesPage) {
        List<RoleDto> content = rolesPage.getContent().stream().map(this::read).toList();

        return new PageDto<>(
                content,
                rolesPage.getNumber(),
                rolesPage.getSize(),
                rolesPage.getTotalPages(),
                rolesPage.hasNext(),
                rolesPage.hasPrevious());
    }
}
