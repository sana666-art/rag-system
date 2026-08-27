import { createContext, useEffect, useState } from "react";

import authService from "../services/authService";

import {
    getAccessToken,
    saveUser,
    saveEmail,
    clearStorage,
} from "../utils/storage";

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {

    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {

        let active = true;

        async function bootstrap() {

            if (!getAccessToken()) {
                setLoading(false);
                return;
            }

            try {
                const currentUser = await authService.fetchCurrentUser();
                if (active) setUser(currentUser);
            } catch {
                if (active) setUser(null);
            } finally {
                if (active) setLoading(false);
            }

        }

        bootstrap();

        return () => { active = false; };

    }, []);

    const login = (userData) => {

        saveUser(userData);

        if (userData?.email) {
            saveEmail(userData.email);
        }

        setUser(userData);

    };

    const setCurrentUser = (userData) => {

        const merged = { ...(user || {}), ...userData };

        saveUser(merged);

        setUser(merged);

    };

    const refreshProfile = async () => {

        try {
            const currentUser = await authService.fetchCurrentUser();
            return currentUser;
        } catch {
            return null;
        }

    };

    const logout = () => {

        clearStorage();

        setUser(null);

    };

    return (
        <AuthContext.Provider
            value={{
                user,
                loading,
                login,
                logout,
                setCurrentUser,
                refreshProfile,
                isAuthenticated: !!user
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}
