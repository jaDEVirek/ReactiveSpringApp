package com.jadevirek.router;

import com.jadevirek.services.SportService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class SportRouter {

    @Bean
    public RouterFunction<ServerResponse> route(SportService sportService) {
        return RouterFunctions.route(RequestPredicates.GET("/api/v1/sports/{sportname}"),
                        sportService::fetchSingleSport)
                .andRoute(RequestPredicates.GET("/api/v1/sports"),
                        sportService::fetchAllSports)
                .andRoute(RequestPredicates.POST("/api/v1/sports/{sportName}"),
                        sportService::createSportForName);
    }
}
