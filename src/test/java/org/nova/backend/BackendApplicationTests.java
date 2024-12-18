package org.nova.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class BackendApplicationTests {
    @Test
    void contextLoads() {
        // Simple test to verify the application context loads successfully
        assertThat(true).isTrue();
    }
}
