import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";

export default function Navbar() {

    const navigate = useNavigate();
    const location = useLocation();
    const { isAuthenticated } = useAuth();

    const goHome = () => {
        if (location.pathname === "/") {
            window.scrollTo({ top: 0, behavior: "smooth" });
        } else {
            navigate("/");
        }
    };

    const goToSection = (target) => {
        if (location.pathname === "/") {
            document.getElementById(target)?.scrollIntoView({ behavior: "smooth" });
        } else {
            navigate("/", { state: { scrollTo: target } });
        }
    };

    return (
        <header className="fixed inset-x-0 top-0 z-40 h-14 border-b border-gray-200 bg-white/90 backdrop-blur">
            <div className="mx-auto flex h-full max-w-7xl items-center justify-between px-4 sm:px-6">
                <div className="flex items-center gap-8">
                    <button
                        type="button"
                        onClick={goHome}
                        className="flex items-center gap-2 text-gray-900"
                    >
                        <svg
                            viewBox="0 0 24 24"
                            className="h-6 w-6"
                            fill="none"
                            aria-hidden="true"
                        >
                            <path
                                d="M12 2 3.5 6.5v11L12 22l8.5-4.5v-11L12 2Z"
                                stroke="currentColor"
                                strokeWidth="1.5"
                            />
                            <path
                                d="M12 22v-9.5M3.5 6.5 12 12l8.5-5.5"
                                stroke="currentColor"
                                strokeWidth="1.5"
                            />
                        </svg>
                        <span className="text-[15px] font-semibold tracking-tight">
                            RAG Assistant
                        </span>
                    </button>

                    <nav className="hidden items-center gap-6 text-sm text-gray-600 md:flex">
                        <button
                            type="button"
                            onClick={() => goToSection("features")}
                            className="transition hover:text-gray-900"
                        >
                            Features
                        </button>
                        <button
                            type="button"
                            onClick={() => goToSection("how-it-works")}
                            className="transition hover:text-gray-900"
                        >
                            How it works
                        </button>
                        <button
                            type="button"
                            onClick={() => goToSection("pricing")}
                            className="transition hover:text-gray-900"
                        >
                            Pricing
                        </button>
                        <button
                            type="button"
                            onClick={() => goToSection("faq")}
                            className="transition hover:text-gray-900"
                        >
                            FAQ
                        </button>
                        {!isAuthenticated && (
                            <button
                                type="button"
                                onClick={() => navigate("/guest-chat")}
                                className="rounded-full border border-gray-300 px-4 py-1.5 font-medium text-gray-700 transition hover:bg-gray-100"
                            >
                                Try demo
                            </button>
                        )}
                    </nav>
                </div>

                <div className="flex items-center gap-2">
                    {isAuthenticated ? (
                        <button
                            type="button"
                            onClick={() => navigate("/dashboard/chat")}
                            className="rounded-full bg-gray-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-black"
                        >
                            Open dashboard
                        </button>
                    ) : (
                        <>
                            <button
                                type="button"
                                onClick={() => navigate("/login")}
                                className="rounded-full px-4 py-2 text-sm font-medium text-gray-700 transition hover:bg-gray-100 hover:text-gray-900"
                            >
                                Log in
                            </button>

                            <button
                                type="button"
                                onClick={() => navigate("/register")}
                                className="rounded-full bg-gray-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-black"
                            >
                                Sign up
                            </button>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
}
