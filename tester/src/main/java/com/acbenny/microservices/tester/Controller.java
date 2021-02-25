package com.acbenny.microservices.tester;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
public class Controller {
    
    private final WebClient webClient;

    public Controller(WebClient.Builder builder) {
        webClient = builder.baseUrl("http://devbox:28080/order")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
    }

    @GetMapping("/start")
    public String start() {
        Order ord = new Order();
        ord.setVpnName("voip-1");
        ord.addNE(101);
        for (int i = 0; i < 2 ; i++) {
            System.out.println(i);
            webClient.post().uri("/createRouteAndConfig")
            .body(Mono.just(ord),Order.class)
            .retrieve().bodyToMono(String.class)
            .doOnError(error -> System.out.println("An error has occurred: " + error.getMessage()))
            .subscribe(System.out::println);
        }
        
        return "Done";
    }
}
