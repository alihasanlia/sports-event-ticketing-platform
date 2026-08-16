package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.elastic.ElasticReindexService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/elastic")
@RequiredArgsConstructor
public class ElasticAdminController {

    private final ElasticReindexService reindexService;

    @PostMapping("/reindex-tickets")
    public ResponseEntity<String> reindex() {
        int c = reindexService.reindexAllTickets();
        return ResponseEntity.ok("Reindexed " + c + " tickets");
    }
}
