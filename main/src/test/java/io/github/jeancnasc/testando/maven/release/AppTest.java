package io.github.jeancnasc.testando.maven.release;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest
{


    @Test
    public void test(){
        assertThat(new App().mensagem()).isEqualTo("Hello World!");
    }
}
