import { useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { Bot, ChevronDown, LogOut, ShieldCheck } from "lucide-react";
import { useAuth } from "../../hooks/useAuth";
import authService from "../../services/authService";
import { getRefreshToken } from "../../utils/storage";

export default function Navbar() {

    const navigate = useNavigate();
    const { user, logout, setCurrentUser } = useAuth();

    const [menuOpen, setMenuOpen] = useState(false);
    const [toggling2FA, setToggling2FA] = useState(false);
    const [loggingOut, setLoggingOut] = useState(false);

    const email = user?.email;
    const twoFactorEnabled = !!user?.twoFactorEnabled;

    const handleLogout = async () => {

        setLoggingOut(true);

        try {
            const refreshToken = getRefreshToken();
            if (refreshToken) {
                await authService.logout(refreshToken);
            }
        } catch {
            // proceed with local logout even if the request fails
        } finally {
            logout();
            navigate("/login", { replace: true });
        }

    };

    const handleToggle2FA = async () => {

        setToggling2FA(true);

        try {

            const result = await authService.toggle2FA();

            const enabled = result?.message?.toLowerCase().includes("enabled");

            setCurrentUser({ twoFactorEnabled: enabled });

            toast.success(result?.message || "Two-factor authentication updated");

        } catch (error) {
            toast.error(error.response?.data?.message || "Could not update two-factor authentication");
        } finally {
            setToggling2FA(false);
        }

    };

    return (
        <header className="border-b border-gray-200 bg-white">
            <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-3">
                <div className="flex items-center gap-2">
                    <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-gray-900 text-white">
                        <Bot className="h-5 w-5" />
                    </div>
                    <span className="text-lg font-semibold text-gray-900">
                        RAG Assistant
                    </span>
                </div>

                <div className="relative">
                    <button
                        type="button"
                        onClick={() => setMenuOpen((open) => !open)}
                        className="flex items-center gap-2 rounded-full border border-gray-200 py-1.5 pl-1.5 pr-3 hover:bg-gray-50"
                    >
                        <span className="flex h-8 w-8 items-center justify-center rounded-full bg-gray-100 text-sm font-semibold text-gray-700">
                            {email?.charAt(0)?.toUpperCase() || "U"}
                        </span>

                        <span className="hidden text-sm text-gray-700 sm:block">
                            {email}
                        </span>

                        <ChevronDown className="h-4 w-4 text-gray-400" />
                    </button>

                    {menuOpen && (
                        <>
                            <div
                                className="fixed inset-0 z-10"
                                onClick={() => setMenuOpen(false)}
                            />

                            <div className="absolute right-0 z-20 mt-2 w-72 rounded-xl border border-gray-200 bg-white p-2 shadow-lg">
                                <div className="border-b border-gray-100 px-3 py-2">
                                    <p className="text-sm font-medium text-gray-900">
                                        {email}
                                    </p>
                                </div>

                                <button
                                    type="button"
                                    onClick={handleToggle2FA}
                                    disabled={toggling2FA}
                                    className="mt-2 flex w-full items-center justify-between rounded-lg px-3 py-2 text-sm text-gray-700 hover:bg-gray-50 disabled:opacity-60"
                                >
                                    <span className="flex items-center gap-2">
                                        <ShieldCheck className="h-4 w-4 text-gray-900" />
                                        Two-factor authentication
                                    </span>

                                    <span
                                        className={
                                            twoFactorEnabled
                                                ? "rounded-full bg-green-100 px-2 py-0.5 text-xs font-medium text-green-700"
                                                : "rounded-full bg-gray-100 px-2 py-0.5 text-xs font-medium text-gray-600"
                                        }
                                    >
                                        {toggling2FA ? "..." : (twoFactorEnabled ? "On" : "Off")}
                                    </span>
                                </button>

                                <button
                                    type="button"
                                    onClick={handleLogout}
                                    disabled={loggingOut}
                                    className="mt-1 flex w-full items-center gap-2 rounded-lg px-3 py-2 text-sm text-red-600 hover:bg-red-50 disabled:opacity-60"
                                >
                                    <LogOut className="h-4 w-4" />
                                    {loggingOut ? "Logging out..." : "Logout"}
                                </button>
                            </div>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
}
