package com.demo.usermanagementservice.controller;

import com.demo.usermanagementservice.EmailServiceApi;
import com.demo.usermanagementservice.dto.UserDto;
import com.demo.usermanagementservice.dto.UserRegisterDto;
import com.demo.usermanagementservice.dto.UserUpdateDto;
import com.demo.usermanagementservice.service.UserService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private EmailServiceApi emailServiceApi;

    @Value("${user-management-service.user-name}")
    private String username;

    @Value("${user-management-service.password}")
    private String password;

    @Test
    public void testGetAllUsers() throws Exception {
        UserDto user1 = new UserDto(1L, "Alex", "alex@demo.com");
        UserDto user2 = new UserDto(2L, "Mary", "mary@demo.com");

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/v1/users").with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].full-name").value("Alex"))
                .andExpect(jsonPath("$[1].full-name").value("Mary"));
    }

    @Test
    public void testGetUser() throws Exception {
        UserDto user = new UserDto(1L, "Alex", "alex@demo.com");

        when(userService.getUser(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users/{id}", 1L).with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.full-name").value("Alex"));
    }

    @Test
    public void testGetUser_UserNotFound() throws Exception {
        when(userService.getUser(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/users/{id}", 1L).with(httpBasic(username, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRegister() throws Exception {
        UserDto addedUser = new UserDto(1L, "Alex", "alex@demo.com");

        when(userService.addUser(any(UserRegisterDto.class))).thenReturn(addedUser);

        mockMvc.perform(post("/api/v1/users/register")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"full-name\": \"Alex\", \"email\": \"alex@demo.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/api/v1/users/register/1"));
    }

    @Test
    public void testUpdateUser() throws Exception {

        mockMvc.perform(patch("/api/v1/users/update/single")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"full-name\": \"Alex Updated\"}"))
                .andExpect(status().isNoContent());

        verify(userService).updateUser(any(UserUpdateDto.class));
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", 1L).with(httpBasic(username, password)))
                .andExpect(status().isNoContent());

        verify(userService).softDeleteUser(1L);
    }

    @Test
    public void testDeleteUsers() throws Exception {
        mockMvc.perform(delete("/api/v1/users")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2]"))
                        .andExpect(status().isNoContent());

        verify(userService).softDeleteUsers(anyList());
    }

}
