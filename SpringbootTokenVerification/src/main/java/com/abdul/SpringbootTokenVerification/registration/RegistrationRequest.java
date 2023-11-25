package com.abdul.SpringbootTokenVerification.registration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class RegistrationRequest {
    private String name;
    private String email;
    private String password;
}
