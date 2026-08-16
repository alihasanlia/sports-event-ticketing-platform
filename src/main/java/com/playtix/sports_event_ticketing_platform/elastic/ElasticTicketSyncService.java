package com.playtix.sports_event_ticketing_platform.elastic;

import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticTicketSyncService {

    private final ElasticsearchOperations elasticOperations;

    /**
     * Sync SQL ticket data into Elasticsearch index.
     * SQL remains the source of truth.
     */
    public void indexTicket(Ticket ticket) {
        TicketIndex index = new TicketIndex();

        index.setId(ticket.getId().toString());
        index.setSeatNumber(ticket.getSeatNumber());
        index.setPrice(ticket.getFinalPrice().doubleValue());
        index.setStatus(ticket.getStatus().name());

        if (ticket.getMatch() != null) {
            Match match = ticket.getMatch();

            index.setMatchId(match.getId().toString());

            if (match.getSportType() != null) {
                index.setSport(match.getSportType().name());
            }

            if (match.getHomeTeam() != null) {
                index.setTeamA(match.getHomeTeam().getName());
            }

            if (match.getAwayTeam() != null) {
                index.setTeamB(match.getAwayTeam().getName());
            }

            if (match.getStadium() != null) {
                index.setCity(match.getStadium().getCity());
            }
        }

        elasticOperations.save(index);
    }

    public void removeTicket(String ticketId) {
        elasticOperations.delete(ticketId, IndexCoordinates.of("tickets"));
    }
}
