package com.jadevirek.configs;


import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;

@Profile(value = "local")
@Order(1)
@Configuration
@Import(EmbeddedMongoAutoConfiguration.class)
public class MongoDataConfig {

    public static final String DATABASE_NAME = "sports";

    @Bean
    public ReactiveMongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleReactiveMongoDatabaseFactory(mongoClient, DATABASE_NAME);
    }

    @Bean
    public ReactiveMongoOperations reactiveMongoTemplate(ReactiveMongoDatabaseFactory databaseFactory) {
        return new ReactiveMongoTemplate(databaseFactory);
    }
}
