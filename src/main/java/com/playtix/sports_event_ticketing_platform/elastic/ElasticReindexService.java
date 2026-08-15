package com.playtix.sports_event_ticketing_platform.elastic;

import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;
import com.playtix.sports_event_ticketing_platform.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ElasticReindexService {

    private final TicketRepository ticketRepository;
    private final ElasticTicketSyncService elasticTicketSyncService;

    @Transactional
    public int reindexAllTickets() {
        Iterable<Ticket> all = ticketRepository.findAll();
        int count = 0;
        for (Ticket t : all) {
            elasticTicketSyncService.indexTicket(t);
            count++;
        }
        return count;
    }
}
