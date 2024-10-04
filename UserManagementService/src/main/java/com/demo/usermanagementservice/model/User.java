package com.demo.usermanagementservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "USER_DETAIL")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Name should have a value")
    @Size(max = 250, message = "Name is too long")
    private String name;

    @Column(name = "email")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE, message = "Email format is not valid")
    private String email;
    @Column(name = "archived")
    private boolean archived;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }


}
