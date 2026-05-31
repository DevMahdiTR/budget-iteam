package com.iteam.buget.core.user;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserDTOService implements Function<User, UserDTO> {
    @Override
    public UserDTO apply(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );
    }
}
