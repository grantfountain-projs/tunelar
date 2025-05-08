package com.tunelar.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "app.jwt-secret=test_jwt_secret_key_for_testing_purposes",
    "app.jwt-expiration-milliseconds=86400000",
    "app.admin-user-password=admin123"
})
class BackendApplicationTests {

    @Test
    void contextLoads() {
        // This test will just check if the application context can be loaded
    }
}