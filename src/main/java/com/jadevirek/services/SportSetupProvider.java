package com.jadevirek.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jadevirek.entities.Sport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;

@Service
public class SportSetupProvider {

    private static final Logger logger = LoggerFactory.getLogger(SportSetupProvider.class);

    private final WebClient webClient;
    private final SportRepository sportRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public SportSetupProvider(WebClient webClient, SportRepository sportRepository, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.sportRepository = sportRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void setupSportsResource() {
        webClient.get()
                .uri("https://sports.api.decathlon.com/sports")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .map(s -> s.path("data"))
                .flatMap(Flux::fromIterable)
                .map(node -> {
                    try {
                        return objectMapper.treeToValue(node, Sport.class);
                    } catch (JsonProcessingException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .flatMap(sportRepository::save)
                .subscribe();
    }

}
