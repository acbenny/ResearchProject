package com.acbenny.microservices.tester;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
public class Controller {
    
    private final WebClient webClient;

    public Controller(WebClient.Builder builder) {
        webClient = builder.baseUrl("http://devbox:28080/order")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    }

    public static int getRandom(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    @GetMapping("/start/{loopCnt}")
    public String start(@PathVariable int loopCnt) {
        System.out.println("Started at: " + new Timestamp(new Date().getTime()));
        for (int i = 1; i <= loopCnt; i++) {
            final int cnt = i;
            Order ord = new Order("voip-" + getRandom(new int[] { 1, 2, 3 }), getRandom(new int[] { 101, 102 }));
            System.out.println(cnt);
            webClient.post().uri("/createRouteAndConfig").body(Mono.just(ord), Order.class).exchangeToMono(res -> {
                return res.bodyToMono(String.class);
            }).elapsed().flatMap(tuple -> {
                String time = (new SimpleDateFormat("mm:ss:SSS")).format(new Date(tuple.getT1())).toString();
                return Mono.just(cnt + " : time : " + time + " : " +tuple.getT2());
            })
            .subscribe(System.out::println);
        }
        
        return "Done";
    }
}
