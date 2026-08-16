package com.playtix.sports_event_ticketing_platform.elastic;

import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;
import com.playtix.sports_event_ticketing_platform.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ElasticReindexService {

    private final TicketRepository ticketRepository;
    private final ElasticTicketSyncService elasticTicketSyncService;
    private final ElasticIndexService elasticIndexService;

    @Transactional
    public int reindexAllTickets() {

        elasticIndexService.ensureTicketIndex();

        List<Ticket> tickets = ticketRepository.findAll();

        for (Ticket ticket : tickets) {
            elasticTicketSyncService.indexTicket(ticket);
        }

        return tickets.size();
    }
}
