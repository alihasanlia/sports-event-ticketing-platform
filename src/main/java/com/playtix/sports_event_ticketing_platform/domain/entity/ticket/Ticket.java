package com.playtix.sports_event_ticketing_platform.domain.entity.ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.details.BaseDetails;
import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.Reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotBlank(message = "Seat number is required")
    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "row_number")
    private String rowNumber;

    @Column(name = "section_number")
    private String sectionNumber;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @PositiveOrZero(message = "Discount cannot be negative")
    @Column(name = "discount_amount", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull(message = "Final price is required")
    @PositiveOrZero(message = "Final price cannot be negative")
    @Column(name = "final_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal finalPrice;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TicketStatus status = TicketStatus.NOT_RESERVED;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(length = 50, unique = true)
    private String barcode;

    @Column(name = "qr_code", length = 200)
    private String qrCode;

    @Column(name = "entry_code", length = 20)
    private String entryCode;

    @NotNull(message = "Ticket category is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_category_id", nullable = false)
    private TicketCategory ticketCategory;

    @NotNull(message = "Match is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_details_id")
    private BaseDetails baseDetails;

    @OneToOne(mappedBy = "ticket", fetch = FetchType.LAZY)
    private Reservation reservation;
    
    @PrePersist
    protected void onCreate() {
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