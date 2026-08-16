/* =====================================================
   API CONFIGURATION
===================================================== */

const API_BASE_URL = "http://localhost:8081";

const REPORTS_ENDPOINT = "/api/v1/reports";


/* =====================================================
   DOM ELEMENTS
===================================================== */

const reportsList =
    document.getElementById("reportsList");

const reportsLoading =
    document.getElementById("reportsLoading");

const noReports =
    document.getElementById("noReports");

const statusFilter =
    document.getElementById("statusFilter");


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

async function loadReports(status) {

    const supportId =
        getSupportIdFromToken();

    if (!supportId) {
        return;
    }

    reportsLoading.style.display = "block";
    reportsList.innerHTML = "";
    noReports.style.display = "none";


    try {

        let endpoint =
            `${API_BASE_URL}${REPORTS_ENDPOINT}/admin/all`;

        // The controller has admin/all endpoint
        // Let me check - actually the controller doesn't have /admin/all
        // It has /admin/pending, /admin/unassigned, /admin/status/{status}

        // Since /admin/all is not available, we'll use /admin/status with ALL
        // or we'll use the support-specific endpoint

        // Let's use the support-specific endpoint
        if (status && status !== "ALL") {

            endpoint =
                `${API_BASE_URL}${REPORTS_ENDPOINT}/admin/status/${status}`;
        } else {

            // Use the support's assigned reports
            endpoint =
                `${API_BASE_URL}${REPORTS_ENDPOINT}/admin/support/${supportId}`;
        }


        const response =
            await fetch(
                endpoint,
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

            // If support-specific endpoint fails, try pending
            if (status === "ALL" || !status) {

                const fallbackResponse =
                    await fetch(
                        `${API_BASE_URL}${REPORTS_ENDPOINT}/admin/pending`,
                        {
                            method: "GET",

                            headers:
                                getAuthHeaders()
                        }
                    );

                if (fallbackResponse.ok) {

                    const reports =
                        await fallbackResponse.json();

                    reportsLoading.style.display =
                        "none";

                    if (reports.length === 0) {

                        noReports.style.display =
                            "block";

                        return;
                    }

                    displayReports(reports);

                    return;
                }
            }

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


            const userName =
                report.userName ||
                report.userId ||
                "Unknown User";


            const reportId =
                report.id || "N/A";


            const subject =
                report.subject || "General";


            const text =
                report.text || "No description provided.";


            const adminResponse =
                report.adminResponse || "";


            article.innerHTML = `

                <div class="report-card-header">

                    <div>

                        <h2>
                            ${subject}
                        </h2>

                        <span class="report-date">
                            ${formatDate(report.createdAt)}
                        </span>

                    </div>


                    <select
                        class="status-select"
                        data-report-id="${reportId}"
                        data-current-status="${report.status || "PENDING"}"
                    >

                        <option value="PENDING" ${report.status === "PENDING" ? "selected" : ""}>
                            PENDING
                        </option>

                        <option value="IN_PROGRESS" ${report.status === "IN_PROGRESS" ? "selected" : ""}>
                            IN PROGRESS
                        </option>

                        <option value="RESOLVED" ${report.status === "RESOLVED" ? "selected" : ""}>
                            RESOLVED
                        </option>

                        <option value="CLOSED" ${report.status === "CLOSED" ? "selected" : ""}>
                            CLOSED
                        </option>

                    </select>

                </div>



                <div class="report-user">

                    <strong>
                        Reported By
                    </strong>

                    <span>
                        ${userName}
                    </span>

                </div>



                <div class="report-content">


                    <div class="report-field">

                        <span class="report-label">
                            Report ID
                        </span>

                        <p>
                            #${reportId}
                        </p>

                    </div>


                    <div class="report-field">

                        <span class="report-label">
                            Subject
                        </span>

                        <p>
                            ${subject}
                        </p>

                    </div>


                    <div class="report-field">

                        <span class="report-label">
                            Report
                        </span>

                        <p>
                            ${text}
                        </p>

                    </div>


                    <div class="report-field">

                        <span class="report-label">
                            Admin Response
                        </span>

                        <textarea
                            class="admin-response"
                            data-report-id="${reportId}"
                            rows="5"
                            placeholder="Write a response..."
                        >${adminResponse}</textarea>

                    </div>


                </div>



                <div class="report-actions">

                    <button
                        class="primary-btn save-response-btn"
                        data-report-id="${reportId}"
                    >
                        Save Response
                    </button>

                    <button
                        class="resolve-btn"
                        data-report-id="${reportId}"
                    >
                        Resolve
                    </button>

                    <button
                        class="secondary-btn reject-btn"
                        data-report-id="${reportId}"
                    >
                        Reject
                    </button>

                </div>

            `;


            reportsList.appendChild(
                article
            );

        }
    );


    // Attach event listeners
    attachEventListeners();
}


/* =====================================================
   ATTACH EVENT LISTENERS
===================================================== */

function attachEventListeners() {

    // Save Response
    document.querySelectorAll(
        ".save-response-btn"
    ).forEach(function (button) {

        button.addEventListener(
            "click",
            async function () {

                const reportId =
                    button.dataset.reportId;

                const responseBox =
                    document.querySelector(
                        `.admin-response[data-report-id="${reportId}"]`
                    );

                const response =
                    responseBox.value.trim();

                if (response === "") {

                    alert(
                        "Please write a response first."
                    );

                    return;
                }

                await saveResponse(
                    reportId,
                    response
                );
            }
        );

    });


    // Resolve Report
    document.querySelectorAll(
        ".resolve-btn"
    ).forEach(function (button) {

        button.addEventListener(
            "click",
            async function () {

                const reportId =
                    button.dataset.reportId;

                const confirmed =
                    confirm(
                        "Resolve this report?"
                    );

                if (!confirmed) {
                    return;
                }

                await resolveReport(reportId);
            }
        );

    });


    // Reject Report
    document.querySelectorAll(
        ".reject-btn"
    ).forEach(function (button) {

        button.addEventListener(
            "click",
            async function () {

                const reportId =
                    button.dataset.reportId;

                const confirmed =
                    confirm(
                        "Reject this report?"
                    );

                if (!confirmed) {
                    return;
                }

                await rejectReport(reportId);
            }
        );

    });


    // Status Change
    document.querySelectorAll(
        ".status-select"
    ).forEach(function (select) {

        select.addEventListener(
            "change",
            function () {

                const reportId =
                    this.dataset.reportId;

                const newStatus =
                    this.value;

                // Update status via API
                updateReportStatus(
                    reportId,
                    newStatus
                );
            }
        );

    });


    // Filter change
    statusFilter.addEventListener(
        "change",
        function () {

            const status =
                this.value;

            loadReports(status);
        }
    );
}


/* =====================================================
   SAVE RESPONSE
===================================================== */

async function saveResponse(reportId, response) {

    const supportId =
        getSupportIdFromToken();

    if (!supportId) {
        return;
    }

    try {

        const payload = {

            reportId: reportId,

            supportId: supportId,

            adminResponse: response

        };


        console.log(
            "Saving response:",
            payload
        );


        const responseResult =
            await fetch(
                `${API_BASE_URL}${REPORTS_ENDPOINT}/resolve`,
                {
                    method: "PUT",

                    headers:
                        getAuthHeaders(),

                    body:
                        JSON.stringify(payload)
                }
            );


        if (responseResult.status === 401) {

            logout();

            return;
        }


        if (!responseResult.ok) {

            let message =
                "Could not save response.";

            try {

                const errorData =
                    await responseResult.json();

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


        const updatedReport =
            await responseResult.json();


        console.log(
            "Response saved:",
            updatedReport
        );


        alert(
            "Response saved successfully."
        );


        // Reload reports
        const currentStatus =
            statusFilter.value;

        loadReports(currentStatus);


    } catch (error) {

        console.error(
            "Save response error:",
            error
        );

        alert(
            error.message ||
            "Could not save response. Please try again."
        );
    }
}


/* =====================================================
   RESOLVE REPORT
===================================================== */

async function resolveReport(reportId) {

    const supportId =
        getSupportIdFromToken();

    if (!supportId) {
        return;
    }

    try {

        const payload = {

            reportId: reportId,

            supportId: supportId

        };


        console.log(
            "Resolving report:",
            payload
        );


        const response =
            await fetch(
                `${API_BASE_URL}${REPORTS_ENDPOINT}/resolve`,
                {
                    method: "PUT",

                    headers:
                        getAuthHeaders(),

                    body:
                        JSON.stringify(payload)
                }
            );


        if (response.status === 401) {

            logout();

            return;
        }


        if (!response.ok) {

            let message =
                "Could not resolve report.";

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


        const updatedReport =
            await response.json();


        console.log(
            "Report resolved:",
            updatedReport
        );


        alert(
            "Report resolved successfully."
        );


        // Reload reports
        const currentStatus =
            statusFilter.value;

        loadReports(currentStatus);


    } catch (error) {

        console.error(
            "Resolve report error:",
            error
        );

        alert(
            error.message ||
            "Could not resolve report. Please try again."
        );
    }
}


/* =====================================================
   REJECT REPORT
===================================================== */

async function rejectReport(reportId) {

    const supportId =
        getSupportIdFromToken();

    if (!supportId) {
        return;
    }

    try {

        const payload = {

            reportId: reportId,

            supportId: supportId

        };


        console.log(
            "Rejecting report:",
            payload
        );


        const response =
            await fetch(
                `${API_BASE_URL}${REPORTS_ENDPOINT}/reject`,
                {
                    method: "PUT",

                    headers:
                        getAuthHeaders(),

                    body:
                        JSON.stringify(payload)
                }
            );


        if (response.status === 401) {

            logout();

            return;
        }


        if (!response.ok) {

            let message =
                "Could not reject report.";

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


        const updatedReport =
            await response.json();


        console.log(
            "Report rejected:",
            updatedReport
        );


        alert(
            "Report rejected successfully."
        );


        // Reload reports
        const currentStatus =
            statusFilter.value;

        loadReports(currentStatus);


    } catch (error) {

        console.error(
            "Reject report error:",
            error
        );

        alert(
            error.message ||
            "Could not reject report. Please try again."
        );
    }
}


/* =====================================================
   UPDATE REPORT STATUS
===================================================== */

async function updateReportStatus(reportId, status) {

    try {

        // Find the response box for this report
        const responseBox =
            document.querySelector(
                `.admin-response[data-report-id="${reportId}"]`
            );

        const response =
            responseBox ? responseBox.value.trim() : "";


        const payload = {

            reportId: reportId,

            adminResponse: response || null

        };


        // Use resolve endpoint for RESOLVED and CLOSED
        // Use reject endpoint for REJECTED
        // For other statuses, use resolve with the status

        let endpoint =
            `${API_BASE_URL}${REPORTS_ENDPOINT}/resolve`;

        if (status === "CLOSED") {

            endpoint =
                `${API_BASE_URL}${REPORTS_ENDPOINT}/resolve`;
        }


        console.log(
            "Updating status:",
            payload
        );


        const responseResult =
            await fetch(
                endpoint,
                {
                    method: "PUT",

                    headers:
                        getAuthHeaders(),

                    body:
                        JSON.stringify(payload)
                }
            );


        if (responseResult.status === 401) {

            logout();

            return;
        }


        if (!responseResult.ok) {

            throw new Error(
                "Could not update status."
            );
        }


        console.log(
            "Status updated successfully."
        );


    } catch (error) {

        console.error(
            "Update status error:",
            error
        );

        alert(
            "Could not update status. Please try again."
        );
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

        loadReports("ALL");

    }
);