package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.mapper.TicketMapper;
import com.playtix.sports_event_ticketing_platform.service.redis.RedisCacheService;
import com.playtix.sports_event_ticketing_platform.config.RedisCacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketSearchService {

    private final TicketService ticketService;
    private final RedisCacheService redisCacheService;
    private final RedisCacheProperties redisCacheProperties;
    private final TicketMapper ticketMapper;

    /**
     * Search tickets with simple key composition and cache-aside pattern.
     */
    public List<?> searchTickets(UUID matchId, UUID categoryId, String sport, String city, String team, String priceRange) {
        String searchKey = buildSearchKey(matchId, categoryId, sport, city, team, priceRange);
        String cacheKey = "ticket-search:" + searchKey;

        return redisCacheService.get(cacheKey)
                .map(obj -> (List<?>) obj)
                .orElseGet(() -> {
                    // currently delegate to TicketService; adapt as you add filtering
                    List<?> results = ticketService.getTicketsByMatch(matchId);

                    redisCacheService.put(cacheKey, results, Duration.ofSeconds(redisCacheProperties.getSearchCacheTtl()));
                    return results;
                });
    }

    private String buildSearchKey(UUID matchId, UUID categoryId, String sport, String city, String team, String priceRange) {
        StringJoiner sj = new StringJoiner("|");
        sj.add(matchId == null ? "-" : matchId.toString());
        sj.add(categoryId == null ? "-" : categoryId.toString());
        sj.add(sport == null ? "-" : sport);
        sj.add(city == null ? "-" : city);
        sj.add(team == null ? "-" : team);
        sj.add(priceRange == null ? "-" : priceRange);
        return sj.toString();
    }
}
