package org.nova.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class BackendApplicationTests {
    @Test
    void simpleSuccessTest() {
        // This test will always pass
        System.out.println("Test passed!");
        assertThat(true);
    }
}
