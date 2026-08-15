/* =====================================================
   API CONFIGURATION
===================================================== */

const API_BASE_URL = "http://localhost:8081";

const REPORTS_ENDPOINT = "/api/v1/reports";


/* =====================================================
   DOM ELEMENTS
===================================================== */

const addReportBtn =
    document.getElementById("addReportBtn");

const newReportForm =
    document.getElementById("newReportForm");

const closeReportForm =
    document.getElementById("closeReportForm");

const reportForm =
    document.getElementById("reportForm");

const reportsList =
    document.getElementById("reportsList");

const reportsLoading =
    document.getElementById("reportsLoading");

const noReports =
    document.getElementById("noReports");


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
   LOAD REPORTS
===================================================== */

async function loadReports() {

    const userId =
        getUserIdFromToken();

    if (!userId) {
        return;
    }

    reportsLoading.style.display = "block";
    reportsList.innerHTML = "";
    noReports.style.display = "none";


    try {

        const response =
            await fetch(
                `${API_BASE_URL}${REPORTS_ENDPOINT}/user/${userId}`,
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
                "Could not load reports."
            );
        }


        const reports =
            await response.json();


        console.log(
            "Reports:",
            reports
        );


        reportsLoading.style.display =
            "none";


        if (reports.length === 0) {

            noReports.style.display =
                "block";

            return;
        }


        displayReports(reports);


    } catch (error) {

        console.error(
            "Load reports error:",
            error
        );

        reportsLoading.textContent =
            "Could not load reports. Please try again.";

        reportsLoading.style.color =
            "#e74c3c";
    }
}


/* =====================================================
   DISPLAY REPORTS
===================================================== */

function displayReports(reports) {

    reportsList.innerHTML = "";


    reports.forEach(
        function (report) {

            const article =
                document.createElement(
                    "article"
                );

            article.className =
                "report-card";


            const statusClass =
                report.status === "PENDING"
                    ? "status-pending"
                    : report.status === "IN_PROGRESS"
                        ? "status-in-progress"
                        : report.status === "RESOLVED"
                            ? "status-resolved"
                            : "status-closed";


            let responseHtml =
                `<p class="no-response">No response yet.</p>`;

            if (report.adminResponse) {

                responseHtml =
                    `<p>${report.adminResponse}</p>`;

            }


            article.innerHTML = `

                <div class="report-card-header">

                    <div>

                        <h2>
                            ${report.subject || "Report"}
                        </h2>

                        <span class="report-date">
                            ${formatDate(report.createdAt)}
                        </span>

                    </div>

                    <span class="status-badge ${statusClass}">
                        ${report.status || "PENDING"}
                    </span>

                </div>


                <div class="report-content">

                    <div class="report-field">

                        <span class="report-label">
                            Subject
                        </span>

                        <p>
                            ${report.subject || "-"}
                        </p>

                    </div>


                    <div class="report-field">

                        <span class="report-label">
                            Report
                        </span>

                        <p>
                            ${report.text || "-"}
                        </p>

                    </div>


                    <div class="report-field">

                        <span class="report-label">
                            Admin Response
                        </span>

                        ${responseHtml}

                    </div>

                </div>

            `;


            reportsList.appendChild(
                article
            );

        }
    );
}


/* =====================================================
   SUBMIT REPORT
===================================================== */

reportForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        const subject =
            document.getElementById(
                "reportSubject"
            ).value;

        const text =
            document.getElementById(
                "reportText"
            ).value.trim();


        if (!subject || !text) {

            alert(
                "Please fill in all fields."
            );

            return;
        }


        const userId =
            getUserIdFromToken();

        if (!userId) {
            return;
        }


        const submitBtn =
            document.querySelector(
                ".submit-report-btn"
            );

        submitBtn.disabled = true;
        submitBtn.textContent =
            "Submitting...";


        try {

            const response =
                await fetch(
                    `${API_BASE_URL}${REPORTS_ENDPOINT}`,
                    {
                        method: "POST",

                        headers:
                            getAuthHeaders(),

                        body:
                            JSON.stringify({
                                userId: userId,
                                subject: subject,
                                text: text
                            })
                    }
                );


            if (response.status === 401) {

                logout();

                return;
            }


            if (!response.ok) {

                let message =
                    "Could not submit report.";

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


                throw new Error(
                    message
                );
            }


            const newReport =
                await response.json();


            console.log(
                "Report submitted:",
                newReport
            );


            alert(
                "Your report has been submitted successfully."
            );


            reportForm.reset();

            newReportForm.style.display =
                "none";


            // Reload reports
            loadReports();


        } catch (error) {

            console.error(
                "Submit report error:",
                error
            );

            alert(
                error.message ||
                "Could not submit report. Please try again."
            );

        } finally {

            submitBtn.disabled = false;
            submitBtn.textContent =
                "Submit Report";
        }

    }
);


/* =====================================================
   OPEN NEW REPORT FORM
===================================================== */

addReportBtn.addEventListener(
    "click",
    function () {

        newReportForm.style.display =
            "block";

        newReportForm.scrollIntoView({
            behavior: "smooth"
        });

    }
);


/* =====================================================
   CLOSE NEW REPORT FORM
===================================================== */

closeReportForm.addEventListener(
    "click",
    function () {

        newReportForm.style.display =
            "none";

        reportForm.reset();

    }
);


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

        loadReports();

    }
);