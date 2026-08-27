const ACCESS_TOKEN_KEY = "accessToken";
const REFRESH_TOKEN_KEY = "refreshToken";
const USER_EMAIL_KEY = "userEmail";
const USER_KEY = "user";

/* ---------- Access Token ---------- */

export function saveAccessToken(token) {
    localStorage.setItem(ACCESS_TOKEN_KEY, token);
}

export function getAccessToken() {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function removeAccessToken() {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
}

/* ---------- Refresh Token ---------- */

export function saveRefreshToken(token) {
    localStorage.setItem(REFRESH_TOKEN_KEY, token);
}

export function getRefreshToken() {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
}

export function removeRefreshToken() {
    localStorage.removeItem(REFRESH_TOKEN_KEY);
}

/* ---------- Email ---------- */

export function saveEmail(email) {
    localStorage.setItem(USER_EMAIL_KEY, email);
}

export function getEmail() {
    return localStorage.getItem(USER_EMAIL_KEY);
}

export function removeEmail() {
    localStorage.removeItem(USER_EMAIL_KEY);
}

/* ---------- User ---------- */

export function saveUser(user) {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function getUser() {
    try {
        return JSON.parse(localStorage.getItem(USER_KEY));
    } catch {
        return null;
    }
}

export function removeUser() {
    localStorage.removeItem(USER_KEY);
}

/* ---------- Helpers ---------- */

export function saveTokens(accessToken, refreshToken) {
    saveAccessToken(accessToken);
    saveRefreshToken(refreshToken);
}

export function isAuthenticated() {
    return !!getAccessToken();
}

export function clearTokens() {
    removeAccessToken();
    removeRefreshToken();
}

export function clearStorage() {
    clearTokens();
    removeEmail();
    removeUser();
}