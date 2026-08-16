package com.mayday;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestVisionConfig.class)
class MaydayApplicationTests {

    @Test
    void contextLoads() {
    }

}
