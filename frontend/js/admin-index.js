/* =====================================================
   API CONFIGURATION
===================================================== */

const API_BASE_URL = "http://localhost:8081";


/* =====================================================
   DOM ELEMENTS
===================================================== */

const ticketCount =
    document.getElementById("ticketCount");

const reportCount =
    document.getElementById("reportCount");

const welcomeMessage =
    document.getElementById("welcomeMessage");


/* =====================================================
   GET TOKEN
===================================================== */

function getToken() {

    const token =
        localStorage.getItem("token");

    if (!token) {

        window.location.href =
            "login.html";

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

        "Content-Type":
            "application/json",

        "Authorization":
            `Bearer ${token}`

    };
}


/* =====================================================
   LOAD DASHBOARD STATS
===================================================== */

async function loadDashboardStats() {

    // Check if user is authenticated
    const token = getToken();

    if (!token) {
        return;
    }

    // Decode token to get user info
    const payload = decodeJwt(token);

    if (payload && payload.firstname) {

        welcomeMessage.textContent =
            `Welcome Back, ${payload.firstname}`;
    }

    try {

        // Get ticket count - using tickets API
        const ticketResponse =
            await fetch(
                `${API_BASE_URL}/api/v1/tickets`,
                {
                    method: "GET",

                    headers:
                        getAuthHeaders()
                }
            );

        if (ticketResponse.status === 401) {

            logout();

            return;
        }

        if (ticketResponse.ok) {

            const tickets =
                await ticketResponse.json();

            ticketCount.textContent =
                tickets.length || 0;
        }


        // Get report count
        const reportResponse =
            await fetch(
                `${API_BASE_URL}/api/v1/reports/admin/all`,
                {
                    method: "GET",

                    headers:
                        getAuthHeaders()
                }
            );

        if (reportResponse.status === 401) {

            logout();

            return;
        }

        if (reportResponse.ok) {

            const reports =
                await reportResponse.json();

            reportCount.textContent =
                reports.length || 0;
        }

    } catch (error) {

        console.error(
            "Load dashboard stats error:",
            error
        );

        // Keep default values
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

        loadDashboardStats();

    }
);