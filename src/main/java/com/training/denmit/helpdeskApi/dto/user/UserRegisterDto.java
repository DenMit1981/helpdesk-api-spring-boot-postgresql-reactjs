package com.training.denmit.helpdeskApi.dto.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserRegisterDto {

    private static final String FIELDS_IS_EMPTY = "Please fill out the required field.";
    private static final String WRONG_NAME_SIZE = "First or last name must be between 6 and 30 symbols";
    private static final String WRONG_EMAIL_SIZE = "Email must be between 8 and 40 symbols";
    private static final String INVALID_DATA = "Please make sure you are using a valid email or password";

    @NotBlank(message = FIELDS_IS_EMPTY)
    @Size(max = 30, min = 3, message = WRONG_NAME_SIZE)
    private String firstName;

    @NotBlank(message = FIELDS_IS_EMPTY)
    @Size(max = 30, min = 3, message = WRONG_NAME_SIZE)
    private String lastName;

    @NotBlank(message = FIELDS_IS_EMPTY)
    @Size(max = 40, min = 8, message = WRONG_EMAIL_SIZE)
    @Email(regexp = "^[^@|\\.].+@.+\\..+[^@|\\.]$", message = INVALID_DATA)
    private String email;

    @NotBlank(message = FIELDS_IS_EMPTY)
    @Pattern(regexp = "((?=.*d)(?=.*[\\p{Lu}])(?=.*[\\d])(?=.*[\\p{Ll}])(?=.*[\\p{Punct}]).{6,20})",
            message = INVALID_DATA)
    private String password;
}
