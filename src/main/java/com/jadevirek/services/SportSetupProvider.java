package com.jadevirek.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jadevirek.entities.Sport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
public class SportSetupProvider {

    private static final Logger logger = LoggerFactory.getLogger(SportSetupProvider.class);

    private final WebClient webClient;
    private final ReactiveMongoOperations reactiveMongoOperations;
    private final ObjectMapper objectMapper;

    @Autowired
    public SportSetupProvider(WebClient webClient, ReactiveMongoOperations reactiveMongoOperations,
            ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.reactiveMongoOperations = reactiveMongoOperations;
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
                .flatMap(reactiveMongoOperations::save)
                .subscribe();
    }

    /**
     * Example of etlProcessBackpressure
     */
    //    @PostConstruct
    public void etlProcessBackpressure() {

        reactiveMongoOperations.findAll(Sport.class)
                .log("category", Level.ALL, SignalType.ON_NEXT, SignalType.ON_ERROR)
                .limitRate(20)
                .delayElements(Duration.ofMillis(1000))
                .doOnNext(sportData -> logger.debug("On next sport data: {}", sportData))
                .map(reactiveMongoOperations::save)
                .publishOn(Schedulers.boundedElastic())
                .map(sportEntity -> {
                    sportEntity.flux()
                            .subscribe(sport -> logger.debug(sport.toString()));
                    return sportEntity;
                })
                .collect(Collectors.toList())
                .block();
    }
}
