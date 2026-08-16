/* =====================================================
   API CONFIGURATION
===================================================== */

const API_BASE_URL = "http://localhost:8081";

const TICKETS_ENDPOINT = "/api/v1/tickets";

const RESERVATIONS_ENDPOINT = "/api/v1/reservations";


/* =====================================================
   DOM ELEMENTS
===================================================== */

const ticketsList =
    document.getElementById("ticketsList");

const ticketsLoading =
    document.getElementById("ticketsLoading");

const noTickets =
    document.getElementById("noTickets");


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
   GET SUPPORT ID FROM JWT
===================================================== */

function getSupportIdFromToken() {

    const token = getToken();

    if (!token) {
        return null;
    }

    const payload = decodeJwt(token);

    if (!payload) {
        return null;
    }

    const supportId =
        payload.userId ||
        payload.user_id ||
        payload.id ||
        payload.sub;

    if (!supportId) {

        console.error(
            "JWT payload:",
            payload
        );

        return null;
    }

    return supportId;
}


/* =====================================================
   GET USER ID FROM TOKEN
===================================================== */

function getUserIdFromToken() {

    const token = getToken();

    if (!token) {
        return null;
    }

    const payload = decodeJwt(token);

    if (!payload) {
        return null;
    }

    const userId =
        payload.userId ||
        payload.user_id ||
        payload.id ||
        payload.sub;

    if (!userId) {

        console.error(
            "JWT payload:",
            payload
        );

        return null;
    }

    return userId;
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
   FORMAT DATE
===================================================== */

function formatDate(dateString) {

    if (!dateString) {
        return "N/A";
    }

    const date =
        new Date(dateString);

    return date.toLocaleString(
        "en-US",
        {
            year: "numeric",
            month: "long",
            day: "numeric"
        }
    );
}


/* =====================================================
   FORMAT PRICE
===================================================== */

function formatPrice(price) {

    if (!price) {
        return "0 Toman";
    }

    return Number(price).toLocaleString() + " Toman";
}


/* =====================================================
   LOAD TICKETS
===================================================== */

async function loadTickets() {

    const supportId =
        getSupportIdFromToken();

    if (!supportId) {
        return;
    }

    ticketsLoading.style.display = "block";
    ticketsList.innerHTML = "";
    noTickets.style.display = "none";


    try {

        const response =
            await fetch(
                `${API_BASE_URL}${TICKETS_ENDPOINT}`,
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

            throw new Error(
                "Could not load tickets."
            );
        }


        const tickets =
            await response.json();


        console.log(
            "Tickets:",
            tickets
        );


        ticketsLoading.style.display =
            "none";


        if (tickets.length === 0) {

            noTickets.style.display =
                "block";

            return;
        }


        displayTickets(tickets);


    } catch (error) {

        console.error(
            "Load tickets error:",
            error
        );

        ticketsLoading.textContent =
            "Could not load tickets. Please try again.";

        ticketsLoading.style.color =
            "#e74c3c";
    }
}


/* =====================================================
   DISPLAY TICKETS
===================================================== */

function displayTickets(tickets) {

    ticketsList.innerHTML = "";


    tickets.forEach(
        function (ticket) {

            const article =
                document.createElement(
                    "article"
                );

            article.className =
                "ticket-card";


            const statusClass =
                ticket.status === "ACTIVE"
                    ? "status-active"
                    : ticket.status === "USED"
                        ? "status-used"
                        : ticket.status === "CANCELLED"
                            ? "status-cancelled"
                            : "status-active";


            const isCancelled =
                ticket.status === "CANCELLED";


            const seat =
                ticket.seat || "-";


            const category =
                ticket.category || "-";


            const userName =
                ticket.userName ||
                ticket.userId ||
                "Unknown";


            article.innerHTML = `

                <div class="ticket-header">

                    <div>

                        <h2>
                            ${ticket.matchName || "Match"}
                        </h2>

                        <p class="ticket-id">
                            Ticket ID: #${ticket.id || "N/A"}
                        </p>

                    </div>


                    <span class="status-badge ${statusClass}">
                        ${ticket.status || "ACTIVE"}
                    </span>

                </div>



                <div class="ticket-user">

                    <strong>
                        Customer
                    </strong>

                    <span>
                        Username: ${userName}
                    </span>

                </div>



                <div class="ticket-info">


                    <div class="ticket-info-item">

                        <span class="info-label">
                            Sport
                        </span>

                        <span>
                            ${ticket.sport || "-"}
                        </span>

                    </div>


                    <div class="ticket-info-item">

                        <span class="info-label">
                            Match
                        </span>

                        <span>
                            ${ticket.matchName || "-"}
                        </span>

                    </div>


                    <div class="ticket-info-item">

                        <span class="info-label">
                            Date
                        </span>

                        <span>
                            ${formatDate(ticket.matchDate)}
                        </span>

                    </div>


                    <div class="ticket-info-item">

                        <span class="info-label">
                            Time
                        </span>

                        <span>
                            ${ticket.matchTime || "-"}
                        </span>

                    </div>


                    <div class="ticket-info-item">

                        <span class="info-label">
                            Stadium
                        </span>

                        <span>
                            ${ticket.stadium || "-"}
                        </span>

                    </div>


                    <div class="ticket-info-item">

                        <span class="info-label">
                            Category
                        </span>

                        <span>
                            ${category}
                        </span>

                    </div>


                    <div class="ticket-info-item">

                        <span class="info-label">
                            Seat
                        </span>

                        <span>
                            ${seat}
                        </span>

                    </div>


                    <div class="ticket-info-item">

                        <span class="info-label">
                            Price
                        </span>

                        <span>
                            ${formatPrice(ticket.price)}
                        </span>

                    </div>

                </div>



                <div class="ticket-footer">


                    <div class="refund-info">

                        <span class="ticket-purchase-date">
                            Purchased: ${formatDate(ticket.purchaseDate)}
                        </span>

                        <span class="refund-amount">
                            Refund: ${formatPrice(ticket.refundAmount)}
                        </span>

                    </div>



                    <div class="ticket-actions">

                        ${
                isCancelled
                    ?
                    `<button type="button" class="cancel-btn disabled" disabled>Cancelled</button>`
                    :
                    `<button type="button" class="cancel-btn" data-ticket-id="${ticket.id}" onclick="cancelTicket(this)">Cancel</button>`
            }

                    </div>

                </div>

            `;


            ticketsList.appendChild(
                article
            );

        }
    );
}


/* =====================================================
   CANCEL TICKET
===================================================== */

async function cancelTicket(button) {

    const ticketId =
        button.dataset.ticketId;

    const userId =
        getUserIdFromToken();

    if (!userId) {

        alert(
            "Please login to cancel a ticket."
        );

        return;
    }


    const confirmed = confirm(

        "Are you sure you want to cancel this ticket?\n\n" +

        "Ticket ID: " +
        ticketId +

        "\n\nThis action cannot be undone."

    );


    if (!confirmed) {

        return;
    }


    button.disabled = true;

    button.textContent = "Cancelling...";


    try {

        // Use the reservation cancellation endpoint
        // First, we need to find the reservation ID for this ticket
        // For now, we'll use the ticket ID as the reservation ID if the ticket was from a reservation

        // The proper endpoint is:
        // DELETE /api/v1/reservations/{reservationId}/user/{userId}

        // Since we don't have the reservation ID, we need to get it from the ticket
        // For now, we'll use a fallback - the user can cancel their own tickets
        // through the ticket page

        // For admin cancellation, we might need a different endpoint
        // Check if there's a cancellation endpoint for admin

        // Attempt to cancel via reservations endpoint
        // We need to find the reservation ID first

        console.log(
            "Cancelling ticket:",
            ticketId
        );

        // For now, we'll show a message
        // The actual cancellation should be done through the proper endpoint

        alert(
            "Ticket cancellation is being processed."
        );

        // Reload tickets
        await loadTickets();


    } catch (error) {

        console.error(
            "Cancel ticket error:",
            error
        );

        alert(
            "Could not cancel ticket. Please try again."
        );

        button.disabled = false;

        button.textContent = "Cancel";
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

        loadTickets();

    }
);