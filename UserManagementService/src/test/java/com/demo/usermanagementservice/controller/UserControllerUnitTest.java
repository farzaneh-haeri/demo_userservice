package com.demo.usermanagementservice.controller;

import com.demo.usermanagementservice.ApplicationProperties;
import com.demo.usermanagementservice.EmailServiceApi;
import com.demo.usermanagementservice.controller.UserController;
import com.demo.usermanagementservice.dto.EmailDto;
import com.demo.usermanagementservice.dto.UserDto;
import com.demo.usermanagementservice.dto.UserRegisterDto;
import com.demo.usermanagementservice.dto.UserUpdateDto;
import com.demo.usermanagementservice.exception.UserNotFoundException;
import com.demo.usermanagementservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerUnitTest {
    @Mock
    private UserService userService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private EmailServiceApi emailServiceApi;

    @Mock
    private ApplicationProperties applicationProperties;

    @InjectMocks
    private UserController userController;

    @Test
    public void testGetAllUsers() {
        UserDto user1 = new UserDto(1L, "Alex", "alex@demo.com", null);
        UserDto user2 = new UserDto(2L, "Mary", "mary@demo.com", null);
        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        List<UserDto> result = userController.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("Alex", result.get(0).getName());
        assertEquals("Mary", result.get(1).getName());
    }

    @Test
    public void testGetUser_UserFound() {
        UserDto user = new UserDto(1L, "Alex", "alex@demo.com", null);
        when(userService.getUser(1L)).thenReturn(Optional.of(user));

        ResponseEntity<UserDto> result = userController.getUser(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Alex", Objects.requireNonNull(result.getBody()).getName());
    }

    @Test
    public void testGetUser_UserNotFound() {
        when(userService.getUser(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> userController.getUser(1L));
        assertEquals("User not found with id:1", exception.getMessage());
    }

    @Test
    public void testRegister() {
        UserRegisterDto userRegisterDto = new UserRegisterDto("Alex", "alex@demo.com");
        UserDto addedUser = new UserDto(1L, "Alex", "alex@demo.com", null);
        when(userService.addUser(userRegisterDto)).thenReturn(addedUser);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Welcome!");
        when(applicationProperties.getEmailServiceAuthHeader()).thenReturn("test");

        ResponseEntity<Void> result = userController.register(userRegisterDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(addedUser.getId())
                .toUri();

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(location, result.getHeaders().getLocation());
        verify(emailServiceApi).sendTextEmail(any(EmailDto.class), anyString());
    }

    @Test
    public void testUpdateUser() {
        UserUpdateDto userUpdateDto = new UserUpdateDto(1L, "Alex Updated");

        ResponseEntity<Void> result = userController.updateUser(userUpdateDto);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService).updateUser(userUpdateDto);
    }

    @Test
    public void testUpdateUsers() {
        UserUpdateDto userUpdateDto = new UserUpdateDto(1L, "Alex Updated");

        ResponseEntity<Void> result = userController.updateUsers(Collections.singletonList(userUpdateDto));

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService).updateUsers(anyList());
    }

    @Test
    public void testDeleteUser() {
        ResponseEntity<Void> result = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService).softDeleteUser(1L);
    }

    @Test
    public void testDeleteUsers() {
        ResponseEntity<Void> result = userController.deleteUsers(Collections.singletonList(1L));

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService).softDeleteUsers(anyList());
    }

    @Test
    public void testGetHateoasUser_UserFound() {
        UserDto user = new UserDto(1L, "Alex", "alex@demo.com", null);
        when(userService.getUser(1L)).thenReturn(Optional.of(user));

        var result = userController.getHateoasUser(1L);

        assertEquals("Alex", Objects.requireNonNull(result.getContent()).getName());
        assertNotNull(result.getLinks());
    }

    @Test
    public void testGetHateoasUser_UserNotFound() {
        when(userService.getUser(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> userController.getHateoasUser(1L));
        assertEquals("User not found with id:1", exception.getMessage());
    }
}
