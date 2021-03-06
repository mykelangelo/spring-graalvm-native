package com.example.test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.One;

@SpringBootApplication(proxyBeanMethods = false)
public class TestServer {

    private One<String> output = Sinks.one();

    private String response = "";

    public static void main(String[] args) {
        Set<String> list = new LinkedHashSet<>(Arrays.asList(args));
        list.addAll(Arrays.asList("--server.port=8000", "--spring.cloud.function.web.export.enabled=false", "--spring.main.web-application-type=reactive"));
        SpringApplication.run(TestServer.class, list.toArray(new String[0]));
    }

    @Bean
    public Supplier<Mono<String>> home() {
        return () -> output.asMono();
    }

    @Bean
    public Function<String, String> echo() {
        return input -> {
            response = input;
            return "Echo: " + input;
        };
    }

    @Bean
    public Function<String, String> add() {
        return input -> {
            System.err.println("Add: " + input);
            output.emitValue(input);
            output = Sinks.one();
            return "Added: " + input;
        };
    }

    @Bean
    public Supplier<String> take() {
        return () -> response;
    }

}