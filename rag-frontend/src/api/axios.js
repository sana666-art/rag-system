import axios from "axios";

import {
    getAccessToken,
    getRefreshToken,
    saveAccessToken,
    clearStorage,
} from "../utils/storage";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

/* ------------------------------------------------ */
/* Request Interceptor */
/* ------------------------------------------------ */

api.interceptors.request.use((config) => {

    const token = getAccessToken();

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;

});

/* ------------------------------------------------ */
/* Refresh Queue */
/* ------------------------------------------------ */

let isRefreshing = false;

let failedQueue = [];

function processQueue(error, token = null) {

    failedQueue.forEach((promise) => {

        if (error) {
            promise.reject(error);
        } else {
            promise.resolve(token);
        }

    });

    failedQueue = [];

}

/* ------------------------------------------------ */
/* Response Interceptor */
/* ------------------------------------------------ */

api.interceptors.response.use(

    (response) => response,

    async (error) => {

        const originalRequest = error.config;

        if (
            error.response?.status !== 401 ||
            originalRequest._retry
        ) {
            return Promise.reject(error);
        }

        originalRequest._retry = true;

        if (isRefreshing) {

            return new Promise((resolve, reject) => {

                failedQueue.push({ resolve, reject });

            }).then((token) => {

                originalRequest.headers.Authorization =
                    `Bearer ${token}`;

                return api(originalRequest);

            });

        }

        isRefreshing = true;

        try {

            const response = await axios.post(

                `${import.meta.env.VITE_API_BASE_URL}/api/refresh`,

                {
                    refreshToken: getRefreshToken(),
                }

            );

            const newAccessToken = response.data.accessToken;

            saveAccessToken(newAccessToken);

            processQueue(null, newAccessToken);

            originalRequest.headers.Authorization =
                `Bearer ${newAccessToken}`;

            return api(originalRequest);

        } catch (refreshError) {

            processQueue(refreshError);

            clearStorage();

            window.location.replace("/login");

            return Promise.reject(refreshError);

        } finally {

            isRefreshing = false;

        }

    }

);

export default api;