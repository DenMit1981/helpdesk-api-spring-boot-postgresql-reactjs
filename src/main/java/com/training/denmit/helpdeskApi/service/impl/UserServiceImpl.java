package com.training.denmit.helpdeskApi.service.impl;

import com.training.denmit.helpdeskApi.config.security.jwt.JwtTokenProvider;
import com.training.denmit.helpdeskApi.converter.UserConverter;
import com.training.denmit.helpdeskApi.dto.user.UserLoginDto;
import com.training.denmit.helpdeskApi.dto.user.UserRegisterDto;
import com.training.denmit.helpdeskApi.exception.UserIsPresentException;
import com.training.denmit.helpdeskApi.exception.UserNotFoundException;
import com.training.denmit.helpdeskApi.model.User;
import com.training.denmit.helpdeskApi.model.enums.Role;
import com.training.denmit.helpdeskApi.repository.UserRepository;
import com.training.denmit.helpdeskApi.model.CustomUserDetails;
import com.training.denmit.helpdeskApi.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class.getName());

    private static final String USER_IS_PRESENT = "User with login %s is already present";
    private static final String USER_NOT_FOUND = "User with login %s not found";
    private static final String USER_HAS_ANOTHER_PASSWORD = "User with login %s has another password. " +
            "Go to register or enter valid credentials";
    private static final String ENGINEER = "engineer";
    private static final String MANAGER = "manager";

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public User save(UserRegisterDto userDto) {
        checkUserBeforeSave(userDto);

        User user = userConverter.fromUserRegisterDto(userDto);

        user.setRole(getUserRoleByLogin(user.getEmail()));

        userRepository.save(user);

        LOGGER.info("New user : {}", user);

        return user;
    }

    @Override
    @Transactional
    public Map<Object, Object> authenticateUser(UserLoginDto userDto) {
        User user = getByLoginAndPassword(userDto.getLogin(), userDto.getPassword());

        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole());

        Map<Object, Object> response = new HashMap<>();

        response.put("userName", user.getEmail());
        response.put("role", user.getRole());
        response.put("token", token);

        return response;
    }

    @Override
    @Transactional
    public User getByLogin(String login) {
        return userRepository.findByEmail(login)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, login)));
    }

    @Override
    @Transactional
    public User getByLoginAndPassword(String login, String password) {
        User user = getByLogin(login);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {

            LOGGER.info("User : {}", user);

            return user;
        }
        LOGGER.error(String.format(USER_HAS_ANOTHER_PASSWORD, login));

        throw new UserNotFoundException(String.format(USER_HAS_ANOTHER_PASSWORD, login));
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        return new CustomUserDetails(getByLogin(login));
    }

    private boolean isUserPresent(UserRegisterDto userDto) {
        List<User> users = (List<User>) userRepository.findAll();

        return users.stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()));
    }

    private void checkUserBeforeSave(UserRegisterDto userDto) {
        if (isUserPresent(userDto)) {
            LOGGER.error(String.format(USER_IS_PRESENT, userDto.getEmail()));

            throw new UserIsPresentException(String.format(USER_IS_PRESENT, userDto.getEmail()));
        }
    }

    private Role getUserRoleByLogin(String login) {
        if (login.contains(ENGINEER)) {
            return Role.ROLE_ENGINEER;
        }

        if (login.contains(MANAGER)) {
            return Role.ROLE_MANAGER;
        }

        return Role.ROLE_EMPLOYEE;
    }
}
