import { useState, useEffect, useCallback } from "react";

const STORAGE_KEY = "rag-theme";

function getStoredTheme() {
    try {
        return localStorage.getItem(STORAGE_KEY) || "light";
    } catch {
        return "light";
    }
}

function applyTheme(theme) {
    const isDark =
        theme === "dark" ||
        (theme === "system" &&
            window.matchMedia("(prefers-color-scheme: dark)").matches);

    document.documentElement.classList.toggle("dark", isDark);
}

export function useTheme() {
    const [theme, setThemeState] = useState(getStoredTheme);

    useEffect(() => {
        applyTheme(theme);
    }, [theme]);

    useEffect(() => {
        const mq = window.matchMedia("(prefers-color-scheme: dark)");
        const handler = () => {
            if (getStoredTheme() === "system") {
                applyTheme("system");
            }
        };
        mq.addEventListener("change", handler);
        return () => mq.removeEventListener("change", handler);
    }, []);

    const setTheme = useCallback((newTheme) => {
        try {
            localStorage.setItem(STORAGE_KEY, newTheme);
        } catch {}
        setThemeState(newTheme);
        applyTheme(newTheme);
    }, []);

    return { theme, setTheme };
}
