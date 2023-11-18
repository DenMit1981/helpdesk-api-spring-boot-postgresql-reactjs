package com.training.denmit.helpdeskApi.converter.impl;

import com.training.denmit.helpdeskApi.converter.UserConverter;
import com.training.denmit.helpdeskApi.dto.user.UserRegisterDto;
import com.training.denmit.helpdeskApi.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserConverterImpl implements UserConverter {

    private final PasswordEncoder passwordEncoder;

    @Override
    public User fromUserRegisterDto(UserRegisterDto userDto) {
        User user = new User();

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());

        return user;
    }
}
