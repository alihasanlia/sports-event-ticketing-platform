package com.playtix.sports_event_ticketing_platform.elastic;

import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticTicketSyncService {

    private final ElasticsearchOperations operations;

    public void indexTicket(Ticket ticket) {
        TicketIndex index = new TicketIndex();

        index.setId(ticket.getId().toString());
        index.setSeatNumber(ticket.getSeatNumber());
        index.setRowNumber(ticket.getRowNumber());
        index.setSectionNumber(ticket.getSectionNumber());
        index.setStatus(ticket.getStatus().name());

        if (ticket.getPrice() != null) {
            index.setPrice(ticket.getPrice().doubleValue());
        }

        if (ticket.getMatch() != null) {
            index.setMatchId(ticket.getMatch().getId().toString());
        }

        if (ticket.getTicketCategory() != null) {
            index.setTicketCategoryId(ticket.getTicketCategory().getId().toString());
        }

        operations.save(index, IndexCoordinates.of("tickets"));
    }

    public void removeTicket(String ticketId) {
        operations.delete(ticketId, IndexCoordinates.of("tickets"));
    }
}
