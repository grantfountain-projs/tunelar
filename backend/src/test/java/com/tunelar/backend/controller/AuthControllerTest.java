package com.tunelar.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

import com.tunelar.backend.TestUtils;
import com.tunelar.backend.config.TestSecurityConfig;
import com.tunelar.backend.dto.LoginDto;
import com.tunelar.backend.dto.RegisterDto;
import com.tunelar.backend.dto.RoleUpdateDto;
import com.tunelar.backend.dto.UsernameUpdateDto;
import com.tunelar.backend.model.User;
import com.tunelar.backend.service.AuthService;

/**
 * Tests the auth controller
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthControllerTest {
    /**
     * Admin password
     */
    @Value("${app.admin-user-password}")
    private String adminUserPassword;
    /**
     * link to auth service
     */
    @Autowired
    private AuthService authService;
    /**
     * link to MockMVC
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Tests if an admin can log in
     *
     * @throws Exception if something goes wrong
     *             
     */
    @Test
    @Transactional
    public void testLoginAdmin() throws Exception {
        final LoginDto loginDto = new LoginDto("admin", adminUserPassword);

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(loginDto)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    /**
     * Tests creating a prod and logging in
     *
     * @throws Exception if something goes wrong
     *             
     */
    @Test
    @Transactional
    public void testCreateProdAndLogin() throws Exception {
        final RegisterDto registerDto = new RegisterDto("Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC");

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(registerDto)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andExpect(content().string("User registered successfully."));

        final LoginDto loginDto = new LoginDto("jestes", "JXB16TBD4LC");

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(loginDto)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ROLE_PROD"));
    }

    /**
     * Tests updating user role to staff and updating username
     *
     * @throws Exception if something goes wrong
     *             
     */
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUserRoleToModAndUpdateUsername() throws Exception {
        // First register a producer user
        final RegisterDto registerDto = new RegisterDto("Test User", "testuser", "testuser@example.com",
                "Password123");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(registerDto)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Login as admin to get token
        final LoginDto loginDto = new LoginDto("admin", adminUserPassword);
        final String adminToken = mvc
                .perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(loginDto)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        // Extract token
        final String accessToken = JsonPath.parse(adminToken).read("$.accessToken");

        // make sure that the username and id are linked normally
        // we are also doing this to get the id of the user
        final User u1 = authService.getUserByUsername("testuser");
        final User u2 = authService.getUserById(u1.getId());
        assertEquals(u1, u2);

        final Long userId = u1.getId();

        // Update role to STAFF
        final RoleUpdateDto roleUpdateDto = new RoleUpdateDto("ROLE_MOD");
        mvc.perform(put("/api/auth/user/role/" + userId).header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON).content(TestUtils.asJsonString(roleUpdateDto))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().string("User role updated successfully."));

        // Update username to john
        final UsernameUpdateDto newUsername = new UsernameUpdateDto("john");
        mvc.perform(put("/api/auth/user/username/" + userId).header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON).content(TestUtils.asJsonString(newUsername))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().string("User username updated successfully."));

        // Verify user now has new username by logging in with new username
        final LoginDto userLoginDto = new LoginDto("john", "Password123");
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(userLoginDto)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.role").value("ROLE_MOD"));
    }

    /**
     * Tests deleting producer
     *
     * @throws Exception if something goes wrong
     *             
     */
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @Transactional
    public void testDeleteProducer() throws Exception {
        final RegisterDto registerDto = new RegisterDto("Lebron James", "goat", "lebron.james@yahoo.edu", "password");

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(registerDto)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andExpect(content().string("User registered successfully."));
        // make sure that the username and id are linked normally
        // we are also doing this to get the id of the user
        final User u1 = authService.getUserByUsername("goat");
        final User u2 = authService.getUserById(u1.getId());
        assertEquals(u1, u2);

        final Long userId = u1.getId();

        mvc.perform(delete("/api/auth/user/" + userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}