package com.guido.guzman.msv.oauth;

import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
//    @Bean
//    @LoadBalanced
//    WebClient.Builder webClient() {
//        return WebClient.builder().baseUrl("http://users");
//    }

    @Bean
    WebClient webClient(WebClient.Builder builder,
                        ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        return builder.baseUrl("http://users")
                .filter(lbFunction)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
