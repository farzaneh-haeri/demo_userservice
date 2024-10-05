package com.demo.usermanagementservice.controller;

import com.demo.usermanagementservice.ApplicationProperties;
import com.demo.usermanagementservice.EmailServiceApi;
import com.demo.usermanagementservice.dto.EmailDto;
import com.demo.usermanagementservice.dto.UserDto;
import com.demo.usermanagementservice.dto.UserRegisterDto;
import com.demo.usermanagementservice.dto.UserUpdateDto;
import com.demo.usermanagementservice.exception.UserNotFoundException;
import com.demo.usermanagementservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Register new user, edit, read or delete selected user(s).")
public class UserController {
    private final UserService userService;
    private final MessageSource messageSource;
    private final ApplicationProperties applicationProperties;
    private final EmailServiceApi emailServiceApi;

    @GetMapping
    @Operation(summary = "Get all users' details")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401", description = "Invalid API Credentials", content = @Content)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user's details specified by id")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401", description = "Invalid API Credentials", content = @Content)
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    public UserDto getUser(@NotNull @PathVariable("id") Long id) {
        return findUserById(id);
    }

    @PostMapping
    @Operation(summary = "Get users' details specified by ids")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401", description = "Invalid API Credentials", content = @Content)
    @ApiResponse(responseCode = "400", description = "At least one id should be provided", content = @Content)
    @ApiResponse(responseCode = "404", description = "There is at least one provided id for which user was not found", content = @Content)
    public List<UserDto> getUsers(@NotNull @RequestBody List<Long> ids) {
        return userService.getUsers(ids);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user and send a welcome email")
    @ApiResponse(responseCode = "201", description = "New user is created")
    @ApiResponse(responseCode = "401", description = "Invalid API Credentials", content = @Content)
    public ResponseEntity<Void> register(@Valid @RequestBody UserRegisterDto user) {

        final UserDto addedUser = userService.addUser(user);
        sendEmail(addedUser);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(addedUser.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/update/single")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update single user")
    @ApiResponse(responseCode = "204", description = "Specified user updated")
    @ApiResponse(responseCode = "401", description = "Invalid API Credentials", content = @Content)
    public void updateUser(@Valid @RequestBody UserUpdateDto user) {
        userService.updateUser(user);
    }

    @PatchMapping("/update/multiple")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update single or multiple users specified by id(s)")
    @ApiResponse(responseCode = "204", description = "Specified user(s) updated")
    @ApiResponse(responseCode = "400", description = "Invalid Users", content = @Content)
    @ApiResponse(responseCode = "401", description = "Invalid API Credentials", content = @Content)
    public void updateUsers(@NotNull @RequestBody List<UserUpdateDto> users) {
        userService.updateUsers(users);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete single user specified by id")
    @ApiResponse(responseCode = "204", description = "Selected user is deleted")
    @ApiResponse(responseCode = "401", description = "Invalid API Credentials", content = @Content)
    public void deleteUser(@NotNull @PathVariable("id") Long id) {
        userService.softDeleteUser(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete single or multiple users specified by id(s)")
    @ApiResponse(responseCode = "204", description = "Selected user(s) are deleted")
    @ApiResponse(responseCode = "400", description = "Invalid User Ids", content = @Content)
    @ApiResponse(responseCode = "401", description = "Invalid API Credentials", content = @Content)
    public void deleteUsers(@NotNull @RequestBody List<Long> ids) {
        userService.softDeleteUsers(ids);
    }

    private UserDto findUserById(Long id) {
        final Optional<UserDto> user = userService.getUser(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with id:" + id);
        }

        return user.get();
    }

    private void sendEmail(UserDto user) {
        final Locale locale = LocaleContextHolder.getLocale();
        final EmailDto emailDto = new EmailDto(applicationProperties.getEmailFrom(),
                user.getEmail(),
                messageSource.getMessage("email.welcome.subject", null, locale),
                messageSource.getMessage("email.welcome.message", new String[]{user.getName()}, locale));
        emailServiceApi.sendTextEmail(emailDto, applicationProperties.getEmailServiceAuthHeader());
    }

}
