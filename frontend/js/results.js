/* =====================================================
   API CONFIGURATION
===================================================== */

const API_BASE_URL = "http://localhost:8081";


/* =====================================================
   DOM ELEMENTS
===================================================== */

const searchDescription =
    document.getElementById("searchDescription");

const resultsContainer =
    document.getElementById("searchResults");

const noResults =
    document.getElementById("noResults");


/* =====================================================
   URL PARAMETERS
===================================================== */

const urlParams =
    new URLSearchParams(window.location.search);

const searchQuery =
    urlParams.get("q") || "";

const searchType =
    urlParams.get("type") || "general";

const dateParam =
    urlParams.get("date") || "";


/* =====================================================
   AUTHENTICATION
===================================================== */

function getToken() {
    return localStorage.getItem("token");
}


function getAuthHeaders() {

    const token = getToken();

    return {
        "Content-Type": "application/json",

        ...(token
            ? {
                "Authorization": `Bearer ${token}`
            }
            : {})
    };
}


/* =====================================================
   GET USER EMAIL FROM JWT
===================================================== */

function getUserEmailFromToken() {

    const token = getToken();

    if (!token) {
        return null;
    }

    try {

        const parts = token.split(".");

        if (parts.length !== 3) {
            return null;
        }

        const payload =
            JSON.parse(
                atob(
                    parts[1]
                        .replace(/-/g, "+")
                        .replace(/_/g, "/")
                )
            );

        /*
         * Backend JwtUtil uses:
         *
         * .subject(userDetails.getUsername())
         *
         * Therefore the user's email/username is in "sub".
         */

        return payload.sub || null;

    } catch (error) {

        console.error(
            "Could not decode JWT:",
            error
        );

        return null;
    }
}


/* =====================================================
   GET USER ID
===================================================== */

async function getCurrentUserId() {

    const email =
        getUserEmailFromToken();

    if (!email) {
        return null;
    }

    const response =
        await fetch(
            `${API_BASE_URL}/api/v1/users/email/${encodeURIComponent(email)}`,
            {
                method: "GET",
                headers: getAuthHeaders()
            }
        );

    if (response.status === 401) {

        handleUnauthorized();

        return null;
    }

    if (!response.ok) {

        throw new Error(
            "Could not retrieve user information."
        );
    }

    const user =
        await response.json();

    return user.id || null;
}


/* =====================================================
   HANDLE UNAUTHORIZED
===================================================== */

function handleUnauthorized() {

    localStorage.removeItem("token");

    alert(
        "Your session has expired. Please login again."
    );

    window.location.href =
        "login.html";
}


/* =====================================================
   HTML ESCAPE
===================================================== */

function escapeHtml(value) {

    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


/* =====================================================
   FORMAT DATE
===================================================== */

function formatMatchDate(dateValue) {

    if (!dateValue) {
        return "Date unavailable";
    }

    const date =
        new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
        return dateValue;
    }

    return date.toLocaleDateString(
        "en-US",
        {
            year: "numeric",
            month: "long",
            day: "numeric"
        }
    );
}


/* =====================================================
   FORMAT TIME
===================================================== */

function formatMatchTime(dateValue) {

    if (!dateValue) {
        return "Time unavailable";
    }

    const date =
        new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
        return "";
    }

    return date.toLocaleTimeString(
        "en-US",
        {
            hour: "2-digit",
            minute: "2-digit"
        }
    );
}


/* =====================================================
   FORMAT PRICE
===================================================== */

function formatPrice(price) {

    if (
        price === null ||
        price === undefined ||
        price === ""
    ) {
        return "Price unavailable";
    }

    const numericPrice =
        Number(price);

    if (Number.isNaN(numericPrice)) {
        return `${escapeHtml(price)} Toman`;
    }

    return `${numericPrice.toLocaleString()} Toman`;
}


/* =====================================================
   UPDATE SEARCH DESCRIPTION
===================================================== */

function updateDescription() {

    if (dateParam) {

        const date =
            new Date(dateParam);

        if (!Number.isNaN(date.getTime())) {

            searchDescription.textContent =
                `Matches on ${date.toLocaleDateString(
                    "en-US",
                    {
                        year: "numeric",
                        month: "long",
                        day: "numeric"
                    }
                )}`;

            return;
        }
    }


    if (!searchQuery) {

        searchDescription.textContent =
            "Showing all upcoming matches.";

        return;
    }


    const typeLabels = {

        league:
            "League / Tournament",

        team:
            "Team",

        stadium:
            "Stadium",

        general:
            "Search"
    };


    const label =
        typeLabels[searchType] ||
        "Search";


    searchDescription.textContent =
        `${label}: "${searchQuery}"`;
}


/* =====================================================
   FETCH MATCHES
===================================================== */

async function fetchMatches() {

    /*
     * Backend provides:
     *
     * GET /api/v1/matches/upcoming
     *
     * GET /api/v1/matches/date-range
     *
     * MatchSummaryDto contains:
     *
     * id
     * homeTeam
     * awayTeam
     * matchDate
     * stadiumName
     * tournamentName
     * leagueName
     */

    if (dateParam) {

        const date =
            new Date(dateParam);

        if (Number.isNaN(date.getTime())) {
            throw new Error(
                "Invalid date parameter."
            );
        }

        const startDate =
            new Date(date);

        startDate.setHours(
            0,
            0,
            0,
            0
        );

        const endDate =
            new Date(date);

        endDate.setHours(
            23,
            59,
            59,
            999
        );


        const start =
            formatLocalDateTime(startDate);

        const end =
            formatLocalDateTime(endDate);


        const response =
            await fetch(
                `${API_BASE_URL}/api/v1/matches/date-range?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}`,
                {
                    method: "GET",
                    headers: getAuthHeaders()
                }
            );


        if (response.status === 401) {
            handleUnauthorized();
            return [];
        }


        if (!response.ok) {

            throw new Error(
                "Could not load matches for the selected date."
            );
        }


        return await response.json();
    }


    /*
     * There is no direct Match search endpoint.
     *
     * Therefore we get upcoming matches and
     * filter them client-side using the actual
     * MatchSummaryDto fields.
     */

    const response =
        await fetch(
            `${API_BASE_URL}/api/v1/matches/upcoming`,
            {
                method: "GET",
                headers: getAuthHeaders()
            }
        );


    if (response.status === 401) {
        handleUnauthorized();
        return [];
    }


    if (!response.ok) {

        throw new Error(
            "Could not load upcoming matches."
        );
    }


    let matches =
        await response.json();


    if (!searchQuery) {
        return matches;
    }


    const query =
        searchQuery
            .trim()
            .toLowerCase();


    matches =
        matches.filter(
            match => {

                const homeTeam =
                    String(
                        match.homeTeam || ""
                    ).toLowerCase();

                const awayTeam =
                    String(
                        match.awayTeam || ""
                    ).toLowerCase();

                const stadium =
                    String(
                        match.stadiumName || ""
                    ).toLowerCase();

                const league =
                    String(
                        match.leagueName || ""
                    ).toLowerCase();

                const tournament =
                    String(
                        match.tournamentName || ""
                    ).toLowerCase();


                switch (searchType) {

                    case "team":

                        return (
                            homeTeam.includes(query) ||
                            awayTeam.includes(query)
                        );


                    case "stadium":

                        return stadium.includes(query);


                    case "league":

                        return (
                            league.includes(query) ||
                            tournament.includes(query)
                        );


                    case "general":

                    default:

                        return (
                            homeTeam.includes(query) ||
                            awayTeam.includes(query) ||
                            stadium.includes(query) ||
                            league.includes(query) ||
                            tournament.includes(query)
                        );
                }
            }
        );


    return matches;
}


/* =====================================================
   FORMAT LOCAL DATETIME
===================================================== */

function formatLocalDateTime(date) {

    const year =
        date.getFullYear();

    const month =
        String(
            date.getMonth() + 1
        ).padStart(2, "0");

    const day =
        String(
            date.getDate()
        ).padStart(2, "0");

    const hours =
        String(
            date.getHours()
        ).padStart(2, "0");

    const minutes =
        String(
            date.getMinutes()
        ).padStart(2, "0");

    const seconds =
        String(
            date.getSeconds()
        ).padStart(2, "0");


    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
}


/* =====================================================
   CREATE MATCH CARD
===================================================== */

function createMatchCard(match) {

    const article =
        document.createElement("article");

    article.className =
        "ticket-card search-match-card";


    const matchId =
        escapeHtml(match.id);

    const homeTeam =
        escapeHtml(match.homeTeam);

    const awayTeam =
        escapeHtml(match.awayTeam);

    const stadium =
        escapeHtml(
            match.stadiumName ||
            "Stadium unavailable"
        );

    const league =
        escapeHtml(
            match.leagueName ||
            match.tournamentName ||
            "League unavailable"
        );


    const formattedDate =
        formatMatchDate(
            match.matchDate
        );

    const formattedTime =
        formatMatchTime(
            match.matchDate
        );


    article.innerHTML = `

        <div class="ticket-header">

            <div>

                <h2>
                    ${homeTeam}
                    vs
                    ${awayTeam}
                </h2>

                <p class="ticket-id">
                    Match ID: #${matchId}
                </p>

            </div>

            <span class="status-badge status-active">
                AVAILABLE
            </span>

        </div>


        <div class="ticket-info">

            <div class="ticket-info-item">

                <span class="info-label">
                    League / Tournament
                </span>

                <span>
                    ${league}
                </span>

            </div>


            <div class="ticket-info-item">

                <span class="info-label">
                    Date
                </span>

                <span>
                    ${escapeHtml(formattedDate)}
                </span>

            </div>


            <div class="ticket-info-item">

                <span class="info-label">
                    Time
                </span>

                <span>
                    ${escapeHtml(formattedTime)}
                </span>

            </div>


            <div class="ticket-info-item">

                <span class="info-label">
                    Stadium
                </span>

                <span>
                    ${stadium}
                </span>

            </div>

        </div>


        <!-- ================= SEAT SELECTION ================= -->

        <div
            class="seat-selection"
            id="seat-selection-${matchId}"
        >

            <div class="seat-selection-header">

                <div>

                    <h3 class="seat-selection-title">
                        Select Your Seat
                    </h3>

                    <p class="seat-selection-subtitle">
                        Choose one available seat for this ticket.
                    </p>

                </div>

            </div>


            <div
                class="loading-seats"
                id="seat-loading-${matchId}"
            >
                Loading available seats...
            </div>


            <div
                class="seat-grid"
                id="seat-grid-${matchId}"
                style="display: none;"
            >
            </div>


            <div
                class="no-seats"
                id="no-seats-${matchId}"
                style="display: none;"
            >
                No available seats were found for this match.
            </div>


            <div
                class="selected-seat-info"
                id="selected-seat-info-${matchId}"
                style="display: none;"
            >

                <div>

                    <span class="selected-seat-label">
                        Selected seat
                    </span>

                    <div
                        class="selected-seat-value"
                        id="selected-seat-${matchId}"
                    >
                        -
                    </div>

                </div>


                <div>

                    <span class="selected-seat-label">
                        Price
                    </span>

                    <div
                        class="seat-price"
                        id="selected-price-${matchId}"
                    >
                        -
                    </div>

                </div>

            </div>


            <div
                class="seat-message"
                id="seat-message-${matchId}"
            >
            </div>

        </div>


        <!-- ================= RESERVATION ================= -->

        <div class="match-card-actions">

            <button
                type="button"
                class="primary-btn reserve-btn"
                data-match-id="${matchId}"
                disabled
            >
                Reserve Selected Seat
            </button>

        </div>

    `;


    /*
     * Load available tickets/seats after the
     * card has been inserted into the DOM.
     */

    const reserveButton =
        article.querySelector(
            ".reserve-btn"
        );


    reserveButton.addEventListener(
        "click",
        () => {

            reserveSelectedSeat(
                match,
                reserveButton
            );
        }
    );


    loadAvailableSeats(
        match,
        article
    );


    return article;
}


/* =====================================================
   LOAD AVAILABLE SEATS
===================================================== */

async function loadAvailableSeats(
    match,
    article
) {

    const matchId =
        match.id;


    const loadingElement =
        article.querySelector(
            `#seat-loading-${matchId}`
        );

    const seatGrid =
        article.querySelector(
            `#seat-grid-${matchId}`
        );

    const noSeatsElement =
        article.querySelector(
            `#no-seats-${matchId}`
        );


    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/v1/tickets/match/${encodeURIComponent(matchId)}/available`,
                {
                    method: "GET",
                    headers: getAuthHeaders()
                }
            );


        if (response.status === 401) {

            handleUnauthorized();

            return;
        }


        if (!response.ok) {

            throw new Error(
                "Could not load available seats."
            );
        }


        const tickets =
            await response.json();


        loadingElement.style.display =
            "none";


        if (
            !Array.isArray(tickets) ||
            tickets.length === 0
        ) {

            noSeatsElement.style.display =
                "block";

            return;
        }


        seatGrid.style.display =
            "grid";


        renderSeats(
            tickets,
            article
        );


    } catch (error) {

        console.error(
            "Load seats error:",
            error
        );


        loadingElement.textContent =
            "Could not load available seats.";
    }
}


/* =====================================================
   RENDER SEATS
===================================================== */

function renderSeats(
    tickets,
    article
) {

    if (!Array.isArray(tickets)) {
        return;
    }


    const seatGrid =
        article.querySelector(
            ".seat-grid"
        );


    const reserveButton =
        article.querySelector(
            ".reserve-btn"
        );


    const messageElement =
        article.querySelector(
            ".seat-message"
        );


    /*
     * Store ticket objects on the article.
     * Each available Ticket represents one
     * selectable seat.
     */

    article._availableTickets =
        tickets;


    seatGrid.innerHTML = "";


    tickets.forEach(
        ticket => {

            const button =
                document.createElement(
                    "button"
                );


            button.type =
                "button";

            button.className =
                "seat-button";


            const seatNumber =
                ticket.seatNumber ||
                "Seat";


            const rowNumber =
                ticket.rowNumber ||
                "";

            const sectionNumber =
                ticket.sectionNumber ||
                "";


            let details = "";


            if (rowNumber) {

                details +=
                    `Row ${escapeHtml(rowNumber)}`;
            }


            if (sectionNumber) {

                if (details) {
                    details += " · ";
                }

                details +=
                    `Section ${escapeHtml(sectionNumber)}`;
            }


            button.innerHTML = `

                <span class="seat-number">
                    ${escapeHtml(seatNumber)}
                </span>

                ${
                    details
                        ? `
                            <span class="seat-details">
                                ${details}
                            </span>
                          `
                        : ""
                }

            `;


            button.addEventListener(
                "click",
                () => {

                    selectSeat(
                        ticket,
                        article
                    );
                }
            );


            seatGrid.appendChild(
                button
            );
        }
    );


    /*
     * No seat is selected initially,
     * therefore Reserve remains disabled.
     */

    reserveButton.disabled =
        true;


    messageElement.textContent =
        `${tickets.length} available seat${tickets.length === 1 ? "" : "s"}.`;

    messageElement.className =
        "seat-message";
}


/* =====================================================
   SELECT SEAT
===================================================== */

function selectSeat(
    ticket,
    article
) {

    const buttons =
        article.querySelectorAll(
            ".seat-button"
        );


    buttons.forEach(
        button => {

            button.classList.remove(
                "selected"
            );
        }
    );


    /*
     * Find the button representing the
     * selected ticket.
     */

    buttons.forEach(
        button => {

            const displayedSeat =
                button.querySelector(
                    ".seat-number"
                )?.textContent
                    .trim();


            if (
                displayedSeat ===
                String(ticket.seatNumber)
            ) {

                button.classList.add(
                    "selected"
                );
            }
        }
    );


    article._selectedTicket =
        ticket;


    const matchId =
        article.dataset.matchId;


    const selectedInfo =
        article.querySelector(
            ".selected-seat-info"
        );


    const selectedSeat =
        article.querySelector(
            ".selected-seat-value"
        );


    const selectedPrice =
        article.querySelector(
            ".seat-price"
        );


    const reserveButton =
        article.querySelector(
            ".reserve-btn"
        );


    const messageElement =
        article.querySelector(
            ".seat-message"
        );


    selectedInfo.style.display =
        "flex";


    selectedSeat.textContent =
        ticket.seatNumber ||
        "Seat";


    selectedPrice.textContent =
        formatPrice(
            ticket.price
        );


    reserveButton.disabled =
        false;


    messageElement.textContent =
        "Seat selected. You can now reserve it.";

    messageElement.className =
        "seat-message success";
}


/* =====================================================
   RESERVE SELECTED SEAT
===================================================== */

async function reserveSelectedSeat(
    match,
    button
) {

    const article =
        button.closest(
            ".search-match-card"
        );


    if (!article) {
        return;
    }


    const selectedTicket =
        article._selectedTicket;


    if (!selectedTicket) {

        alert(
            "Please select a seat first."
        );

        return;
    }


    const token =
        getToken();


    if (!token) {

        alert(
            "Please login to reserve a ticket."
        );

        window.location.href =
            "login.html";

        return;
    }


    button.disabled =
        true;

    button.textContent =
        "Reserving...";


    try {

        /*
         * JWT subject contains the user's
         * username/email, not UUID.
         *
         * We therefore resolve the actual
         * UUID through UserController.
         */

        const userId =
            await getCurrentUserId();


        if (!userId) {

            throw new Error(
                "Could not determine the current user."
            );
        }


        /*
         * Backend CreateReservationRequest:
         *
         * {
         *     userId,
         *     ticketId,
         *     quantity
         * }
         */

        const reservationData = {

            userId: userId,

            ticketId:
                selectedTicket.id,

            quantity: 1
        };


        console.log(
            "Creating reservation:",
            reservationData
        );


        const response =
            await fetch(
                `${API_BASE_URL}/api/v1/reservations`,
                {
                    method: "POST",

                    headers:
                        getAuthHeaders(),

                    body:
                        JSON.stringify(
                            reservationData
                        )
                }
            );


        if (response.status === 401) {

            handleUnauthorized();

            return;
        }


        if (!response.ok) {

            let message =
                "Could not reserve the selected seat.";


            try {

                const errorData =
                    await response.json();


                if (
                    errorData.message
                ) {

                    message =
                        errorData.message;
                }

            } catch (error) {

                console.error(
                    "Could not parse error response:",
                    error
                );
            }


            throw new Error(
                message
            );
        }


        const reservation =
            await response.json();


        console.log(
            "Reservation created:",
            reservation
        );


        /*
         * Reservation was successful.
         *
         * Backend returns UserReservationDto
         * containing reservation id.
         */

        const reservationId =
            reservation.id;


        alert(
            `Seat ${selectedTicket.seatNumber} reserved successfully!`
        );


        /*
         * Preserve the reservation ID for
         * the payment page.
         *
         * If payment.html already reads
         * reservationId from the URL, this
         * will continue the existing flow.
         */

        if (reservationId) {

            window.location.href =
                `payment.html?reservationId=${encodeURIComponent(
                    reservationId
                )}`;

            return;
        }


        /*
         * Fallback if the backend response
         * somehow does not contain an ID.
         */

        window.location.href =
            "tickets.html";


    } catch (error) {

        console.error(
            "Reservation error:",
            error
        );


        alert(
            error.message ||
            "Could not reserve the selected seat."
        );


        /*
         * The reservation may fail because
         * another user took the seat.
         *
         * Reload available seats so the UI
         * reflects the current backend state.
         */

        await loadAvailableSeats(
            match,
            article
        );


        button.disabled =
            !article._selectedTicket;

        button.textContent =
            "Reserve Selected Seat";
    }
}


/* =====================================================
   DISPLAY MATCHES
===================================================== */

function displayMatches(matches) {

    resultsContainer.innerHTML =
        "";

    noResults.style.display =
        "none";


    if (
        !Array.isArray(matches) ||
        matches.length === 0
    ) {

        noResults.style.display =
            "block";

        return;
    }


    matches.forEach(
        match => {

            if (!match || !match.id) {
                return;
            }


            const card =
                createMatchCard(
                    match
                );


            /*
             * Store the Match ID directly
             * on the card for seat handling.
             */

            card.dataset.matchId =
                match.id;


            resultsContainer.appendChild(
                card
            );
        }
    );


    if (
        resultsContainer.children.length === 0
    ) {

        noResults.style.display =
            "block";
    }
}


/* =====================================================
   LOAD RESULTS
===================================================== */

async function loadResults() {

    updateDescription();


    resultsContainer.innerHTML =
        "";

    noResults.style.display =
        "none";


    try {

        const matches =
            await fetchMatches();


        displayMatches(
            matches
        );


    } catch (error) {

        console.error(
            "Load results error:",
            error
        );


        resultsContainer.innerHTML =
            "";


        noResults.style.display =
            "block";


        const message =
            noResults.querySelector(
                "p"
            );


        if (message) {

            message.textContent =
                error.message ||
                "Failed to load matches. Please try again later.";
        }
    }
}


/* =====================================================
   INITIALIZE
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    loadResults
);