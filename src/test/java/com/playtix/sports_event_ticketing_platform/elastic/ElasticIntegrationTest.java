package com.playtix.sports_event_ticketing_platform.elastic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.ElasticsearchContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class ElasticIntegrationTest {

    static ElasticsearchContainer elastic = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.7.0")
            .withEnv("discovery.type", "single-node");

    static GenericContainer<?> redis = new GenericContainer<>("redis:7").withExposedPorts(6379);

    @Autowired
    private ElasticIndexService indexService;

    @Test
    public void smoke() {
        // This test only starts containers and verifies index creation code path can run.
        elastic.start();
        redis.start();

        indexService.ensureTicketIndex();
        assertTrue(true);

        elastic.stop();
        redis.stop();
    }
}
