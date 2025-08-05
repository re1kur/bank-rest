package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.role.RoleDto;
import com.example.bankcards.core.dto.role.RolePayload;
import com.example.bankcards.core.dto.role.RoleUpdatePayload;
import com.example.bankcards.entity.sql.Role;
import com.example.bankcards.mapper.impl.RoleMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RoleMapperTest {
    @InjectMocks
    private RoleMapperImpl roleMapper;

    @Test
    void create_ShouldMapPayloadToRoleEntity() {
        RolePayload payload = new RolePayload("ADMIN");

        Role role = roleMapper.create(payload);

        assertThat(role.getName()).isEqualTo("ADMIN");
    }

    @Test
    void read_ShouldMapRoleEntityToRoleDto() {
        Integer id = 1;
        Role role = Role.builder()
                .id(id)
                .name("USER")
                .build();

        RoleDto dto = roleMapper.read(role);

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.name()).isEqualTo("USER");
    }

    @Test
    void update_ShouldModifyRoleFields() {
        Role role = Role.builder()
                .id(1)
                .name("OLD_NAME")
                .build();

        RoleUpdatePayload payload = new RoleUpdatePayload("NEW_NAME");

        Role updatedRole = roleMapper.update(role, payload);

        assertThat(updatedRole.getName()).isEqualTo("NEW_NAME");
        assertThat(updatedRole).isSameAs(role);
    }

    @Test
    void readPage__ShouldMapListEntitiesToPageDto() {
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        List<RoleDto> expectedDtos = List.of(
                RoleDto.builder().name("ADMIN").build(),
                RoleDto.builder().name("USER").build()
        );

        PageDto<RoleDto> expected = new PageDto<>(expectedDtos, 0, 5, 1, false, false);

        Role role1 = Role.builder().name("ADMIN").build();
        Role role2 = Role.builder().name("USER").build();

        List<Role> roles = List.of(role1, role2);
        Page<Role> pageRoles = new PageImpl<>(roles, pageable, 2);

        PageDto<RoleDto> result = roleMapper.readPage(pageRoles);

        assertEquals(expected, result);
    }
}