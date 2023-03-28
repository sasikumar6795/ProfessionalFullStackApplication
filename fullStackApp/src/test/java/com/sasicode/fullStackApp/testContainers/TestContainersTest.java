package com.sasicode.fullStackApp.testContainers;

import com.sasicode.fullStackApp.testContainers.TestContainers;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class TestContainersTest extends TestContainers {

    @Test
    void name() {

        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
        //Given

        //when

        //then
    }
}
