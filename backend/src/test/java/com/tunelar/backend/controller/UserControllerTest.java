package com.tunelar.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.tunelar.backend.TestUtils;
import com.tunelar.backend.dto.RegisterDto;
import com.tunelar.backend.model.Role;
import com.tunelar.backend.model.User;
import com.tunelar.backend.service.AuthService;

/**
 * tests the usercontroller class
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {
    /**
     * the admin's password
     */
    @Value("${app.admin-user-password}")
    private String adminUserPassword;
    /**
     * connection to authService
     */
    @Autowired
    private AuthService authService;
    /**
     * connection to mvc
     */
    @Autowired
    private MockMvc mvc;

    /**
     * tests creating a customer and getting it by id
     *
     * @throws Exception if something goes wrong
     *             
     */
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @Transactional
    public void testCreateProducerAndGetProducerById() throws Exception {
        final RegisterDto registerDto = new RegisterDto("Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC");

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(registerDto)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andExpect(content().string("User registered successfully."));
        // make sure that the username and id are linked normally
        // we are also doing this to get the id of the user
        final User u1 = authService.getUserByUsername("jestes");
        final User u2 = authService.getUserById(u1.getId());
        assertEquals(u1, u2);

        mvc.perform(get("/api/user/byId/" + u1.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("jestes"))
                .andExpect(jsonPath("$.name").value("Jordan Estes"))
                .andExpect(jsonPath("$.email").value("vitae.erat@yahoo.edu"));
    }

    /**
     * tests getting user by username
     *
     * @throws Exception if something goes wrong
     *             
     */
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @Transactional
    public void testGetUserByUsername() throws Exception {
        final RegisterDto registerDto = new RegisterDto("Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC");

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(registerDto)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andExpect(content().string("User registered successfully."));

        mvc.perform(get("/api/user/byUsername/jestes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("jestes"))
                .andExpect(jsonPath("$.name").value("Jordan Estes"))
                .andExpect(jsonPath("$.email").value("vitae.erat@yahoo.edu"));
    }

    /**
     * tests getting all users
     *
     * @throws Exception  if something goes wrong
     *            
     */
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @Transactional
    public void testGetAllUsers() throws Exception {
        final RegisterDto registerDto1 = new RegisterDto("Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC");

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(registerDto1)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andExpect(content().string("User registered successfully."));

        final RegisterDto registerDto2 = new RegisterDto("jimmy", "jimmy", "jimmy@yahoo.edu", "JXB16TBD4LC");

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(registerDto2)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andExpect(content().string("User registered successfully."));

        // make sure that the username and id are linked normally
        // we are also doing this to get the id of the user
        final User u1 = authService.getUserByUsername("jestes");
        final User u2 = authService.getUserById(u1.getId());
        assertEquals(u1, u2);

        // make sure that the username and id are linked normally
        // we are also doing this to get the id of the user
        final User u3 = authService.getUserByUsername("jestes");
        final User u4 = authService.getUserById(u3.getId());
        assertEquals(u3, u4);

        // Test admin email is different in test profile
        mvc.perform(get("/api/user/all").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$[" + 1 + "].username").value("jestes"))
                .andExpect(jsonPath("$[" + 1 + "].name").value("Jordan Estes"))
                .andExpect(jsonPath("$[" + 1 + "].email").value("vitae.erat@yahoo.edu"))
                .andExpect(jsonPath("$[" + 2 + "].username").value("jimmy"))
                .andExpect(jsonPath("$[" + 2 + "].name").value("jimmy"))
                .andExpect(jsonPath("$[" + 2 + "].email").value("jimmy@yahoo.edu"))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].name").value("Admin User"))
                .andExpect(jsonPath("$[0].email").value("admin@tunelar.com"));
    }

    /**
     * tests deleting a producer
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

        final Collection<Role> r = u1.getRoles();
        final Object[] roles = r.toArray();
        assertEquals("ROLE_PROD", roles[0].toString());

        mvc.perform(delete("/api/auth/user/" + userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}