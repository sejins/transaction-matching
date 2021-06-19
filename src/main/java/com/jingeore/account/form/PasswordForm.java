package com.jingeore.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordForm {

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;

    @NotBlank
    @Length(min = 8, max = 50)
    private String passwordConfirm;

}
