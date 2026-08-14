package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketCategory;
import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;
import com.playtix.sports_event_ticketing_platform.domain.entity.details.BaseDetails;
import com.playtix.sports_event_ticketing_platform.domain.entity.details.BasketballDetails;
import com.playtix.sports_event_ticketing_platform.domain.entity.details.FootballDetails;
import com.playtix.sports_event_ticketing_platform.domain.entity.details.VolleyballDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TicketRepository {

    private final JdbcTemplate jdbcTemplate;

    public TicketRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SELECT_COLUMNS = "SELECT id, seat_number, row_number, section_number, price, discount_amount, final_price, status, purchase_date, barcode, qr_code, entry_code, ticket_category_id, match_id, base_details_id FROM tickets";

    private final RowMapper<Ticket> ticketRowMapper = (rs, rowNum) -> {
        Ticket ticket = new Ticket();
        ticket.setId((UUID) rs.getObject("id"));
        ticket.setSeatNumber(rs.getString("seat_number"));
        ticket.setRowNumber(rs.getString("row_number"));
        ticket.setSectionNumber(rs.getString("section_number"));
        ticket.setPrice(rs.getBigDecimal("price"));
        ticket.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        ticket.setFinalPrice(rs.getBigDecimal("final_price"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            ticket.setStatus(TicketStatus.valueOf(statusStr));
        }

        Timestamp purchaseDateTs = rs.getTimestamp("purchase_date");
        if (purchaseDateTs != null) {
            ticket.setPurchaseDate(purchaseDateTs.toLocalDateTime());
        }

        ticket.setBarcode(rs.getString("barcode"));
        ticket.setQrCode(rs.getString("qr_code"));
        ticket.setEntryCode(rs.getString("entry_code"));

        UUID categoryId = (UUID) rs.getObject("ticket_category_id");
        if (categoryId != null) {
            TicketCategory tc = new TicketCategory();
            tc.setId(categoryId);
            ticket.setTicketCategory(tc);
        }

        UUID matchId = (UUID) rs.getObject("match_id");
        if (matchId != null) {
            Match match = new Match();
            match.setId(matchId);
            ticket.setMatch(match);
        }

        UUID baseDetailsId = (UUID) rs.getObject("base_details_id");
        if (baseDetailsId != null) {
            // BaseDetails is abstract, so instantiate a concrete subclass (defaulting to FootballDetails if type is unknown)
            FootballDetails bd = new FootballDetails();
            bd.setId(baseDetailsId);
            try {
                bd.setTournamentName(rs.getString("tournament_name"));
                bd.setLeagueName(rs.getString("league_name"));
                bd.setFacilities(rs.getString("facilities"));
                bd.setStadiumName(rs.getString("stadium_name"));
            } catch (Exception e) {
                // Columns might not be present in standard SELECT_COLUMNS
            }
            ticket.setBaseDetails(bd);
        }

        return ticket;
    };

    public Optional<Ticket> findById(UUID id) {
        String sql = SELECT_COLUMNS + " WHERE id = ?";
        List<Ticket> results = jdbcTemplate.query(sql, ticketRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM tickets WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(Ticket ticket) {
        if (ticket != null && ticket.getId() != null) {
            deleteById(ticket.getId());
        }
    }

    public Ticket save(Ticket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(UUID.randomUUID());
            String sql = "INSERT INTO tickets (id, seat_number, row_number, section_number, price, discount_amount, final_price, status, purchase_date, barcode, qr_code, entry_code, ticket_category_id, match_id, base_details_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                ticket.getId(),
                ticket.getSeatNumber(),
                ticket.getRowNumber(),
                ticket.getSectionNumber(),
                ticket.getPrice(),
                ticket.getDiscountAmount(),
                ticket.getFinalPrice(),
                ticket.getStatus() != null ? ticket.getStatus().name() : null,
                ticket.getPurchaseDate(),
                ticket.getBarcode(),
                ticket.getQrCode(),
                ticket.getEntryCode(),
                ticket.getTicketCategory() != null ? ticket.getTicketCategory().getId() : null,
                ticket.getMatch() != null ? ticket.getMatch().getId() : null,
                ticket.getBaseDetails() != null ? ticket.getBaseDetails().getId() : null
            );
        } else {
            String sql = "UPDATE tickets SET seat_number = ?, row_number = ?, section_number = ?, price = ?, discount_amount = ?, final_price = ?, status = ?, purchase_date = ?, barcode = ?, qr_code = ?, entry_code = ?, ticket_category_id = ?, match_id = ?, base_details_id = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                ticket.getSeatNumber(),
                ticket.getRowNumber(),
                ticket.getSectionNumber(),
                ticket.getPrice(),
                ticket.getDiscountAmount(),
                ticket.getFinalPrice(),
                ticket.getStatus() != null ? ticket.getStatus().name() : null,
                ticket.getPurchaseDate(),
                ticket.getBarcode(),
                ticket.getQrCode(),
                ticket.getEntryCode(),
                ticket.getTicketCategory() != null ? ticket.getTicketCategory().getId() : null,
                ticket.getMatch() != null ? ticket.getMatch().getId() : null,
                ticket.getBaseDetails() != null ? ticket.getBaseDetails().getId() : null,
                ticket.getId()
            );
        }
        return ticket;
    }

    public List<Ticket> findAll() {
        return jdbcTemplate.query(SELECT_COLUMNS, ticketRowMapper);
    }

    public List<Ticket> findByStatus(TicketStatus status) {
        String sql = SELECT_COLUMNS + " WHERE status = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, status != null ? status.name() : null);
    }

    public List<Ticket> findByMatch_Id(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, matchId);
    }

    public List<Ticket> findByMatch_IdAndStatus(UUID matchId, TicketStatus status) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND status = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, matchId, status != null ? status.name() : null);
    }

    public List<Ticket> findByTicketCategory_Id(UUID categoryId) {
        String sql = SELECT_COLUMNS + " WHERE ticket_category_id = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, categoryId);
    }

    public List<Ticket> findByTicketCategory_IdAndStatus(UUID categoryId, TicketStatus status) {
        String sql = SELECT_COLUMNS + " WHERE ticket_category_id = ? AND status = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, categoryId, status != null ? status.name() : null);
    }

    public Optional<Ticket> findByBarcode(String barcode) {
        String sql = SELECT_COLUMNS + " WHERE barcode = ?";
        List<Ticket> results = jdbcTemplate.query(sql, ticketRowMapper, barcode);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<Ticket> findByQrCode(String qrCode) {
        String sql = SELECT_COLUMNS + " WHERE qr_code = ?";
        List<Ticket> results = jdbcTemplate.query(sql, ticketRowMapper, qrCode);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<Ticket> findByEntryCode(String entryCode) {
        String sql = SELECT_COLUMNS + " WHERE entry_code = ?";
        List<Ticket> results = jdbcTemplate.query(sql, ticketRowMapper, entryCode);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Ticket> findBySeatNumber(String seatNumber) {
        String sql = SELECT_COLUMNS + " WHERE seat_number = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, seatNumber);
    }

    public List<Ticket> findByMatch_IdAndSeatNumber(UUID matchId, String seatNumber) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND seat_number = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, matchId, seatNumber);
    }

    public List<Ticket> findByMatch_IdAndSectionNumber(UUID matchId, String sectionNumber) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND section_number = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, matchId, sectionNumber);
    }

    public List<Ticket> findByMatch_IdAndRowNumber(UUID matchId, String rowNumber) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND row_number = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, matchId, rowNumber);
    }

    public List<Ticket> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        String sql = SELECT_COLUMNS + " WHERE price BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, ticketRowMapper, minPrice, maxPrice);
    }

    public List<Ticket> findByFinalPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        String sql = SELECT_COLUMNS + " WHERE final_price BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, ticketRowMapper, minPrice, maxPrice);
    }

    public List<Ticket> findByPurchaseDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = SELECT_COLUMNS + " WHERE purchase_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, ticketRowMapper, start, end);
    }

    public List<Ticket> findByMatch_IdAndStatusOrderBySeatNumberAsc(UUID matchId, TicketStatus status) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND status = ? ORDER BY seat_number ASC";
        return jdbcTemplate.query(sql, ticketRowMapper, matchId, status != null ? status.name() : null);
    }

    public List<Ticket> findByMatch_IdOrderBySeatNumberAsc(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? ORDER BY seat_number ASC";
        return jdbcTemplate.query(sql, ticketRowMapper, matchId);
    }

    public long countByMatch_Id(UUID matchId) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE match_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, matchId);
        return count != null ? count : 0L;
    }

    public long countByMatch_IdAndStatus(UUID matchId, TicketStatus status) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE match_id = ? AND status = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, matchId, status != null ? status.name() : null);
        return count != null ? count : 0L;
    }

    public long countByTicketCategory_Id(UUID categoryId) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE ticket_category_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, categoryId);
        return count != null ? count : 0L;
    }

    public Optional<Ticket> findByIdWithMatchAndCategory(UUID ticketId) {
        return findById(ticketId);
    }

    public Optional<Ticket> findByIdWithReservation(UUID ticketId) {
        return findById(ticketId);
    }

    public Optional<Ticket> findByIdWithBaseDetails(UUID ticketId) {
        String sql = "SELECT t.id, t.seat_number, t.row_number, t.section_number, t.price, t.discount_amount, t.final_price, " +
                     "t.status, t.purchase_date, t.barcode, t.qr_code, t.entry_code, t.ticket_category_id, t.match_id, t.base_details_id, " +
                     "bd.id AS bd_id, bd.tournament_name AS bd_tournament_name, bd.league_name AS bd_league_name, " +
                     "bd.facilities AS bd_facilities, bd.stadium_name AS bd_stadium_name, bd.sport_type AS bd_sport_type " +
                     "FROM tickets t LEFT JOIN base_details bd ON t.base_details_id = bd.id WHERE t.id = ?";
        
        List<Ticket> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Ticket ticket = ticketRowMapper.mapRow(rs, rowNum);
            if (ticket != null) {
                UUID bdId = (UUID) rs.getObject("bd_id");
                if (bdId != null) {
                    String sportType = rs.getString("bd_sport_type");
                    BaseDetails bd;
                    if ("BASKETBALL".equalsIgnoreCase(sportType)) {
                        bd = new BasketballDetails();
                    } else if ("VOLLEYBALL".equalsIgnoreCase(sportType)) {
                        bd = new VolleyballDetails();
                    } else {
                        bd = new FootballDetails();
                    }
                    bd.setId(bdId);
                    bd.setTournamentName(rs.getString("bd_tournament_name"));
                    bd.setLeagueName(rs.getString("bd_league_name"));
                    bd.setFacilities(rs.getString("bd_facilities"));
                    bd.setStadiumName(rs.getString("bd_stadium_name"));
                    ticket.setBaseDetails(bd);
                }
            }
            return ticket;
        }, ticketId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<Ticket> findByIdWithAllRelationships(UUID ticketId) {
        return findByIdWithBaseDetails(ticketId);
    }

    public List<Ticket> findAvailableTicketsByMatchId(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND status = 'NOT_RESERVED'";
        return jdbcTemplate.query(sql, ticketRowMapper, matchId);
    }

    public List<Ticket> findReservedTicketsByMatchId(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND status = 'RESERVED'";
        return jdbcTemplate.query(sql, ticketRowMapper, matchId);
    }

    public List<Ticket> findSoldTicketsByMatchId(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND status = 'SOLD'";
        return jdbcTemplate.query(sql, ticketRowMapper, matchId);
    }

    public List<Ticket> findUnavailableTicketsByMatchId(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND status IN ('RESERVED', 'SOLD')";
        return jdbcTemplate.query(sql, ticketRowMapper, matchId);
    }

    public List<Ticket> findTicketsByUserId(UUID userId) {
        String sql = "SELECT t.id, t.seat_number, t.row_number, t.section_number, t.price, t.discount_amount, t.final_price, t.status, t.purchase_date, t.barcode, t.qr_code, t.entry_code, t.ticket_category_id, t.match_id, t.base_details_id FROM tickets t JOIN reservations r ON t.id = r.ticket_id WHERE r.user_id = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, userId);
    }

    public List<Ticket> findPurchasedTicketsByUserId(UUID userId) {
        String sql = "SELECT t.id, t.seat_number, t.row_number, t.section_number, t.price, t.discount_amount, t.final_price, t.status, t.purchase_date, t.barcode, t.qr_code, t.entry_code, t.ticket_category_id, t.match_id, t.base_details_id FROM tickets t JOIN reservations r ON t.id = r.ticket_id WHERE r.user_id = ? AND t.status = 'SOLD'";
        return jdbcTemplate.query(sql, ticketRowMapper, userId);
    }

    public List<Ticket> findReservedTicketsByUserId(UUID userId) {
        String sql = "SELECT t.id, t.seat_number, t.row_number, t.section_number, t.price, t.discount_amount, t.final_price, t.status, t.purchase_date, t.barcode, t.qr_code, t.entry_code, t.ticket_category_id, t.match_id, t.base_details_id FROM tickets t JOIN reservations r ON t.id = r.ticket_id WHERE r.user_id = ? AND t.status = 'RESERVED'";
        return jdbcTemplate.query(sql, ticketRowMapper, userId);
    }

    public List<Ticket> findTicketsByReservationId(UUID reservationId) {
        String sql = "SELECT t.id, t.seat_number, t.row_number, t.section_number, t.price, t.discount_amount, t.final_price, t.status, t.purchase_date, t.barcode, t.qr_code, t.entry_code, t.ticket_category_id, t.match_id, t.base_details_id FROM tickets t JOIN reservations r ON t.id = r.ticket_id WHERE r.id = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, reservationId);
    }

    public List<Ticket> findSoldTicketsByMatchIdSince(UUID matchId, LocalDateTime since) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND status = 'SOLD' AND purchase_date >= ?";
        return jdbcTemplate.query(sql, ticketRowMapper, matchId, since);
    }

    public long countSoldTicketsByMatchId(UUID matchId) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE match_id = ? AND status = 'SOLD'";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, matchId);
        return count != null ? count : 0L;
    }

    public long countReservedTicketsByMatchId(UUID matchId) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE match_id = ? AND status = 'RESERVED'";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, matchId);
        return count != null ? count : 0L;
    }

    public BigDecimal sumFinalPriceByMatchId(UUID matchId) {
        String sql = "SELECT SUM(final_price) FROM tickets WHERE match_id = ? AND status = 'SOLD'";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, matchId);
    }

    public List<Object[]> countSoldTicketsByMatch() {
        String sql = "SELECT match_id, COUNT(*) FROM tickets WHERE status = 'SOLD' GROUP BY match_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[] { rs.getObject(1), rs.getLong(2) });
    }

    public List<Object[]> countSoldTicketsByCategoryForMatch(UUID matchId) {
        String sql = "SELECT ticket_category_id, COUNT(*) FROM tickets WHERE match_id = ? AND status = 'SOLD' GROUP BY ticket_category_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[] { rs.getObject(1), rs.getLong(2) }, matchId);
    }

    public Optional<Ticket> findAvailableTicketByMatchAndSeat(UUID matchId, String seatNumber) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND status = 'NOT_RESERVED' AND seat_number = ?";
        List<Ticket> results = jdbcTemplate.query(sql, ticketRowMapper, matchId, seatNumber);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Ticket> findSoldTicketsWithBarcode() {
        String sql = SELECT_COLUMNS + " WHERE barcode IS NOT NULL AND status = 'SOLD'";
        return jdbcTemplate.query(sql, ticketRowMapper);
    }

    public List<Ticket> findSoldTicketsWithQrCode() {
        String sql = SELECT_COLUMNS + " WHERE qr_code IS NOT NULL AND status = 'SOLD'";
        return jdbcTemplate.query(sql, ticketRowMapper);
    }

    public boolean existsByBarcode(String barcode) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE barcode = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, barcode);
        return count != null && count > 0;
    }

    public boolean existsByQrCode(String qrCode) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE qr_code = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, qrCode);
        return count != null && count > 0;
    }

    public boolean existsByEntryCode(String entryCode) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE entry_code = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, entryCode);
        return count != null && count > 0;
    }

    public List<Ticket> findExpiredReservations(LocalDateTime expiryTime) {
        String sql = "SELECT t.id, t.seat_number, t.row_number, t.section_number, t.price, t.discount_amount, t.final_price, t.status, t.purchase_date, t.barcode, t.qr_code, t.entry_code, t.ticket_category_id, t.match_id, t.base_details_id FROM tickets t JOIN reservations r ON t.id = r.ticket_id WHERE t.status = 'RESERVED' AND r.reservation_date <= ?";
        return jdbcTemplate.query(sql, ticketRowMapper, expiryTime);
    }

    public boolean existsById(UUID id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM table_name WHERE id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

}