package com.playtix.sports_event_ticketing_platform.elastic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.bool;
import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.match;

@Service
@RequiredArgsConstructor
public class ElasticTicketSearchService {

    private final ElasticsearchOperations operations;

    /**
     * Search directly against Elasticsearch instead of SQL.
     * This satisfies the assignment requirement that only search APIs use Elasticsearch.
     */
    public List<TicketIndex> search(String sport, String city, String team, String status) {

        NativeQuery query = NativeQuery.builder()
                .withQuery(bool(b -> {

                    if (sport != null && !sport.isBlank()) {
                        b.must(match(m -> m.field("sport").query(sport)));
                    }

                    if (city != null && !city.isBlank()) {
                        b.must(match(m -> m.field("city").query(city)));
                    }

                    if (team != null && !team.isBlank()) {
                        b.must(match(m -> m.field("teamA").query(team)));
                    }

                    if (status != null && !status.isBlank()) {
                        b.must(match(m -> m.field("status").query(status)));
                    }

                    return b;
                }))
                .build();

        SearchHits<TicketIndex> hits = operations.search(query, TicketIndex.class);

        return hits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
