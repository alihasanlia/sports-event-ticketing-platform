/* =====================================================
   API CONFIGURATION
===================================================== */

const API_BASE_URL = "http://localhost:8081";

const RESERVATIONS_ENDPOINT = "/api/v1/reservations";


/* =====================================================
   DOM ELEMENTS
===================================================== */

const reservationsList =
    document.getElementById("reservationsList");

const reservationsLoading =
    document.getElementById("reservationsLoading");

const emptyReservations =
    document.getElementById("emptyReservations");


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
            day: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        }
    );
}


/* =====================================================
   LOAD RESERVATIONS
===================================================== */

async function loadReservations() {

    const userId =
        getUserIdFromToken();

    if (!userId) {
        return;
    }

    reservationsLoading.style.display = "block";
    reservationsList.innerHTML = "";
    emptyReservations.style.display = "none";


    try {

        const response =
            await fetch(
                `${API_BASE_URL}${RESERVATIONS_ENDPOINT}/user/${userId}`,
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
                "Could not load reservations."
            );
        }


        const reservations =
            await response.json();


        console.log(
            "Reservations:",
            reservations
        );


        reservationsLoading.style.display =
            "none";


        if (reservations.length === 0) {

            emptyReservations.style.display =
                "block";

            return;
        }


        displayReservations(reservations);


    } catch (error) {

        console.error(
            "Load reservations error:",
            error
        );

        reservationsLoading.textContent =
            "Could not load reservations. Please try again.";

        reservationsLoading.style.color =
            "#e74c3c";
    }
}


/* =====================================================
   DISPLAY RESERVATIONS
===================================================== */

function displayReservations(reservations) {

    reservationsList.innerHTML = "";


    reservations.forEach(
        function (reservation, index) {

            const card =
                document.createElement(
                    "article"
                );

            card.className =
                "ticket-card";


            const paymentStatus =
                reservation.paid
                    ? "PAID"
                    : "UNPAID";


            const paymentClass =
                reservation.paid
                    ? "status-paid"
                    : "status-pending";


            card.innerHTML = `

                <div class="ticket-header">

                    <div>

                        <h2>
                            ${reservation.matchName || "Match"}
                        </h2>

                        <p class="ticket-id">
                            Match ID: ${reservation.matchId || "N/A"}
                        </p>

                    </div>


                    <span class="status-badge ${paymentClass}">
                        ${paymentStatus}
                    </span>

                </div>



                <div class="ticket-info">


                    <div class="ticket-info-item">

                        <span class="info-label">
                            Stadium
                        </span>

                        <span>
                            ${reservation.stadium || "-"}
                        </span>

                    </div>



                    <div class="ticket-info-item">

                        <span class="info-label">
                            Ticket Category
                        </span>

                        <span>
                            ${reservation.category || "-"}
                        </span>

                    </div>



                    <div class="ticket-info-item">

                        <span class="info-label">
                            Price
                        </span>

                        <span>
                            ${Number(reservation.price || 0).toLocaleString()} Toman
                        </span>

                    </div>



                    <div class="ticket-info-item">

                        <span class="info-label">
                            Reservation Time
                        </span>

                        <span>
                            ${formatDate(reservation.reservationTime)}
                        </span>

                    </div>


                </div>



                <div class="ticket-footer">


                    <div>

                        <span class="info-label">
                            Payment Status
                        </span>

                        <strong>
                            ${paymentStatus}
                        </strong>

                    </div>



                    ${
                reservation.paid

                    ?

                    `
                                <span class="paid-label">
                                    Payment Completed
                                </span>
                                `

                    :

                    `
                                <button
                                    type="button"
                                    class="primary-btn pay-btn"
                                    data-reservation-id="${reservation.id}"
                                    onclick="payReservation(this)"
                                >
                                    Pay
                                </button>
                                `

            }


                </div>

            `;


            reservationsList.appendChild(
                card
            );

        }
    );
}


/* =====================================================
   PAY RESERVATION
===================================================== */

function payReservation(button) {

    const reservationId =
        button.dataset.reservationId;

    // Redirect to payment page with reservation ID
    window.location.href =
        `payment.html?reservation=${reservationId}`;
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

        loadReservations();

    }
);