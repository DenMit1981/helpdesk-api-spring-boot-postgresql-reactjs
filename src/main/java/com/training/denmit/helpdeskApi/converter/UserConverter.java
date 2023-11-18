package com.training.denmit.helpdeskApi.converter;

import com.training.denmit.helpdeskApi.dto.user.UserRegisterDto;
import com.training.denmit.helpdeskApi.model.User;

public interface UserConverter {

    User fromUserRegisterDto(UserRegisterDto userDto);
}
