/* =====================================================
   API CONFIGURATION
===================================================== */

const API_BASE_URL = "http://localhost:8081";

const TICKETS_ENDPOINT = "/api/v1/tickets";


/* =====================================================
   DOM ELEMENTS
===================================================== */

const ticketIdDisplay =
    document.getElementById("ticketIdDisplay");

const usernameInput =
    document.getElementById("username");

const sportSelect =
    document.getElementById("sport");

const matchInput =
    document.getElementById("match");

const matchDateInput =
    document.getElementById("matchDate");

const matchTimeInput =
    document.getElementById("matchTime");

const stadiumInput =
    document.getElementById("stadium");

const categorySelect =
    document.getElementById("category");

const seatInput =
    document.getElementById("seat");

const priceInput =
    document.getElementById("price");

const statusSelect =
    document.getElementById("status");

const editTicketStatus =
    document.getElementById("editTicketStatus");

const notAvailableMessage =
    document.getElementById("notAvailableMessage");

const editFormSection =
    document.getElementById("editFormSection");


/* =====================================================
   GET TOKEN
===================================================== */

function getToken() {

    const token =
        localStorage.getItem("token");

    if (!token) {

        window.location.href = "login.html";

        return null;
    }

    return token;
}


/* =====================================================
   DECODE JWT
===================================================== */

function decodeJwt(token) {

    try {

        const payload =
            token.split(".")[1];

        const base64 =
            payload
                .replace(/-/g, "+")
                .replace(/_/g, "/");

        const decoded =
            decodeURIComponent(
                atob(base64)
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
            );

        return JSON.parse(decoded);

    } catch (error) {

        console.error(
            "JWT decode error:",
            error
        );

        return null;
    }
}


/* =====================================================
   AUTH HEADERS
===================================================== */

function getAuthHeaders() {

    const token = getToken();

    return {

        "Content-Type": "application/json",

        "Authorization":
            `Bearer ${token}`

    };
}


/* =====================================================
   GET URL PARAMETERS
===================================================== */

const urlParams =
    new URLSearchParams(
        window.location.search
    );

const ticketId =
    urlParams.get("ticketId");


/* =====================================================
   FORMAT DATE
===================================================== */

function formatDateForInput(dateString) {

    if (!dateString) {
        return "";
    }

    const date =
        new Date(dateString);

    if (isNaN(date.getTime())) {
        return "";
    }

    const year =
        date.getFullYear();

    const month =
        String(date.getMonth() + 1)
            .padStart(2, "0");

    const day =
        String(date.getDate())
            .padStart(2, "0");

    return `${year}-${month}-${day}`;
}


/* =====================================================
   FORMAT TIME FOR INPUT
===================================================== */

function formatTimeForInput(timeString) {

    if (!timeString) {
        return "";
    }

    // If it's a full datetime string
    if (timeString.includes("T")) {

        const date =
            new Date(timeString);

        if (isNaN(date.getTime())) {
            return "";
        }

        const hours =
            String(date.getHours())
                .padStart(2, "0");

        const minutes =
            String(date.getMinutes())
                .padStart(2, "0");

        return `${hours}:${minutes}`;
    }

    // If it's just a time string
    return timeString;
}


/* =====================================================
   LOAD TICKET DETAILS
===================================================== */

async function loadTicketDetails() {

    if (!ticketId) {

        ticketIdDisplay.textContent =
            "Ticket ID: Not specified";

        editTicketStatus.textContent =
            "No ticket ID provided.";

        notAvailableMessage.style.display =
            "block";

        editFormSection.style.display =
            "none";

        return;
    }

    ticketIdDisplay.textContent =
        `Ticket ID: #${ticketId}`;

    editTicketStatus.textContent =
        `Loading ticket #${ticketId}...`;


    try {

        const response =
            await fetch(
                `${API_BASE_URL}${TICKETS_ENDPOINT}/${ticketId}/details`,
                {
                    method: "GET",

                    headers:
                        getAuthHeaders()
                }
            );


        if (response.status === 401) {

            logout();

            return;
        }


        if (!response.ok) {

            let message =
                "Could not load ticket details.";

            if (response.status === 404) {

                message =
                    "Ticket not found.";
            }

            throw new Error(message);
        }


        const ticket =
            await response.json();


        console.log(
            "Ticket details:",
            ticket
        );


        // Display ticket details (read-only since no update API)
        displayTicketDetails(ticket);


        // Show the message that editing is not available
        // but still show the ticket data
        notAvailableMessage.style.display =
            "block";

        editFormSection.style.display =
            "block";

        editTicketStatus.textContent =
            `Viewing ticket #${ticketId} (Edit not available)`;


    } catch (error) {

        console.error(
            "Load ticket details error:",
            error
        );

        editTicketStatus.textContent =
            error.message ||
            "Could not load ticket details.";

        notAvailableMessage.style.display =
            "block";

        editFormSection.style.display =
            "none";
    }
}


/* =====================================================
   DISPLAY TICKET DETAILS (READ-ONLY)
===================================================== */

function displayTicketDetails(ticket) {

    // Username
    usernameInput.value =
        ticket.userName ||
        ticket.userId ||
        "Unknown";


    // Sport
    const sport =
        ticket.sport ||
        ticket.sportType ||
        "football";

    const sportLower =
        sport.toLowerCase();

    let sportValue = "football";

    if (sportLower.includes("basket")) {

        sportValue = "basketball";

    } else if (sportLower.includes("volley")) {

        sportValue = "volleyball";

    }

    sportSelect.value = sportValue;


    // Match
    matchInput.value =
        ticket.matchName ||
        ticket.match ||
        "-";


    // Date
    const dateValue =
        ticket.matchDate ||
        ticket.date;

    matchDateInput.value =
        formatDateForInput(dateValue);


    // Time
    const timeValue =
        ticket.matchTime ||
        ticket.time;

    matchTimeInput.value =
        formatTimeForInput(timeValue);


    // Stadium
    stadiumInput.value =
        ticket.stadium ||
        "-";


    // Category
    const category =
        ticket.category ||
        ticket.ticketCategory ||
        "Regular";

    categorySelect.value = category;


    // Seat
    seatInput.value =
        ticket.seat ||
        "-";


    // Price
    priceInput.value =
        ticket.price ||
        0;


    // Status
    const status =
        ticket.status ||
        "ACTIVE";

    if (statusSelect.querySelector(
        `option[value="${status}"]`
    )) {

        statusSelect.value = status;
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

        loadTicketDetails();

    }
);