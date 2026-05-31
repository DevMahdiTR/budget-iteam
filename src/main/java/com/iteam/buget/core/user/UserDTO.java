package com.iteam.buget.core.user;

import com.iteam.buget.core.role.Role;

public record UserDTO (
        String email,
        String password,
        Role role
) {
}
