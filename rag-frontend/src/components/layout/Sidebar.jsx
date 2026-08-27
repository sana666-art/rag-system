import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    Archive,
    ArchiveRestore,
    ChevronDown,
    ChevronUp,
    Download,
    LogOut,
    MoreHorizontal,
    Pencil,
    Pin,
    PinOff,
    Plus,
    Search,
    Settings,
    Trash2,
    User,
    Zap,
} from "lucide-react";
import { motion } from "framer-motion";
import clsx from "clsx";
import toast from "react-hot-toast";
import { useAuth } from "../../hooks/useAuth";
import { exportSession } from "../../api/chatApi";
import Avatar from "../ui/Avatar";
import Button from "../ui/Button";
import Dropdown from "../ui/Dropdown";
import Modal from "../ui/Modal";
import Skeleton from "../ui/Skeleton";

const GROUP_ORDER = [
    "Pinned",
    "Today",
    "Yesterday",
    "Previous 7 Days",
    "Previous 30 Days",
    "Older",
];

function startOfDay(date) {
    const d = new Date(date);
    d.setHours(0, 0, 0, 0);
    return d;
}

function groupFor(date) {
    const now = new Date();
    const day = startOfDay(date);
    const today = startOfDay(now);

    const shiftDays = (days) => {
        const d = new Date(today);
        d.setDate(today.getDate() - days);
        return d;
    };

    if (day >= today) return "Today";
    if (day >= shiftDays(1)) return "Yesterday";
    if (day >= shiftDays(6)) return "Previous 7 Days";
    if (day >= shiftDays(29)) return "Previous 30 Days";
    return "Older";
}

function sessionDate(session) {
    const value = session.updatedAt || session.createdAt;
    return value ? new Date(value) : new Date(0);
}

function timeAgo(value) {
    const date = new Date(value);
    const seconds = Math.floor((Date.now() - date.getTime()) / 1000);
    if (Number.isNaN(seconds)) return "";
    if (seconds < 60) return "just now";
    const minutes = Math.floor(seconds / 60);
    if (minutes < 60) return `${minutes}m ago`;
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours}h ago`;
    const days = Math.floor(hours / 24);
    if (days < 7) return `${days}d ago`;
    return date.toLocaleDateString();
}

function previewFor(session) {
    const content = (session.lastMessage || "")
        .replace(/\s+/g, " ")
        .trim();
    if (!content) return "";
    return content.length > 80 ? content.slice(0, 80) + "…" : content;
}

function formatTokens(value) {
    const n = Number(value);
    if (!Number.isFinite(n) || n <= 0) return null;
    if (n >= 1000) {
        const k = n / 1000;
        return `${k >= 100 ? Math.round(k) : k.toFixed(1)}K tokens`;
    }
    return `${n} tokens`;
}

const PIN_KEY_PREFIX = "pinnedChats:";

function loadPinned(email) {
    try {
        const raw = localStorage.getItem(PIN_KEY_PREFIX + (email || "guest"));
        return raw ? JSON.parse(raw) : [];
    } catch {
        return [];
    }
}

function savePinned(email, ids) {
    try {
        localStorage.setItem(
            PIN_KEY_PREFIX + (email || "guest"),
            JSON.stringify(ids)
        );
    } catch {
        // ignore storage errors
    }
}

const ARCHIVE_KEY_PREFIX = "archivedChats:";

function loadArchived(email) {
    try {
        const raw = localStorage.getItem(ARCHIVE_KEY_PREFIX + (email || "guest"));
        return raw ? JSON.parse(raw) : [];
    } catch {
        return [];
    }
}

function saveArchived(email, ids) {
    try {
        localStorage.setItem(
            ARCHIVE_KEY_PREFIX + (email || "guest"),
            JSON.stringify(ids)
        );
    } catch {
        // ignore storage errors
    }
}

function SessionItem({
    session,
    active,
    pinned,
    archived = false,
    onSelect,
    onRename,
    onDelete,
    onTogglePin,
    onArchive,
    onExport,
}) {

    const title = session.title || "Untitled chat";
    const preview = previewFor(session);
    const tokenLabel = formatTokens(session.totalTokens);

    return (
        <div
            className={clsx(
                "group relative flex items-center rounded-lg transition-colors",
                active ? "bg-gray-100" : "hover:bg-gray-100"
            )}
        >
            <button
                type="button"
                onClick={() => onSelect(session.id)}
                className="min-w-0 flex-1 px-3 py-2 text-left"
            >
                <span className="flex items-center gap-1.5">
                    {pinned && (
                        <Pin className="h-3 w-3 shrink-0 text-gray-400" />
                    )}
                    {archived && (
                        <Archive className="h-3 w-3 shrink-0 text-gray-400" />
                    )}
                    <span
                        className={clsx(
                            "truncate text-sm",
                            active ? "text-gray-900" : "text-gray-700"
                        )}
                    >
                        {title}
                    </span>
                </span>
                <span className="mt-0.5 flex items-center gap-1 truncate text-xs text-gray-400">
                    {preview
                        ? `${preview} · ${timeAgo(sessionDate(session))}`
                        : timeAgo(sessionDate(session))}
                    {tokenLabel && (
                        <span className="inline-flex shrink-0 items-center gap-0.5 font-medium text-gray-400">
                            <Zap className="h-3 w-3" />
                            {tokenLabel}
                        </span>
                    )}
                </span>
            </button>

            <div className="absolute right-1 top-1/2 -translate-y-1/2">
                <Dropdown
                    align="left"
                    menuClassName="right-0"
                    flip
                    trigger={
                        <button
                            type="button"
                            aria-label="Chat options"
                            className={clsx(
                                "flex h-7 w-7 items-center justify-center rounded-md transition-colors",
                                active
                                    ? "text-gray-500 hover:bg-gray-200 hover:text-gray-800"
                                    : "text-gray-300 hover:bg-gray-200 hover:text-gray-700"
                            )}
                        >
                            <MoreHorizontal className="h-4 w-4" />
                        </button>
                    }
                >
                    {!archived && (
                        <button
                            type="button"
                            onClick={() => onTogglePin(session)}
                            className="flex w-full items-center gap-2 px-3 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
                        >
                            {pinned ? (
                                <PinOff className="h-4 w-4 text-gray-400" />
                            ) : (
                                <Pin className="h-4 w-4 text-gray-400" />
                            )}
                            {pinned ? "Unpin" : "Pin"}
                        </button>
                    )}

                    <button
                        type="button"
                        onClick={() => onRename(session)}
                        className="flex w-full items-center gap-2 px-3 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
                    >
                        <Pencil className="h-4 w-4 text-gray-400" />
                        Rename
                    </button>

                    <button
                        type="button"
                        onClick={() => onArchive?.(session)}
                        className="flex w-full items-center gap-2 px-3 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
                    >
                        {archived ? (
                            <ArchiveRestore className="h-4 w-4 text-gray-400" />
                        ) : (
                            <Archive className="h-4 w-4 text-gray-400" />
                        )}
                        {archived ? "Unarchive" : "Archive"}
                    </button>

                    <button
                        type="button"
                        onClick={() => onExport(session)}
                        className="flex w-full items-center gap-2 px-3 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
                    >
                        <Download className="h-4 w-4 text-gray-400" />
                        Export as Markdown
                    </button>

                    <button
                        type="button"
                        onClick={() => onDelete(session)}
                        className="flex w-full items-center gap-2 px-3 py-2 text-left text-sm text-red-600 hover:bg-gray-100"
                    >
                        <Trash2 className="h-4 w-4 text-red-400" />
                        Delete
                    </button>
                </Dropdown>
            </div>
        </div>
    );
}

export default function Sidebar({
    sessions = [],
    loading = false,
    activeSessionId,
    collapsed = false,
    mobileOpen = false,
    onCloseMobile,
    onNewChat,
    onSelectSession,
    onRenameSession,
    onDeleteSession,
}) {

    const navigate = useNavigate();
    const { user, logout } = useAuth();

    const [query, setQuery] = useState("");
    const [renameTarget, setRenameTarget] = useState(null);
    const [deleteTarget, setDeleteTarget] = useState(null);
    const [renameValue, setRenameValue] = useState("");
    const [pinnedIds, setPinnedIds] = useState(() => loadPinned(user?.email));
    const [archivedIds, setArchivedIds] = useState(() => loadArchived(user?.email));
    const [archivedOpen, setArchivedOpen] = useState(false);
    const searchRef = useRef(null);

    const groups = useMemo(() => {
        const active = sessions.filter((s) => !archivedIds.includes(s.id));
        const filtered = query.trim()
            ? active.filter((s) =>
                (s.title || "").toLowerCase().includes(query.trim().toLowerCase()))
            : active;

        const sorted = [...filtered].sort(
            (a, b) => sessionDate(b) - sessionDate(a)
        );

        const pinnedSet = new Set(pinnedIds);

        const map = new Map();
        for (const session of sorted) {
            if (pinnedSet.has(session.id)) continue;
            const label = groupFor(sessionDate(session));
            if (!map.has(label)) map.set(label, []);
            map.get(label).push(session);
        }

        const groups = GROUP_ORDER
            .filter((label) => map.has(label))
            .map((label) => ({ label, items: map.get(label) }));

        const pinnedItems = sorted.filter((s) => pinnedSet.has(s.id));
        if (pinnedItems.length > 0) {
            groups.unshift({ label: "Pinned", items: pinnedItems });
        }

        return groups;
    }, [sessions, query, pinnedIds, archivedIds]);

    const archivedSessions = useMemo(() => {
        const archived = sessions.filter((s) => archivedIds.includes(s.id));
        const filtered = query.trim()
            ? archived.filter((s) =>
                (s.title || "").toLowerCase().includes(query.trim().toLowerCase()))
            : archived;
        return filtered.sort((a, b) => sessionDate(b) - sessionDate(a));
    }, [sessions, query, archivedIds]);

    const togglePin = (session) => {
        setPinnedIds((previous) => {
            const next = previous.includes(session.id)
                ? previous.filter((id) => id !== session.id)
                : [...previous, session.id];
            savePinned(user?.email, next);
            return next;
        });
    };

    const toggleArchive = (session) => {
        const isArchived = archivedIds.includes(session.id);
        const next = isArchived
            ? archivedIds.filter((id) => id !== session.id)
            : [...archivedIds, session.id];
        setArchivedIds(next);
        saveArchived(user?.email, next);

        if (!isArchived && pinnedIds.includes(session.id)) {
            const nextPins = pinnedIds.filter((id) => id !== session.id);
            setPinnedIds(nextPins);
            savePinned(user?.email, nextPins);
        }
    };

    const handleExport = async (session) => {
        try {
            const blob = await exportSession(session.id);
            const url = URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.download = `chat-${session.id}.md`;
            document.body.appendChild(link);
            link.click();
            link.remove();
            URL.revokeObjectURL(url);
        } catch {
            toast.error("Failed to export chat");
        }
    };

    useEffect(() => {
        const handleKeyDown = (event) => {
            if (event.defaultPrevented) return;

            const tag = document.activeElement?.tagName;
            const typing =
                tag === "INPUT" || tag === "TEXTAREA" || tag === "SELECT";

            if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === "n") {
                event.preventDefault();
                onNewChat();
                return;
            }

            if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === "k") {
                event.preventDefault();
                searchRef.current?.focus();
                searchRef.current?.select();
                return;
            }

            if (
                !typing &&
                !collapsed &&
                (event.key === "Delete" || event.key === "Backspace")
            ) {
                const active = sessions.find((s) => s.id === activeSessionId);
                if (active) {
                    event.preventDefault();
                    setDeleteTarget(active);
                }
            }
        };

        window.addEventListener("keydown", handleKeyDown);
        return () => window.removeEventListener("keydown", handleKeyDown);
    }, [onNewChat, activeSessionId, sessions, collapsed]);

    const openRename = (session) => {
        setRenameTarget(session);
        setRenameValue(session.title || "");
    };

    const submitRename = async () => {
        const title = renameValue.trim();
        if (!title || !renameTarget) return;
        await onRenameSession(renameTarget.id, title);
        setRenameTarget(null);
    };

    const submitDelete = async () => {
        if (!deleteTarget) return;
        await onDeleteSession(deleteTarget.id);
        setDeleteTarget(null);
    };

    const displayName = user?.fullName || user?.name || "User";

    const handleLogout = () => {
        logout();
        navigate("/");
    };

    return (
        <>
            {mobileOpen && (
                <div
                    className="fixed inset-0 z-40 bg-black/40 md:hidden"
                    onClick={onCloseMobile}
                    aria-hidden="true"
                />
            )}

            <motion.aside
                animate={{ width: mobileOpen ? 256 : collapsed ? 0 : 256 }}
                transition={{ type: "spring", stiffness: 300, damping: 32 }}
                className={clsx(
                    "shrink-0 overflow-hidden border-r border-gray-200 bg-white transition-transform duration-300 md:static md:translate-x-0",
                    mobileOpen
                        ? "fixed inset-y-0 left-0 z-50 translate-x-0"
                        : "fixed inset-y-0 left-0 z-50 -translate-x-full"
                )}
            >
            <div className="flex h-full w-64 flex-col">
                <div className="p-3 pb-2">
                    <button
                        type="button"
                        onClick={() => {
                            onNewChat();
                            onCloseMobile?.();
                        }}
                        className="flex w-full items-center gap-2 rounded-xl border border-gray-200 px-3 py-2 text-sm font-medium text-gray-700 transition hover:bg-gray-50"
                    >
                        <Plus className="h-4 w-4" />
                        New chat
                    </button>
                </div>

                <div className="relative px-3 pb-2">
                    <Search className="pointer-events-none absolute left-6 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
                    <input
                        ref={searchRef}
                        value={query}
                        onChange={(event) => setQuery(event.target.value)}
                        placeholder="Search chats  (Ctrl+K)"
                        className="w-full rounded-xl border border-transparent bg-gray-100 py-2 pl-9 pr-3 text-sm text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-gray-200 focus:bg-white"
                    />
                </div>

                <div className="flex-1 overflow-y-auto px-2 pb-3">
                    {loading ? (
                        <div className="space-y-2 px-1 py-2">
                            {[0, 1, 2, 3].map((i) => (
                                <Skeleton key={i} className="h-9 w-full" />
                            ))}
                        </div>
                    ) : groups.length === 0 && archivedSessions.length === 0 ? (
                        <p className="px-3 py-4 text-center text-xs text-gray-400">
                            {query.trim()
                                ? "No chats match your search"
                                : "No chats yet. Start a new one!"}
                        </p>
                    ) : (
                        <>
                            {groups.map((group) => (
                                <div key={group.label}>
                                    <p className="px-3 pb-1 pt-3 text-xs font-medium text-gray-500">
                                        {group.label}
                                    </p>
                                    <div className="space-y-0.5">
                                        {group.items.map((session) => (
                                            <SessionItem
                                                key={session.id}
                                                session={session}
                                                active={activeSessionId === session.id}
                                                pinned={pinnedIds.includes(session.id)}
                                                onSelect={(id) => {
                                                    onSelectSession(id);
                                                    onCloseMobile?.();
                                                }}
                                                onRename={openRename}
                                                onDelete={setDeleteTarget}
                                                onTogglePin={togglePin}
                                                onArchive={toggleArchive}
                                                onExport={handleExport}
                                            />
                                        ))}
                                    </div>
                                </div>
                            ))}

                            {archivedSessions.length > 0 && (
                                <div>
                                    <button
                                        type="button"
                                        onClick={() => setArchivedOpen((v) => !v)}
                                        className="flex w-full items-center gap-1.5 px-3 pb-1 pt-3 text-xs font-medium text-gray-500 transition hover:text-gray-800"
                                    >
                                        <Archive className="h-3.5 w-3.5" />
                                        Archived
                                        <span className="rounded-full bg-gray-200 px-1.5 text-[10px] font-semibold text-gray-600">
                                            {archivedSessions.length}
                                        </span>
                                        <ChevronDown
                                            className={clsx(
                                                "ml-auto h-3.5 w-3.5 transition-transform",
                                                archivedOpen && "rotate-180"
                                            )}
                                        />
                                    </button>
                                    {archivedOpen && (
                                        <div className="space-y-0.5">
                                            {archivedSessions.map((session) => (
                                                <SessionItem
                                                    key={session.id}
                                                    session={session}
                                                    active={activeSessionId === session.id}
                                                    archived
                                                    onSelect={(id) => {
                                                        onSelectSession(id);
                                                        onCloseMobile?.();
                                                    }}
                                                    onRename={openRename}
                                                    onDelete={setDeleteTarget}
                                                    onArchive={toggleArchive}
                                                    onExport={handleExport}
                                                />
                                            ))}
                                        </div>
                                    )}
                                </div>
                            )}
                        </>
                    )}
                </div>

                <div className="border-t border-gray-200 p-2">
                    <Dropdown
                        align="left"
                        menuClassName="left-0 bottom-full mb-1 w-56 mt-0!"
                        trigger={
                            <button
                                type="button"
                                className="flex w-full items-center gap-2 rounded-lg p-2 text-left transition hover:bg-gray-100"
                            >
                                <Avatar name={displayName} size="sm" />
                                <span className="min-w-0 flex-1">
                                    <span className="block truncate text-sm font-medium text-gray-900">
                                        {displayName}
                                    </span>
                                    {user?.email && (
                                        <span className="block truncate text-xs text-gray-500">
                                            {user.email}
                                        </span>
                                    )}
                                </span>
                                <ChevronUp className="h-4 w-4 text-gray-400" />
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
            </div>

            <Modal
                open={renameTarget !== null}
                onClose={() => setRenameTarget(null)}
                title="Rename chat"
                size="sm"
                footer={
                    <>
                        <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => setRenameTarget(null)}
                        >
                            Cancel
                        </Button>
                        <Button
                            size="sm"
                            onClick={submitRename}
                            disabled={!renameValue.trim()}
                        >
                            Save
                        </Button>
                    </>
                }
            >
                <input
                    value={renameValue}
                    onChange={(event) => setRenameValue(event.target.value)}
                    onKeyDown={(event) => {
                        if (event.key === "Enter") submitRename();
                    }}
                    autoFocus
                    className="w-full rounded-xl border border-gray-300 px-3 py-2 text-sm outline-none focus:border-gray-500 focus:ring-2 focus:ring-gray-100"
                    placeholder="Chat title"
                />
            </Modal>

            <Modal
                open={deleteTarget !== null}
                onClose={() => setDeleteTarget(null)}
                title="Delete chat?"
                size="sm"
                footer={
                    <>
                        <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => setDeleteTarget(null)}
                        >
                            Cancel
                        </Button>
                        <Button
                            variant="danger"
                            size="sm"
                            onClick={submitDelete}
                        >
                            Delete
                        </Button>
                    </>
                }
            >
                <p className="text-sm text-gray-600">
                    This will permanently delete{" "}
                    <span className="font-semibold text-gray-900">
                        “{deleteTarget?.title || "Untitled chat"}”
                    </span>{" "}
                    and all its messages. This action cannot be undone.
                </p>
            </Modal>
        </motion.aside>
        </>
    );
}
