package com.example.bankcards.service;

import com.example.bankcards.entity.Role;

public interface RoleService {
    Role get(String roleName);

    Role get(Integer role);
}
