import { useNavigate } from "react-router-dom";
import { ChevronDown, LogOut, PanelLeft, Settings, Sparkles, User } from "lucide-react";
import { useAuth } from "../../hooks/useAuth";
import Avatar from "../ui/Avatar";
import Dropdown from "../ui/Dropdown";

function ModelSelector({ model }) {

    const activeModel = (model || "AI Assistant").trim();

    return (
        <Dropdown
            align="left"
            menuClassName="w-64"
            trigger={
                <button
                    type="button"
                    aria-label="Select model"
                    className="flex items-center gap-1.5 rounded-lg px-2 py-1.5 text-sm font-medium text-gray-600 transition hover:bg-gray-100 hover:text-gray-900"
                >
                    <Sparkles className="h-3.5 w-3.5 text-gray-400" />
                    <span className="max-w-32 truncate">{activeModel}</span>
                    <ChevronDown className="h-3.5 w-3.5 text-gray-400" />
                </button>
            }
        >
            <div className="px-3 py-2">
                <p className="text-[11px] font-semibold uppercase tracking-wide text-gray-400">
                    Model
                </p>
                <div className="mt-1.5 flex items-center justify-between gap-2 rounded-lg bg-gray-50 px-2.5 py-2">
                    <span className="text-sm font-medium text-gray-900">
                        {activeModel}
                    </span>
                    <span className="text-[11px] font-semibold text-gray-400">
                        Active
                    </span>
                </div>
                <p className="mt-2 text-xs leading-relaxed text-gray-500">
                    Model switching is coming soon. Responses are generated with
                    the configured model.
                </p>
            </div>
        </Dropdown>
    );
}

export default function ChatHeader({ title = "New chat", model, onToggleSidebar }) {

    const navigate = useNavigate();
    const { user, logout } = useAuth();

    const displayName = user?.fullName || user?.name || "User";

    const handleLogout = () => {
        logout();
        navigate("/");
    };

    return (
        <header className="flex h-14 shrink-0 items-center border-b border-gray-200 bg-white">
            <div className="flex w-14 justify-center">
                <button
                    type="button"
                    onClick={onToggleSidebar}
                    aria-label="Toggle sidebar"
                    className="rounded-lg p-2 text-gray-500 transition hover:bg-gray-100 hover:text-gray-900"
                >
                    <PanelLeft className="h-5 w-5" />
                </button>
            </div>

            <div className="flex min-w-0 flex-1 items-center justify-center gap-2">
                <ModelSelector model={model} />

                <h1 className="min-w-0 truncate text-sm font-medium text-gray-900">
                    {title}
                </h1>
            </div>

            <div className="flex w-14 justify-center">
                <Dropdown
                    align="right"
                    menuClassName="w-56"
                    trigger={
                        <button
                            type="button"
                            aria-label="Account menu"
                            className="rounded-full transition hover:opacity-80"
                        >
                            <Avatar name={displayName} size="sm" />
                        </button>
                    }
                >
                    <button
                        type="button"
                        onClick={() => navigate("/dashboard/profile")}
                        className="flex w-full items-center gap-2 px-3 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
                    >
                        <User className="h-4 w-4 text-gray-400" />
                        Profile
                    </button>

                    <button
                        type="button"
                        onClick={() => navigate("/dashboard/settings")}
                        className="flex w-full items-center gap-2 px-3 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
                    >
                        <Settings className="h-4 w-4 text-gray-400" />
                        Settings
                    </button>

                    <button
                        type="button"
                        onClick={handleLogout}
                        className="flex w-full items-center gap-2 px-3 py-2 text-left text-sm text-red-600 hover:bg-gray-100"
                    >
                        <LogOut className="h-4 w-4 text-red-400" />
                        Log out
                    </button>
                </Dropdown>
            </div>
        </header>
    );
}
