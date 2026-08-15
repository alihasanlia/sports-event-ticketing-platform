package com.playtix.sports_event_ticketing_platform.domain.entity.ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.details.BaseDetails;
import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.Reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ticket {

    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotBlank(message = "Seat number is required")
    private String seatNumber;

    private String rowNumber;

    private String sectionNumber;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @PositiveOrZero(message = "Discount cannot be negative")
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull(message = "Final price is required")
    @PositiveOrZero(message = "Final price cannot be negative")
    private BigDecimal finalPrice;

    @NotNull(message = "Status is required")
    @Builder.Default
    private TicketStatus status = TicketStatus.NOT_RESERVED;

    private LocalDateTime purchaseDate;

    private String barcode;

    private String qrCode;

    private String entryCode;

    @NotNull(message = "Ticket category is required")
    private TicketCategory ticketCategory;

    @NotNull(message = "Match is required")
    private Match match;

    private BaseDetails baseDetails;

    private Reservation reservation;
    
    // متد جایگزین @PrePersist برای مقداردهی اولیه
    public void initializeDefaults() {
        if (this.finalPrice == null && this.price != null) {
            this.finalPrice = this.price.subtract(
                this.discountAmount != null ? this.discountAmount : BigDecimal.ZERO
            );
        }
        if (this.status == null) {
            this.status = TicketStatus.NOT_RESERVED;
        }
    }
    
    public void reserve() {
        if (this.status != TicketStatus.NOT_RESERVED) {
            throw new IllegalStateException("Ticket is already reserved or sold");
        }
        this.status = TicketStatus.RESERVED;
    }

    public void purchase() {
        if (this.status != TicketStatus.RESERVED) {
            throw new IllegalStateException("Ticket must be reserved first");
        }
        this.status = TicketStatus.SOLD;
        this.purchaseDate = LocalDateTime.now();
    }

    public void cancelReservation() {
        if (this.status != TicketStatus.RESERVED) {
            throw new IllegalStateException("Ticket is not reserved");
        }
        this.status = TicketStatus.NOT_RESERVED;
    }
    
    public boolean isAvailable() {
        return this.status == TicketStatus.NOT_RESERVED;
    }

    public boolean isReserved() {
        return this.status == TicketStatus.RESERVED;
    }

    public boolean isSold() {
        return this.status == TicketStatus.SOLD;
    }

    public BigDecimal getFinalPrice() {
        if (finalPrice != null) {
            return finalPrice;
        }
        if (price != null && discountAmount != null) {
            return price.subtract(discountAmount);
        }
        return price;
    }

    public String getSeatInfo() {
        String info = "Seat " + seatNumber;
        if (rowNumber != null) {
            info += ", Row " + rowNumber;
        }
        if (sectionNumber != null) {
            info += ", Section " + sectionNumber;
        }
        return info;
    }
    
    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", seatNumber='" + seatNumber + '\'' +
                ", status=" + status +
                ", price=" + price +
                ", finalPrice=" + finalPrice +
                '}';
    }
}