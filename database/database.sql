CREATE TABLE users (
    id UUID PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(11),
    city VARCHAR(20),
    balance VARCHAR(255),
    password_hash VARCHAR(255) NOT NULL,
    registration_date TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE supports (
    id UUID PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(11),
    city VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    registration_date TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE sports (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    number_of_players INTEGER
);

CREATE TABLE teams (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(50),
    stadium VARCHAR(100),
    founded_year INTEGER,
    logo VARCHAR(200),
    coach VARCHAR(50),
    description VARCHAR(500)
);


CREATE TABLE tournaments (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    sport_id UUID NOT NULL,
    CONSTRAINT fk_tournament_sport FOREIGN KEY (sport_id) REFERENCES sports(id)
);

CREATE TABLE leagues (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(50),
    season VARCHAR(20),
    number_of_teams INTEGER,
    description VARCHAR(500),
    sport_id UUID NOT NULL,
    CONSTRAINT fk_league_sport FOREIGN KEY (sport_id) REFERENCES sports(id)
);

CREATE TABLE stadiums (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL,
    address VARCHAR(255)
);

CREATE TABLE matches (
    id UUID PRIMARY KEY,
    match_date TIMESTAMP,
    match_time TIMESTAMP,
    sport_type VARCHAR(50) NOT NULL,
    home_team_id UUID NOT NULL,
    away_team_id UUID NOT NULL,
    stadium_id UUID NOT NULL,
    league_id UUID,
    tournament_id UUID,
    
    CONSTRAINT fk_match_home_team FOREIGN KEY (home_team_id) REFERENCES teams(id),
    CONSTRAINT fk_match_away_team FOREIGN KEY (away_team_id) REFERENCES teams(id),
    CONSTRAINT fk_match_stadium FOREIGN KEY (stadium_id) REFERENCES stadiums(id),
    CONSTRAINT fk_match_league FOREIGN KEY (league_id) REFERENCES leagues(id),
    CONSTRAINT fk_match_tournament FOREIGN KEY (tournament_id) REFERENCES tournaments(id)
);

CREATE TABLE base_details (
    id UUID PRIMARY KEY,
    tournament_name VARCHAR(255),
    league_name VARCHAR(255),
    facilities VARCHAR(500),
    stadium_name VARCHAR(100),
    sport_type VARCHAR(255) NOT NULL
);

CREATE TABLE basketball_details (
    id UUID PRIMARY KEY,
    CONSTRAINT fk_basketball_details_base FOREIGN KEY (id) REFERENCES base_details(id)
);

CREATE TABLE football_details (
    id UUID PRIMARY KEY,
    CONSTRAINT fk_football_details_base FOREIGN KEY (id) REFERENCES base_details(id)
);

CREATE TABLE volleyball_details (
    id UUID PRIMARY KEY,
    CONSTRAINT fk_volleyball_details_base FOREIGN KEY (id) REFERENCES base_details(id)
);

CREATE TABLE ticket_categories (
    id UUID PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    total_capacity INTEGER NOT NULL,
    remaining_capacity INTEGER NOT NULL,
    description VARCHAR(500),
    match_id UUID NOT NULL,
    CONSTRAINT fk_ticket_category_match FOREIGN KEY (match_id) REFERENCES matches(id)
);

CREATE TABLE tickets (
    id UUID PRIMARY KEY,
    seat_number VARCHAR(255) NOT NULL,
    row_number VARCHAR(255),
    section_number VARCHAR(255),
    price NUMERIC(19, 2) NOT NULL,
    discount_amount NUMERIC(19, 2),
    final_price NUMERIC(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    purchase_date TIMESTAMP,
    barcode VARCHAR(50) UNIQUE,
    qr_code VARCHAR(200),
    entry_code VARCHAR(20),
    ticket_category_id UUID NOT NULL,
    match_id UUID NOT NULL,
    base_details_id UUID,
    CONSTRAINT fk_ticket_category FOREIGN KEY (ticket_category_id) REFERENCES ticket_categories(id),
    CONSTRAINT fk_ticket_match FOREIGN KEY (match_id) REFERENCES matches(id),
    CONSTRAINT fk_ticket_base_details FOREIGN KEY (base_details_id) REFERENCES base_details(id)
);

CREATE TABLE payments (
    id UUID PRIMARY KEY,
    amount NUMERIC(19, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_date TIMESTAMP,
    event_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(255) NOT NULL UNIQUE,
    payment_reference VARCHAR(255),
    bank_receipt_number VARCHAR(255),
    failure_reason VARCHAR(255),
    user_id UUID NOT NULL,
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE reports (
    id UUID PRIMARY KEY,
    subject VARCHAR(50) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    admin_response VARCHAR(500),
    status VARCHAR(50) NOT NULL,
    user_id UUID NOT NULL,
    support_id UUID,
    ticket_id UUID NOT NULL,
    CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_report_support FOREIGN KEY (support_id) REFERENCES supports(id),
    CONSTRAINT fk_report_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id)
);

CREATE TABLE reservations (
    id UUID PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    reservation_date TIMESTAMP NOT NULL,
    expiry TIMESTAMP NOT NULL,
    quantity INTEGER NOT NULL,
    payment_id UUID UNIQUE,
    ticket_id UUID NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    CONSTRAINT fk_reservation_payment FOREIGN KEY (payment_id) REFERENCES payments(id),
    CONSTRAINT fk_reservation_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id),
    CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE ticket_cancellations (
    id UUID PRIMARY KEY,
    penalty_percent INTEGER NOT NULL,
    refund_amount NUMERIC(19, 2) NOT NULL,
    cancellation_fee NUMERIC(19, 2),
    request_date TIMESTAMP NOT NULL,
    reason VARCHAR(500),
    user_id UUID NOT NULL,
    ticket_id UUID NOT NULL,
    CONSTRAINT fk_ticket_cancellation_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ticket_cancellation_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id)
);