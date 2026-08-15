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
   GET TOKEN
===================================================== */

function getToken() {

    return localStorage.getItem("token");
}


/* =====================================================
   AUTH HEADERS
===================================================== */

function getAuthHeaders() {

    const token = getToken();

    return {

        "Content-Type": "application/json",

        ...(token ? { "Authorization": `Bearer ${token}` } : {})

    };
}


/* =====================================================
   GET URL PARAMETERS
===================================================== */

const urlParams =
    new URLSearchParams(
        window.location.search
    );

const searchQuery =
    urlParams.get("q") || "";

const searchType =
    urlParams.get("type") || "general";

const dateParam =
    urlParams.get("date") || "";


/* =====================================================
   UPDATE DESCRIPTION
===================================================== */

function updateDescription() {

    if (dateParam) {

        const date = new Date(dateParam);

        searchDescription.textContent =
            `Matches on ${date.toLocaleDateString("en-US", { year: "numeric", month: "long", day: "numeric" })}`;

        return;
    }

    if (!searchQuery) {

        searchDescription.textContent =
            "Showing all available matches.";

        return;
    }

    const typeLabels = {
        league: "League / Tournament",
        team: "Team",
        stadium: "Stadium",
        general: "Search"
    };

    const label =
        typeLabels[searchType] || "Search";

    searchDescription.textContent =
        `${label}: "${searchQuery}"`;
}


/* =====================================================
   CREATE MATCH CARD
===================================================== */

function createMatchCard(match) {

    const article =
        document.createElement("article");

    article.className =
        "ticket-card search-match-card";


    // Facilities
    const facilitiesHtml =
        match.facilities
            .map(f => `<span class="facility-tag">${f}</span>`)
            .join("");


    // Ticket categories
    const optionsHtml =
        match.categories
            .map(cat => `
                <option value="${cat.name}" data-price="${cat.price}">
                    ${cat.name}
                </option>
            `)
            .join("");


    const defaultPrice =
        match.categories[0]?.price || 0;


    article.innerHTML = `

        <div class="ticket-header">

            <div>

                <h2>
                    ${match.homeTeam} vs ${match.awayTeam}
                </h2>

                <p class="ticket-id">
                    Match ID: #${match.id}
                </p>

            </div>

            <span class="status-badge status-active">
                AVAILABLE
            </span>

        </div>


        <div class="ticket-info">

            <div class="ticket-info-item">

                <span class="info-label">
                    Sport
                </span>

                <span>
                    ${match.sport}
                </span>

            </div>

            <div class="ticket-info-item">

                <span class="info-label">
                    League / Tournament
                </span>

                <span>
                    ${match.league}
                </span>

            </div>

            <div class="ticket-info-item">

                <span class="info-label">
                    Date
                </span>

                <span>
                    ${match.date}
                </span>

            </div>

            <div class="ticket-info-item">

                <span class="info-label">
                    Time
                </span>

                <span>
                    ${match.time}
                </span>

            </div>

            <div class="ticket-info-item">

                <span class="info-label">
                    Stadium
                </span>

                <span>
                    ${match.stadium}
                </span>

            </div>

            <div class="ticket-info-item">

                <span class="info-label">
                    Remaining Capacity
                </span>

                <span>
                    ${match.remainingCapacity.toLocaleString()} seats
                </span>

            </div>

        </div>


        <div class="stadium-facilities">

            <span class="info-label">
                Stadium Facilities
            </span>

            <div class="facility-list">
                ${facilitiesHtml}
            </div>

        </div>


        <div class="ticket-booking-section">

            <div class="form-group">

                <label for="category-${match.id}">
                    Ticket Category
                </label>

                <select
                    id="category-${match.id}"
                    class="ticket-category-select"
                    data-match-id="${match.id}"
                    data-price-target="price-${match.id}"
                >
                    ${optionsHtml}
                </select>

            </div>


            <div class="selected-ticket-price">

                <span class="info-label">
                    Price
                </span>

                <strong id="price-${match.id}">
                    ${Number(defaultPrice).toLocaleString()} Toman
                </strong>

            </div>


            <button
                type="button"
                class="primary-btn reserve-btn"
                data-match-id="${match.id}"
                data-match="${match.homeTeam} vs ${match.awayTeam}"
                data-stadium="${match.stadium}"
                data-category-select="category-${match.id}"
                onclick="reserveTicket(this)"
            >
                Reserve
            </button>

        </div>

    `;

    return article;
}


/* =====================================================
   LOAD RESULTS
===================================================== */

async function loadResults() {

    updateDescription();

    resultsContainer.innerHTML = "";
    noResults.style.display = "none";


    try {

        // If date is provided, fetch matches by date range
        if (dateParam) {

            const date = new Date(dateParam);

            const startDate =
                new Date(date);
            startDate.setHours(0, 0, 0, 0);

            const endDate =
                new Date(date);
            endDate.setHours(23, 59, 59, 999);

            const response =
                await fetch(
                    `${API_BASE_URL}/api/v1/matches/date-range?start=${startDate.toISOString()}&end=${endDate.toISOString()}`,
                    {
                        method: "GET",

                        headers:
                            getAuthHeaders()
                    }
                );

            if (!response.ok) {

                throw new Error(
                    "Could not load matches."
                );
            }

            const matches =
                await response.json();

            displayMatches(matches);

            return;
        }


        // If search query is provided
        if (searchQuery) {

            let endpoint =
                "/api/v1/matches/search";

            const q =
                searchQuery.trim();

            // Try different search endpoints based on type
            let response;

            if (searchType === "league") {

                response =
                    await fetch(
                        `${API_BASE_URL}/api/v1/leagues/search?keyword=${encodeURIComponent(q)}`,
                        {
                            method: "GET",

                            headers:
                                getAuthHeaders()
                        }
                    );

                if (response.ok) {

                    const leagues =
                        await response.json();

                    // For each league, get its matches
                    let allMatches = [];

                    for (const league of leagues) {

                        const matchesResponse =
                            await fetch(
                                `${API_BASE_URL}/api/v1/matches/league/${league.id}`,
                                {
                                    method: "GET",

                                    headers:
                                        getAuthHeaders()
                                }
                            );

                        if (matchesResponse.ok) {

                            const leagueMatches =
                                await matchesResponse.json();

                            allMatches =
                                allMatches.concat(
                                    leagueMatches
                                );
                        }
                    }

                    displayMatches(
                        allMatches
                    );

                    return;
                }

            } else if (searchType === "stadium") {

                response =
                    await fetch(
                        `${API_BASE_URL}/api/v1/stadiums/search?name=${encodeURIComponent(q)}`,
                        {
                            method: "GET",

                            headers:
                                getAuthHeaders()
                        }
                    );

                if (response.ok) {

                    const stadiums =
                        await response.json();

                    let allMatches = [];

                    for (const stadium of stadiums) {

                        const matchesResponse =
                            await fetch(
                                `${API_BASE_URL}/api/v1/matches/stadium/${stadium.id}`,
                                {
                                    method: "GET",

                                    headers:
                                        getAuthHeaders()
                                }
                            );

                        if (matchesResponse.ok) {

                            const stadiumMatches =
                                await matchesResponse.json();

                            allMatches =
                                allMatches.concat(
                                    stadiumMatches
                                );
                        }
                    }

                    displayMatches(
                        allMatches
                    );

                    return;
                }

            } else {

                // General search - try to search matches directly
                // Note: The MatchController doesn't have a direct search endpoint,
                // so we'll use sport and league filtering

                // For now, we'll show all upcoming matches
                // In a real implementation, you'd have a search endpoint

                response =
                    await fetch(
                        `${API_BASE_URL}/api/v1/matches/upcoming`,
                        {
                            method: "GET",

                            headers:
                                getAuthHeaders()
                        }
                    );

                if (response.ok) {

                    let matches =
                        await response.json();

                    // Filter matches client-side for demo
                    const qLower =
                        q.toLowerCase();

                    matches =
                        matches.filter(m =>
                            m.homeTeam.toLowerCase().includes(qLower) ||
                            m.awayTeam.toLowerCase().includes(qLower) ||
                            m.stadium.toLowerCase().includes(qLower) ||
                            m.league.toLowerCase().includes(qLower)
                        );

                    displayMatches(matches);

                    return;
                }
            }

            // If all else fails, show no results
            noResults.style.display = "block";

            return;
        }


        // If no search query, show upcoming matches
        const response =
            await fetch(
                `${API_BASE_URL}/api/v1/matches/upcoming`,
                {
                    method: "GET",

                    headers:
                        getAuthHeaders()
                }
            );

        if (!response.ok) {

            throw new Error(
                "Could not load matches."
            );
        }

        const matches =
            await response.json();

        displayMatches(matches);


    } catch (error) {

        console.error(
            "Load results error:",
            error
        );

        noResults.style.display = "block";

        noResults.querySelector("p").textContent =
            "Failed to load matches. Please try again later.";
    }
}


/* =====================================================
   DISPLAY MATCHES
===================================================== */

function displayMatches(matches) {

    if (!matches || matches.length === 0) {

        noResults.style.display = "block";

        return;
    }


    matches.forEach(match => {

        // Convert to the expected format for createMatchCard
        const formattedMatch = {

            id: match.id || "MATCH-001",

            homeTeam: match.homeTeam || "Team A",

            awayTeam: match.awayTeam || "Team B",

            sport: match.sport || "Football",

            league: match.league || "League",

            date: match.date || new Date().toLocaleDateString(),

            time: match.time || "18:00",

            stadium: match.stadium || "Stadium",

            remainingCapacity: match.remainingCapacity || 1000,

            facilities: match.facilities || ["Parking", "Food Court"],

            categories: match.categories || [
                { name: "Regular", price: 500000 }
            ]

        };

        resultsContainer.appendChild(
            createMatchCard(formattedMatch)
        );
    });


    // Attach price listeners
    attachPriceListeners();
}


/* =====================================================
   CHANGE PRICE ON CATEGORY SELECT
===================================================== */

function attachPriceListeners() {

    document.querySelectorAll(
        ".ticket-category-select"
    ).forEach(select => {

        select.addEventListener(
            "change",
            function () {

                const selectedOption =
                    this.options[this.selectedIndex];

                const price =
                    selectedOption.dataset.price;

                const priceTarget =
                    document.getElementById(
                        this.dataset.priceTarget
                    );

                if (priceTarget) {

                    priceTarget.textContent =
                        Number(price).toLocaleString() +
                        " Toman";
                }

            }
        );

    });
}


/* =====================================================
   RESERVE TICKET
===================================================== */

async function reserveTicket(button) {

    const matchId =
        button.dataset.matchId;

    const match =
        button.dataset.match;

    const stadium =
        button.dataset.stadium;

    const categorySelect =
        document.getElementById(
            button.dataset.categorySelect
        );

    const selectedOption =
        categorySelect.options[
            categorySelect.selectedIndex
            ];

    const category =
        selectedOption.value;

    const price =
        Number(selectedOption.dataset.price);


    const userId =
        getUserIdFromToken();

    if (!userId) {

        alert(
            "Please login to reserve a ticket."
        );

        window.location.href =
            "login.html";

        return;
    }


    const reservationData = {

        userId: userId,

        matchId: matchId,

        ticketCategory: category

    };


    console.log(
        "Reservation data:",
        reservationData
    );


    // Show loading
    button.disabled = true;

    button.textContent = "Reserving...";


    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/v1/reservations`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json",

                        "Authorization":
                            `Bearer ${getToken()}`
                    },

                    body:
                        JSON.stringify(
                            reservationData
                        )
                }
            );


        if (response.status === 401) {

            logout();

            return;
        }


        if (!response.ok) {

            let message =
                "Could not reserve ticket.";

            try {

                const errorData =
                    await response.json();

                if (errorData.message) {

                    message =
                        errorData.message;
                }

            } catch (error) {

                console.error(
                    "Could not parse error:",
                    error
                );
            }


            throw new Error(message);
        }


        const reservation =
            await response.json();


        console.log(
            "Reservation created:",
            reservation
        );


        alert(
            "Ticket reserved successfully!\n\n" +
            "Match: " + match + "\n" +
            "Category: " + category + "\n" +
            "Price: " + price.toLocaleString() + " Toman\n\n" +
            "Please complete payment in your reservations."
        );


        button.textContent = "Reserved";

        button.classList.add("disabled");


        // Redirect to reservations
        window.location.href =
            "reservations.html";


    } catch (error) {

        console.error(
            "Reservation error:",
            error
        );

        alert(
            error.message ||
            "Could not reserve ticket. Please try again."
        );

        button.disabled = false;

        button.textContent = "Reserve";

    }
}


/* =====================================================
   GET USER ID FROM TOKEN
===================================================== */

function getUserIdFromToken() {

    const token = getToken();

    if (!token) {
        return null;
    }

    try {

        const payload =
            token.split(".")[1];

        const decodedPayload =
            payload
                .replace(/-/g, "+")
                .replace(/_/g, "/");

        const decoded =
            JSON.parse(
                decodeURIComponent(
                    atob(decodedPayload)
                        .split("")
                        .map(function (character) {

                            return "%" +
                                (
                                    "00" +
                                    character
                                        .charCodeAt(0)
                                        .toString(16)
                                ).slice(-2);

                        })
                        .join("")
                )
            );

        return decoded.userId ||
            decoded.user_id ||
            decoded.id ||
            decoded.sub;

    } catch (error) {

        console.error(
            "Could not decode JWT:",
            error
        );

        return null;
    }
}


/* =====================================================
   LOGOUT
===================================================== */

function logout() {

    localStorage.removeItem("token");

    localStorage.removeItem("role");

    localStorage.removeItem("email");

    window.location.href =
        "login.html";
}


/* =====================================================
   INITIALIZE
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadResults();

    }
);