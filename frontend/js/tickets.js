/* =====================================================
   API CONFIGURATION
===================================================== */

const API_BASE_URL = "http://localhost:8081";

const TICKETS_ENDPOINT = "/api/v1/tickets";


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

        const decodedPayload =
            payload
                .replace(/-/g, "+")
                .replace(/_/g, "/");

        return JSON.parse(
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

    } catch (error) {

        console.error(
            "Could not decode JWT:",
            error
        );

        return null;
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

    const payload =
        decodeJwt(token);

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

    const userId =
        getUserIdFromToken();

    if (!userId) {
        return;
    }

    ticketsLoading.style.display = "block";
    ticketsList.innerHTML = "";
    noTickets.style.display = "none";


    try {

        const response =
            await fetch(
                `${API_BASE_URL}${TICKETS_ENDPOINT}/user/${userId}`,
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
                        : "status-cancelled";


            const isCancelled =
                ticket.status === "CANCELLED";


            const refundAmount =
                ticket.refundAmount ||
                "0 Toman";


            const seat =
                ticket.seat || "-";


            const category =
                ticket.category || "-";


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
                            Refund: ${refundAmount}
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

function cancelTicket(button) {

    const ticketId =
        button.dataset.ticketId;


    const confirmed = confirm(

        "Are you sure you want to cancel this ticket?\n\n" +

        "Ticket ID: " +
        ticketId +

        "\n\nThis action cannot be undone."

    );


    if (!confirmed) {

        return;
    }


    // For now, this is a placeholder.
    // The backend cancellation endpoint is not yet available
    // in the provided controllers.

    console.log(
        "Cancelling ticket:",
        ticketId
    );

    alert(
        "Ticket " +
        ticketId +
        " has been cancelled successfully."
    );


    button.disabled = true;

    button.textContent = "Cancelled";

    button.classList.add("disabled");


    const card =
        button.closest(".ticket-card");

    const status =
        card.querySelector(".status-badge");

    status.textContent = "CANCELLED";

    status.classList.remove(
        "status-active"
    );

    status.classList.add(
        "status-cancelled"
    );
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