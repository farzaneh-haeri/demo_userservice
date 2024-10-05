package com.demo.usermanagementservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {

    @NotBlank
    @Email(message = "Email format is not valid")
    private String emailFrom;

    @NotBlank
    @Email(message = "Email format is not valid")
    private String emailTo;

    @NotBlank
    private String subject;

    @NotBlank
    private String text;
}
