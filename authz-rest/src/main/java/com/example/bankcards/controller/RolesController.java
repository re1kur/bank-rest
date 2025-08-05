package com.example.bankcards.controller;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.role.RoleDto;
import com.example.bankcards.core.dto.role.RolePayload;
import com.example.bankcards.core.dto.role.RoleUpdatePayload;
import com.example.bankcards.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RolesController {
    private final RoleService service;

    @PostMapping
    public ResponseEntity<?> createRole(
            @RequestBody @Valid RolePayload payload
    ) {
        service.create(payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readRole(
            @PathVariable(name = "id") Integer roleId
    ) {
        RoleDto responseBody = service.read(roleId);
        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(
            @PathVariable(name = "id") Integer roleId,
            @RequestBody @Valid RoleUpdatePayload payload
            ) {
        service.update(roleId, payload);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(
            @PathVariable(name = "id") Integer roleId
    ) {
        service.delete(roleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getRoles(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageDto<RoleDto> responseBody = service.readAll(pageable);

        return ResponseEntity.ok(responseBody);
    }
}
