package com.demo.usermanagementservice.service;


import com.demo.usermanagementservice.dto.UserDto;
import com.demo.usermanagementservice.dto.UserRegisterDto;
import com.demo.usermanagementservice.dto.UserUpdateDto;
import com.demo.usermanagementservice.exception.BadRequestException;
import com.demo.usermanagementservice.exception.DuplicatedEmailException;
import com.demo.usermanagementservice.exception.UserNotFoundException;
import com.demo.usermanagementservice.model.User;
import com.demo.usermanagementservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findUsersByArchivedFalse().stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<UserDto> getUser(Long id) {
        final Optional<User> user = userRepository.findUserByIdAndArchivedFalse(id);
        return user.map(this::toDto);
    }

    public List<UserDto> getUsers(List<Long> ids) {

        if (CollectionUtils.isEmpty(ids)) {
            throw new BadRequestException("At least one id should be provided");
        }

        final List<User> users = userRepository.findUsersByIdInAndArchivedFalse(ids);
        if (users.size() != ids.size()) {
            throw new UserNotFoundException(ids.size() - users.size() + " users were not found");
        }

        return users.stream().map(this::toDto).collect(Collectors.toList());
    }

    public UserDto addUser(UserRegisterDto user) {

        if (userRepository.findUserByEmailEqualsIgnoreCaseAndArchivedFalse(user.getEmail()) != null) {
            throw new DuplicatedEmailException("User with provided email already exists");
        }

        final User newUser = toEntity(user);
        newUser.setArchived(false);
        return toDto(userRepository.save(newUser));
    }

    public void updateUser(UserUpdateDto user) {

        final Optional<User> userToUpdate = userRepository.findById(user.getId());
        if (userToUpdate.isEmpty()) {
            throw new UserNotFoundException("User was not found");
        }

        userToUpdate.get().setName(user.getName());
        userRepository.save(userToUpdate.get());
    }

    public void updateUsers(List<UserUpdateDto> users) {

        if (CollectionUtils.isEmpty(users)) {
            throw new BadRequestException("At least one user must be provided");
        }

        final List<User> usersToUpdate = userRepository.findAllById(users.stream().map(UserUpdateDto::getId).collect(Collectors.toList()));
        if (usersToUpdate.size() != users.size()) {
            throw new UserNotFoundException(users.size() - usersToUpdate.size() + " users were not found");
        }

        usersToUpdate.forEach(user -> {
            final Optional<UserUpdateDto> userDto = users.stream().filter(u -> u.getId().equals(user.getId())).findFirst();
            userDto.ifPresent(userUpdateDto -> user.setName(userUpdateDto.getName()));
        });

        userRepository.saveAll(usersToUpdate);
    }

    public void softDeleteUser(Long id) {

        if (id == null) {
            throw new BadRequestException("User id must be provided");
        }

        final Optional<User> userToDelete = userRepository.findById(id);
        if (userToDelete.isEmpty()) {
            throw new UserNotFoundException("User was not found");
        }

        userToDelete.get().setArchived(true);
        userRepository.save(userToDelete.get());
    }

    public void softDeleteUsers(List<Long> userIds) {

        if (CollectionUtils.isEmpty(userIds)) {
            throw new BadRequestException("At least one user id must be provided");
        }

        final List<User> usersToDelete = userRepository.findAllById(userIds);
        if (usersToDelete.size() != userIds.size()) {
            throw new UserNotFoundException(userIds.size() - usersToDelete.size() + " users were not found");
        }

        usersToDelete.forEach(user -> user.setArchived(true));
        userRepository.saveAll(usersToDelete);
    }

    private UserDto toDto(User entity) {
        return new UserDto(
                entity.getId(),
                entity.getName(),
                entity.getEmail());
    }

    private User toEntity(UserRegisterDto dto) {
        return new User(
                dto.getName(),
                dto.getEmail());
    }

}
