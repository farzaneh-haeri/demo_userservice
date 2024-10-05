package com.demo.usermanagementservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDto {

    @EqualsAndHashCode.Include
    private Long id;

    @JsonProperty("full-name")
    @NotBlank(message = "Name should have a value")
    @Size(max = 250, message = "Name is too long")
    private String name;

    @NotBlank
    @Email(message = "Email format is not valid")
    private String email;

}
