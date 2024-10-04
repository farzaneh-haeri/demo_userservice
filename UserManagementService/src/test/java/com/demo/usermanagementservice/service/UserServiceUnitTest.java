package com.demo.usermanagementservice.service;



import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.demo.usermanagementservice.dto.UserDto;
import com.demo.usermanagementservice.dto.UserRegisterDto;
import com.demo.usermanagementservice.dto.UserUpdateDto;
import com.demo.usermanagementservice.exception.BadRequestException;
import com.demo.usermanagementservice.exception.DuplicatedEmailException;
import com.demo.usermanagementservice.exception.UserNotFoundException;
import com.demo.usermanagementservice.model.User;
import com.demo.usermanagementservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetAllUsers() {
        List<User> users = Arrays.asList(new User(1L, "Alex", "alex@demo.com", false),
                new User(2L, "Mary", "mary@demo.com", false));
        when(userRepository.findUsersByArchivedFalse()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("Alex", result.get(0).getName());
    }

    @Test
    public void testGetUser() {
        User user = new User(1L, "Alex", "alex@demo.com", false);
        when(userRepository.findUserByIdAndArchivedFalse(1L)).thenReturn(Optional.of(user));

        Optional<UserDto> result = userService.getUser(1L);

        assertTrue(result.isPresent());
        assertEquals("Alex", result.get().getName());
    }

    @Test
    public void testGetUser_UserNotFound() {
        when(userRepository.findUserByIdAndArchivedFalse(1L)).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.getUser(1L);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetUsers_WithValidIds() {
        List<Long> ids = Arrays.asList(1L, 2L);
        User user1 = new User(1L, "Alex", "alex@demo.com", false);
        User user2 = new User(2L, "Mary", "mary@demo.com", false);
        when(userRepository.findUsersByIdInAndArchivedFalse(ids)).thenReturn(Arrays.asList(user1, user2));

        List<UserDto> result = userService.getUsers(ids);

        assertEquals(2, result.size());
        assertEquals("Alex", result.get(0).getName());
        assertEquals("Mary", result.get(1).getName());
    }

    @Test
    public void testGetUsers_SomeIdsNotFound() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        User user1 = new User(1L, "Alex", "alex@demo.com", false);
        User user2 = new User(2L, "Mary", "mary@demo.com", false);

        when(userRepository.findUsersByIdInAndArchivedFalse(ids)).thenReturn(Arrays.asList(user1, user2));

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.getUsers(ids));
        assertEquals("1 users were not found", exception.getMessage());
    }

    @Test
    public void testGetUsers_EmptyIds() {
        Exception exception = assertThrows(BadRequestException.class, () -> userService.getUsers(Collections.emptyList()));
        assertEquals("At least one id should be provided", exception.getMessage());
    }

    @Test
    public void testAddUser() {
        UserRegisterDto userRegisterDto = new UserRegisterDto("Alex", "alex@demo.com");
        User newUser = new User("Alex", "alex@demo.com");
        when(userRepository.findUserByEmailEqualsIgnoreCase("alex@demo.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserDto result = userService.addUser(userRegisterDto);

        assertEquals("Alex", result.getName());
    }

    @Test
    public void testAddUser_DuplicatedEmail() {
        UserRegisterDto userRegisterDto = new UserRegisterDto("Alex", "alex@demo.com");
        User existingUser = new User("Alex", "alex@demo.com");
        when(userRepository.findUserByEmailEqualsIgnoreCase("alex@demo.com")).thenReturn(existingUser);

        Exception exception = assertThrows(DuplicatedEmailException.class, () -> userService.addUser(userRegisterDto));
        assertEquals("User with provided email already exists", exception.getMessage());
    }

    @Test
    public void testUpdateUser() {
        UserUpdateDto userUpdateDto = new UserUpdateDto(1L, "Alex Updated");
        User user = new User(1L, "Alex", "alex@example.com", false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUser(userUpdateDto);

        assertEquals("Alex Updated", user.getName());
        verify(userRepository).save(user);
    }


    @Test
    public void testUpdateUsers() {
        List<UserUpdateDto> userUpdates =
                Arrays.asList(new UserUpdateDto(1L, "Alex Updated"), new UserUpdateDto(2L, "Mary Updated"));
        User user1 = new User(1L, "Alex", "alex@demo.com", false);
        User user2 = new User(2L, "Mary", "mary@demo.com", false);
        when(userRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(user1, user2));

        userService.updateUsers(userUpdates);

        assertEquals("Alex Updated", user1.getName());
        assertEquals("Mary Updated", user2.getName());
        verify(userRepository).saveAll(Arrays.asList(user1, user2));
    }

    @Test
    public void testUpdateUsers_EmptyList() {
        Exception exception = assertThrows(BadRequestException.class, () -> userService.updateUsers(Collections.emptyList()));
        assertEquals("At least one user must be provided", exception.getMessage());
    }

    @Test
    public void testUpdateUsers_SomeUsersNotFound() {
        List<UserUpdateDto> userUpdates = Arrays.asList(new UserUpdateDto(1L, "Alex Updated"),
                new UserUpdateDto(3L, "Vikas Updated"));
        User user1 = new User(1L, "Alex", "alex@demo.com", false);
        when(userRepository.findAllById(Arrays.asList(1L, 3L))).thenReturn(Collections.singletonList(user1));

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.updateUsers(userUpdates));
        assertEquals("1 users were not found", exception.getMessage());
    }

    @Test
    public void testSoftDeleteUser() {
        User user = new User(1L, "Alex", "alex@example.com", false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.softDeleteUser(1L);

        assertTrue(user.isArchived());
        verify(userRepository).save(user);
    }

    @Test
    public void testSoftDeleteUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.softDeleteUser(1L));
        assertEquals("User was not found", exception.getMessage());
    }

    @Test
    public void testSoftDeleteUsers() {
        List<Long> userIds = Arrays.asList(1L, 2L);
        User user1 = new User(1L, "Alex", "alex@demo.com", false);
        User user2 = new User(2L, "Mary", "mary@demo.com", false);
        when(userRepository.findAllById(userIds)).thenReturn(Arrays.asList(user1, user2));

        userService.softDeleteUsers(userIds);

        assertTrue(user1.isArchived());
        assertTrue(user2.isArchived());
        verify(userRepository).saveAll(Arrays.asList(user1, user2));
    }

    @Test
    public void testSoftDeleteUsers_EmptyList() {
        Exception exception = assertThrows(BadRequestException.class, () -> userService.softDeleteUsers(Collections.emptyList()));
        assertEquals("At least one user id must be provided", exception.getMessage());
    }

    @Test
    public void testSoftDeleteUsers_SomeUsersNotFound() {
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        User user1 = new User(1L, "Alex", "alex@demo.com", false);
        User user2 = new User(2L, "Mary", "mary@demo.com", false);
        when(userRepository.findAllById(userIds)).thenReturn(Arrays.asList(user1, user2));

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.softDeleteUsers(userIds));
        assertEquals("1 users were not found", exception.getMessage());
    }

    @Test
    public void testSoftDeleteUser_NullId() {
        Exception exception = assertThrows(BadRequestException.class, () -> userService.softDeleteUser(null));
        assertEquals("User id must be provided", exception.getMessage());
    }
}

