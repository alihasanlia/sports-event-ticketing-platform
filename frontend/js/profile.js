/* =====================================================
   API CONFIGURATION
===================================================== */

const API_BASE_URL = "http://localhost:8081";

const PROFILE_ENDPOINT = "/api/v1/users";


/* =====================================================
   DOM ELEMENTS
===================================================== */

const profileForm =
    document.getElementById("userProfileForm");

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

const userIdInput =
    document.getElementById("userId");

const registrationDateInput =
    document.getElementById("registrationDate");

const fullNameElement =
    document.getElementById("fullName");

const profileImage =
    document.getElementById("profileImage");

const saveMessage =
    document.getElementById("saveMessage");

const errorMessage =
    document.getElementById("errorMessage");

const saveChangesBtn =
    document.getElementById("saveChangesBtn");

const cancelEditBtn =
    document.getElementById("cancelEditBtn");

const logoutBtn =
    document.getElementById("logoutBtn");


/* =====================================================
   ORIGINAL PROFILE DATA
===================================================== */

let originalProfile = null;


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

        showError(
            "Invalid login session. Please login again."
        );

        return null;
    }


    /*
     * Different JWT implementations may
     * store the user UUID under different
     * claim names.
     */

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

        showError(
            "Could not find your user ID in the login token."
        );

        return null;
    }


    return userId;
}


/* =====================================================
   AUTHORIZATION HEADERS
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
   GET USER PROFILE
===================================================== */

async function loadUserProfile() {

    const userId =
        getUserIdFromToken();

    if (!userId) {
        return;
    }


    try {

        const response =
            await fetch(
                `${API_BASE_URL}${PROFILE_ENDPOINT}/${userId}/profile`,
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

            const errorText =
                await response.text();

            console.error(
                "Profile request failed:",
                errorText
            );

            throw new Error(
                "Could not load your profile."
            );
        }


        const profile =
            await response.json();


        console.log(
            "Profile received:",
            profile
        );


        originalProfile = profile;


        displayProfile(profile);


    } catch (error) {

        console.error(
            "Load profile error:",
            error
        );

        showError(
            error.message ||
            "Could not load your profile."
        );
    }
}


/* =====================================================
   DISPLAY PROFILE
===================================================== */

function displayProfile(profile) {

    /*
     * Save UUID
     */

    userIdInput.value =
        profile.id || "";


    /*
     * First name
     */

    firstNameInput.value =
        profile.firstname || "";


    /*
     * Last name
     */

    lastNameInput.value =
        profile.lastname || "";


    /*
     * Email
     */

    emailInput.value =
        profile.email || "";


    /*
     * Phone
     */

    phoneNumberInput.value =
        profile.phoneNumber || "";


    /*
     * City
     */

    cityInput.value =
        profile.city || "";


    /*
     * Registration date
     */

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


    /*
     * Full name
     */

    updateFullName();


    /*
     * Profile image
     */

    if (profile.profileImageUrl) {

        profileImage.src =
            profile.profileImageUrl;

    } else {

        profileImage.src =
            "assets/images/default-profile.jpg";
    }


    /*
     * Remove loading state
     */

    hideError();
}


/* =====================================================
   UPDATE FULL NAME
===================================================== */

function updateFullName() {

    const firstName =
        firstNameInput.value.trim();

    const lastName =
        lastNameInput.value.trim();


    const fullName =
        `${firstName} ${lastName}`.trim();


    fullNameElement.textContent =
        fullName || "User";
}


/* =====================================================
   SAVE PROFILE
===================================================== */

profileForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        const userId =
            userIdInput.value;


        if (!userId) {

            showError(
                "User ID is missing."
            );

            return;
        }


        const firstname =
            firstNameInput.value.trim();

        const lastname =
            lastNameInput.value.trim();

        const phoneNumber =
            phoneNumberInput.value.trim();

        const city =
            cityInput.value.trim();


        /*
         * Backend UpdateProfileDto accepts:
         *
         * id
         * firstname
         * lastname
         * phoneNumber
         * city
         *
         * Email is NOT included.
         */

        const requestBody = {

            id: userId,

            firstname: firstname,

            lastname: lastname,

            phoneNumber: phoneNumber,

            city: city

        };


        saveChangesBtn.disabled =
            true;

        saveChangesBtn.textContent =
            "Saving...";


        hideError();

        hideSaveMessage();


        try {

            const response =
                await fetch(
                    `${API_BASE_URL}${PROFILE_ENDPOINT}/${userId}/profile`,
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


            if (response.status === 401) {

                logout();

                return;
            }


            if (!response.ok) {

                let errorMessageText =
                    "Could not save profile.";

                try {

                    const errorData =
                        await response.json();

                    console.error(
                        "Backend error:",
                        errorData
                    );

                    if (
                        errorData.message
                    ) {

                        errorMessageText =
                            errorData.message;

                    }

                } catch (error) {

                    console.error(
                        "Could not parse error response:",
                        error
                    );
                }


                throw new Error(
                    errorMessageText
                );
            }


            /*
             * Backend returns the updated
             * UserProfileDto.
             */

            const updatedProfile =
                await response.json();


            console.log(
                "Updated profile:",
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
   LIVE FULL NAME UPDATE
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
   SHOW SAVE MESSAGE
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
   HIDE SAVE MESSAGE
===================================================== */

function hideSaveMessage() {

    saveMessage.style.display =
        "none";
}


/* =====================================================
   SHOW ERROR
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

    localStorage.removeItem("token");

    localStorage.removeItem("role");

    localStorage.removeItem("email");

    window.location.href =
        "login.html";
}


logoutBtn.addEventListener(
    "click",
    logout
);


/* =====================================================
   INITIALIZE PROFILE PAGE
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadUserProfile();

    }
);