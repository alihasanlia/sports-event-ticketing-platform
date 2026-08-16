package com.playtix.sports_event_ticketing_platform.elastic;

import lombok.Data;

@Data
public class TicketIndex {
    private String id;
    private String matchId;
    private String seatNumber;
    private String rowNumber;
    private String sectionNumber;
    private String ticketCategoryId;
    private String sport;
    private String city;
    private String teamA;
    private String teamB;
    private double price;
    private String status;
}
