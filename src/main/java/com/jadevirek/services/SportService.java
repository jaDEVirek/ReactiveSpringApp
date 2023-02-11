package com.jadevirek.services;

import com.jadevirek.entities.Sport;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class SportService {

    public Mono<ServerResponse> createSportForName(ServerRequest request) {
        return request.bodyToMono(String.class)
                .flatMap(sportName -> {
                    return Mono.just(sportName)
                            .flatMap(sportSource -> this.createSport(sportName))
                            .flatMap(savedSport -> ServerResponse.created(
                                            URI.create("/api/v1/sport/" + savedSport.getName()))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(savedSport));
                });
    }

    public Mono<ServerResponse> fetchSingleSport(ServerRequest request) {
        return Mono.just(request.pathVariable("sportname"))
                .flatMap(this::getSportByName)
                .flatMap(sport -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(sport))
                .switchIfEmpty(ServerResponse.notFound()
                        .build());
    }

    public Mono<ServerResponse> fetchAllSports(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.findAll(), Sport.class);
    }

    private Mono<Sport> getSportByName(String name) {
        return sportRepository.findByName(name)
                .switchIfEmpty(Mono.error(new SportObjectNotFoundEception(name)));
    }

    private Mono<Sport> createSport(String name) {
        return sportRepository.save(new Sport(name))
                .onErrorMap(Exception.class, e -> new SportForNameExistException(name));
        ;
    }

    private Flux<Sport> findAll() {
        return sportRepository.findAll();
    }
}
