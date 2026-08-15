/* =====================================================
   API CONFIGURATION
===================================================== */

const API_BASE_URL = "http://localhost:8081";

const SUPPORT_ENDPOINT = "/api/v1/supports";


/* =====================================================
   DOM ELEMENTS
===================================================== */

const profileForm =
    document.getElementById("adminProfileForm");

const userIdInput =
    document.getElementById("userId");

const firstNameInput =
    document.getElementById("firstName");

const lastNameInput =
    document.getElementById("lastName");

const emailInput =
    document.getElementById("email");

const phoneNumberInput =
    document.getElementById("phoneNumber");

const cityInput =
    document.getElementById("city");

const registrationDateInput =
    document.getElementById("registrationDate");

const fullNameElement =
    document.getElementById("fullName");

const profileImage =
    document.getElementById("profileImage");

const saveChangesBtn =
    document.getElementById("saveChangesBtn");

const cancelEditBtn =
    document.getElementById("cancelEditBtn");

const saveMessage =
    document.getElementById("saveMessage");

const errorMessage =
    document.getElementById("errorMessage");

const logoutBtn =
    document.getElementById("logoutBtn");


/* =====================================================
   ORIGINAL PROFILE
===================================================== */

let originalProfile = null;


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
   GET SUPPORT ID FROM JWT
===================================================== */

function getSupportIdFromToken() {

    const token =
        getToken();


    if (!token) {
        return null;
    }


    const payload =
        decodeJwt(token);


    if (!payload) {

        showError(
            "Invalid login session. Please login again."
        );

        return null;
    }


    /*
     * We support the most common JWT claim names.
     *
     * If your backend stores the UUID in "sub",
     * this will use it.
     */

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


        showError(
            "Could not find your Support ID."
        );


        return null;
    }


    return supportId;
}


/* =====================================================
   AUTH HEADERS
===================================================== */

function getAuthHeaders() {

    const token =
        getToken();


    return {

        "Content-Type":
            "application/json",

        "Authorization":
            `Bearer ${token}`

    };
}


/* =====================================================
   LOAD SUPPORT PROFILE
===================================================== */

async function loadSupportProfile() {

    const supportId =
        getSupportIdFromToken();


    if (!supportId) {
        return;
    }


    try {

        const response =
            await fetch(
                `${API_BASE_URL}${SUPPORT_ENDPOINT}/${supportId}/profile`,
                {
                    method: "GET",

                    headers:
                        getAuthHeaders()
                }
            );


        /* =============================================
           UNAUTHORIZED
        ============================================= */

        if (response.status === 401) {

            logout();

            return;
        }


        /* =============================================
           OTHER ERRORS
        ============================================= */

        if (!response.ok) {

            const errorText =
                await response.text();

            console.error(
                "Support profile error:",
                errorText
            );


            throw new Error(
                "Could not load administrator profile."
            );
        }


        /* =============================================
           RESPONSE
        ============================================= */

        const profile =
            await response.json();


        console.log(
            "Support profile:",
            profile
        );


        originalProfile =
            profile;


        displayProfile(
            profile
        );


    } catch (error) {

        console.error(
            "Load profile error:",
            error
        );


        showError(
            error.message ||
            "Could not load administrator profile."
        );
    }
}


/* =====================================================
   DISPLAY PROFILE
===================================================== */

function displayProfile(profile) {

    /* =============================================
       UUID
    ============================================= */

    userIdInput.value =
        profile.id || "";


    /* =============================================
       FIRST NAME
    ============================================= */

    firstNameInput.value =
        profile.firstname || "";


    /* =============================================
       LAST NAME
    ============================================= */

    lastNameInput.value =
        profile.lastname || "";


    /* =============================================
       EMAIL
    ============================================= */

    emailInput.value =
        profile.email || "";


    /* =============================================
       PHONE
    ============================================= */

    phoneNumberInput.value =
        profile.phoneNumber || "";


    /* =============================================
       CITY
    ============================================= */

    cityInput.value =
        profile.city || "";


    /* =============================================
       REGISTRATION DATE
    ============================================= */

    if (profile.registrationDate) {

        const date =
            new Date(
                profile.registrationDate
            );


        registrationDateInput.value =
            date.toLocaleDateString();

    } else {

        registrationDateInput.value =
            "-";
    }


    /* =============================================
       FULL NAME
    ============================================= */

    updateFullName();


    /* =============================================
       PROFILE IMAGE
    ============================================= */

    if (profile.profileImageUrl) {

        profileImage.src =
            profile.profileImageUrl;

    } else {

        profileImage.src =
            "assets/images/default-profile.jpg";
    }


    hideError();
}


/* =====================================================
   UPDATE FULL NAME
===================================================== */

function updateFullName() {

    const firstname =
        firstNameInput.value.trim();

    const lastname =
        lastNameInput.value.trim();


    const fullName =
        `${firstname} ${lastname}`.trim();


    fullNameElement.textContent =
        fullName || "Administrator";
}


/* =====================================================
   SAVE PROFILE
===================================================== */

profileForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        const supportId =
            userIdInput.value;


        if (!supportId) {

            showError(
                "Support ID is missing."
            );

            return;
        }


        /* =============================================
           GET FORM VALUES
        ============================================= */

        const firstname =
            firstNameInput.value.trim();

        const lastname =
            lastNameInput.value.trim();

        const phoneNumber =
            phoneNumberInput.value.trim();

        const city =
            cityInput.value.trim();


        /* =============================================
           REQUEST BODY

           This matches UpdateProfileDto.

           IMPORTANT:
           email is NOT sent because the backend
           UpdateProfileDto does not contain email.
        ============================================= */

        const requestBody = {

            id: supportId,

            firstname: firstname,

            lastname: lastname,

            phoneNumber: phoneNumber,

            city: city

        };


        console.log(
            "Updating support profile:",
            requestBody
        );


        /* =============================================
           LOADING STATE
        ============================================= */

        saveChangesBtn.disabled =
            true;

        saveChangesBtn.textContent =
            "Saving...";


        hideError();

        hideSaveMessage();


        try {

            /* =========================================
               PUT /api/v1/supports/{supportId}/profile
            ========================================= */

            const response =
                await fetch(
                    `${API_BASE_URL}${SUPPORT_ENDPOINT}/${supportId}/profile`,
                    {
                        method: "PUT",

                        headers:
                            getAuthHeaders(),

                        body:
                            JSON.stringify(
                                requestBody
                            )
                    }
                );


            /* =========================================
               UNAUTHORIZED
            ========================================= */

            if (response.status === 401) {

                logout();

                return;
            }


            /* =========================================
               BACKEND ERROR
            ========================================= */

            if (!response.ok) {

                let message =
                    "Could not save profile.";


                try {

                    const errorData =
                        await response.json();


                    console.error(
                        "Backend error:",
                        errorData
                    );


                    if (errorData.message) {

                        message =
                            errorData.message;
                    }


                } catch (error) {

                    console.error(
                        "Could not parse backend error:",
                        error
                    );
                }


                throw new Error(
                    message
                );
            }


            /* =========================================
               UPDATED PROFILE
            ========================================= */

            const updatedProfile =
                await response.json();


            console.log(
                "Updated support profile:",
                updatedProfile
            );


            originalProfile =
                updatedProfile;


            displayProfile(
                updatedProfile
            );


            showSaveMessage(
                "Changes saved successfully."
            );


        } catch (error) {

            console.error(
                "Update profile error:",
                error
            );


            showError(
                error.message ||
                "Could not save changes."
            );


        } finally {

            saveChangesBtn.disabled =
                false;

            saveChangesBtn.textContent =
                "Save Changes";
        }

    }
);


/* =====================================================
   CANCEL CHANGES
===================================================== */

cancelEditBtn.addEventListener(
    "click",
    function () {

        if (!originalProfile) {
            return;
        }


        displayProfile(
            originalProfile
        );


        hideError();

        hideSaveMessage();
    }
);


/* =====================================================
   LIVE NAME UPDATE
===================================================== */

firstNameInput.addEventListener(
    "input",
    updateFullName
);


lastNameInput.addEventListener(
    "input",
    updateFullName
);


/* =====================================================
   SUCCESS MESSAGE
===================================================== */

function showSaveMessage(message) {

    saveMessage.textContent =
        message;


    saveMessage.style.display =
        "block";


    setTimeout(
        function () {

            hideSaveMessage();

        },
        3000
    );
}


/* =====================================================
   HIDE SUCCESS MESSAGE
===================================================== */

function hideSaveMessage() {

    saveMessage.style.display =
        "none";
}


/* =====================================================
   ERROR MESSAGE
===================================================== */

function showError(message) {

    errorMessage.textContent =
        message;


    errorMessage.style.display =
        "block";
}


/* =====================================================
   HIDE ERROR
===================================================== */

function hideError() {

    errorMessage.style.display =
        "none";
}


/* =====================================================
   LOGOUT
===================================================== */

function logout() {

    localStorage.removeItem(
        "token"
    );


    localStorage.removeItem(
        "role"
    );


    localStorage.removeItem(
        "email"
    );


    window.location.href =
        "login.html";
}


logoutBtn.addEventListener(
    "click",
    logout
);


/* =====================================================
   INITIALIZE
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadSupportProfile();

    }
);