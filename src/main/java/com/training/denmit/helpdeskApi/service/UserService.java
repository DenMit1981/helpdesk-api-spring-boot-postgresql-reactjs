package com.training.denmit.helpdeskApi.service;

import com.training.denmit.helpdeskApi.dto.user.UserLoginDto;
import com.training.denmit.helpdeskApi.dto.user.UserRegisterDto;
import com.training.denmit.helpdeskApi.model.User;

import java.util.Map;

public interface UserService {

    User save(UserRegisterDto userDto);

    Map<Object, Object> authenticateUser(UserLoginDto userDto);

    User getByLogin(String login);

    User getByLoginAndPassword(String login, String password);
}
