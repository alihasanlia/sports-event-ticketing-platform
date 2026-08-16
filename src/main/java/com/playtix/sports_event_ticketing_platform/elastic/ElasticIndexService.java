package com.playtix.sports_event_ticketing_platform.elastic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticIndexService {

    private final ElasticsearchOperations operations;

    public void ensureTicketIndex() {
        IndexOperations indexOps = operations.indexOps(IndexCoordinates.of("tickets"));
        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping(TicketIndex.class));
        }
    }
}
