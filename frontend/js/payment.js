/* =====================================================
   API CONFIGURATION
===================================================== */

const API_BASE_URL = "http://localhost:8081";

const PAYMENTS_ENDPOINT = "/api/v1/payments";

const RESERVATIONS_ENDPOINT = "/api/v1/reservations";


/* =====================================================
   DOM ELEMENTS
===================================================== */

const paymentForm =
    document.getElementById("paymentForm");

const paymentSuccess =
    document.getElementById("paymentSuccess");

const paymentError =
    document.getElementById("paymentError");

const payButton =
    document.getElementById("payButton");

const matchName =
    document.getElementById("matchName");

const stadiumName =
    document.getElementById("stadiumName");

const ticketCategory =
    document.getElementById("ticketCategory");

const paymentAmount =
    document.getElementById("paymentAmount");


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
   GET URL PARAMETERS
===================================================== */

const urlParams =
    new URLSearchParams(
        window.location.search
    );

const reservationId =
    urlParams.get("reservation");


/* =====================================================
   LOAD RESERVATION
===================================================== */

async function loadReservation() {

    if (!reservationId) {

        matchName.textContent =
            "No reservation specified";

        stadiumName.textContent = "-";

        ticketCategory.textContent = "-";

        paymentAmount.textContent = "-";

        return;
    }

    const userId =
        getUserIdFromToken();

    if (!userId) {
        return;
    }


    try {

        const response =
            await fetch(
                `${API_BASE_URL}${RESERVATIONS_ENDPOINT}/${reservationId}/user`,
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
                "Could not load reservation."
            );
        }


        const reservation =
            await response.json();


        console.log(
            "Reservation:",
            reservation
        );


        matchName.textContent =
            reservation.matchName ||
            reservation.match ||
            "Match";


        stadiumName.textContent =
            reservation.stadium ||
            "-";


        ticketCategory.textContent =
            reservation.category ||
            reservation.ticketCategory ||
            "-";


        const price =
            reservation.price ||
            reservation.amount ||
            0;

        paymentAmount.textContent =
            Number(price).toLocaleString() +
            " Toman";


        // If already paid
        if (reservation.paid) {

            paymentForm.style.display =
                "none";

            paymentSuccess.style.display =
                "block";

            return;
        }


    } catch (error) {

        console.error(
            "Load reservation error:",
            error
        );

        matchName.textContent =
            "Could not load reservation";

        stadiumName.textContent = "-";

        ticketCategory.textContent = "-";

        paymentAmount.textContent = "-";
    }
}


/* =====================================================
   PROCESS PAYMENT
===================================================== */

paymentForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        const cardHolder =
            document.getElementById(
                "cardHolder"
            ).value.trim();

        const cardNumber =
            document.getElementById(
                "cardNumber"
            ).value.trim();

        const expiry =
            document.getElementById(
                "expiry"
            ).value.trim();

        const cvv =
            document.getElementById(
                "cvv"
            ).value.trim();


        // Basic validation
        if (!cardHolder || !cardNumber || !expiry || !cvv) {

            showError(
                "Please fill in all card details."
            );

            return;
        }


        if (cardNumber.length < 16) {

            showError(
                "Please enter a valid card number (16 digits)."
            );

            return;
        }


        const userId =
            getUserIdFromToken();

        if (!userId) {
            return;
        }


        if (!reservationId) {

            showError(
                "No reservation found."
            );

            return;
        }


        // Disable button and show loading
        payButton.disabled = true;

        payButton.textContent = "Processing...";

        hideError();


        try {

            // Create payment first
            const createResponse =
                await fetch(
                    `${API_BASE_URL}${PAYMENTS_ENDPOINT}`,
                    {
                        method: "POST",

                        headers:
                            getAuthHeaders(),

                        body:
                            JSON.stringify({
                                userId: userId,
                                reservationId: reservationId,
                                amount: 0 // Amount will be determined from reservation
                            })
                    }
                );


            if (createResponse.status === 401) {

                logout();

                return;
            }


            if (!createResponse.ok) {

                let message =
                    "Could not initialize payment.";

                try {

                    const errorData =
                        await createResponse.json();

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


            const payment =
                await createResponse.json();


            console.log(
                "Payment created:",
                payment
            );


            // Process payment
            const processResponse =
                await fetch(
                    `${API_BASE_URL}${PAYMENTS_ENDPOINT}/process`,
                    {
                        method: "POST",

                        headers:
                            getAuthHeaders(),

                        body:
                            JSON.stringify({
                                paymentId: payment.id,
                                paymentMethod: "CARD",
                                cardNumber: cardNumber.slice(-4) // Only send last 4 digits
                            })
                    }
                );


            if (processResponse.status === 401) {

                logout();

                return;
            }


            if (!processResponse.ok) {

                let message =
                    "Could not process payment.";

                try {

                    const errorData =
                        await processResponse.json();

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


            const processedPayment =
                await processResponse.json();


            console.log(
                "Payment processed:",
                processedPayment
            );


            // Show success
            paymentForm.style.display =
                "none";

            paymentSuccess.style.display =
                "block";


            // Update reservation status in local storage if needed


        } catch (error) {

            console.error(
                "Payment error:",
                error
            );

            showError(
                error.message ||
                "Payment failed. Please try again."
            );

            payButton.disabled = false;

            payButton.textContent = "Pay Now";
        }
    }
);


/* =====================================================
   SHOW ERROR
===================================================== */

function showError(message) {

    paymentError.textContent =
        message;

    paymentError.style.display =
        "block";
}


/* =====================================================
   HIDE ERROR
===================================================== */

function hideError() {

    paymentError.style.display =
        "none";
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

        loadReservation();

    }
);