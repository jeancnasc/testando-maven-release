package io.github.jeancnasc.testando.maven.release;

import io.github.jeancnasc.testando.maven.io.github.jeancnasc.testando.maven.release.HelloWorld;

public class App
{


    public static void main( String[] args )
    {
        System.out.println(new App().mensagem());
    }

    public String mensagem() {
        return new HelloWorld().getMessage();
    }

}
