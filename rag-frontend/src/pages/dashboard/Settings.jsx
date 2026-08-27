import { useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import {
    ArrowLeft,
    Bell,
    Eye,
    Globe,
    Key,
    Lock,
    Mail,
    Moon,
    Palette,
    ShieldCheck,
    Smartphone,
    Sun,
    Trash2,
    User,
    Zap,
    Plus,
    Copy,
    Check,
    AlertTriangle,
    LogOut,
} from "lucide-react";
import toast from "react-hot-toast";
import Card from "../../components/ui/Card";
import Badge from "../../components/ui/Badge";
import Button from "../../components/ui/Button";
import Input from "../../components/ui/Input";
import Modal from "../../components/ui/Modal";
import Divider from "../../components/ui/Divider";
import { useAuth } from "../../hooks/useAuth";
import { useTheme } from "../../hooks/useTheme";
import authService from "../../services/authService";

function SectionHeader({ icon: Icon, title, description }) {
    return (
        <div className="flex items-start gap-3">
            <div className="mt-0.5 flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-gray-100">
                <Icon className="h-4.5 w-4.5 text-gray-600" />
            </div>
            <div>
                <h3 className="text-sm font-semibold text-gray-900">{title}</h3>
                <p className="mt-0.5 text-xs text-gray-500">{description}</p>
            </div>
        </div>
    );
}

function Toggle({ enabled, onChange, disabled = false }) {
    return (
        <button
            type="button"
            onClick={() => !disabled && onChange(!enabled)}
            className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer items-center rounded-full transition-colors duration-200 ease-in-out focus:outline-none focus-visible:ring-2 focus-visible:ring-gray-400 focus-visible:ring-offset-2 ${
                enabled ? "bg-gray-900" : "bg-gray-200"
            } ${disabled ? "cursor-not-allowed opacity-50" : ""}`}
        >
            <span
                className={`pointer-events-none inline-block h-4 w-4 transform rounded-full bg-white shadow-sm ring-0 transition duration-200 ease-in-out ${
                    enabled ? "translate-x-6" : "translate-x-1"
                }`}
            />
        </button>
    );
}

function SettingRow({ label, description, children, className = "" }) {
    return (
        <div
            className={`flex items-center justify-between gap-4 py-3.5 ${className}`}
        >
            <div className="min-w-0 flex-1">
                <p className="text-sm font-medium text-gray-800">{label}</p>
                {description && (
                    <p className="mt-0.5 text-xs text-gray-500">{description}</p>
                )}
            </div>
            <div className="shrink-0">{children}</div>
        </div>
    );
}

function ThemeCard({ icon: Icon, label, active, onClick }) {
    return (
        <button
            type="button"
            onClick={onClick}
            className={`flex flex-col items-center gap-2 rounded-xl border-2 p-4 transition-all duration-150 ${
                active
                    ? "border-gray-900 bg-gray-50 shadow-sm"
                    : "border-gray-200 bg-white hover:border-gray-300 hover:bg-gray-50"
            }`}
        >
            <Icon
                className={`h-5 w-5 ${
                    active ? "text-gray-900" : "text-gray-500"
                }`}
            />
            <span
                className={`text-xs font-medium ${
                    active ? "text-gray-900" : "text-gray-600"
                }`}
            >
                {label}
            </span>
        </button>
    );
}

function ColorDot({ color, active, onClick }) {
    return (
        <button
            type="button"
            onClick={onClick}
            className={`flex h-7 w-7 items-center justify-center rounded-full transition-all duration-150 ${
                active
                    ? "ring-2 ring-offset-2 ring-gray-900"
                    : "hover:ring-2 hover:ring-offset-1 hover:ring-gray-300"
            }`}
        >
            <span
                className={`h-5 w-5 rounded-full ${color}`}
                style={{ display: "block" }}
            />
        </button>
    );
}

function APIKeyItem({ name, keyPreview, createdAt, onCopy, onDelete }) {
    return (
        <div className="flex items-center justify-between rounded-xl border border-gray-200 bg-white px-4 py-3 transition hover:border-gray-300">
            <div className="flex items-center gap-3">
                <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-gray-100">
                    <Key className="h-4 w-4 text-gray-600" />
                </div>
                <div>
                    <p className="text-sm font-medium text-gray-800">{name}</p>
                    <p className="text-xs text-gray-500 font-mono">
                        {keyPreview}
                    </p>
                </div>
            </div>
            <div className="flex items-center gap-1.5">
                <button
                    onClick={onCopy}
                    className="rounded-lg p-1.5 text-gray-400 transition hover:bg-gray-100 hover:text-gray-600"
                    title="Copy key"
                >
                    <Copy className="h-4 w-4" />
                </button>
                <button
                    onClick={onDelete}
                    className="rounded-lg p-1.5 text-gray-400 transition hover:bg-red-50 hover:text-red-500"
                    title="Delete key"
                >
                    <Trash2 className="h-4 w-4" />
                </button>
            </div>
        </div>
    );
}

export default function Settings() {
    const { user, setCurrentUser, refreshProfile, logout } = useAuth();
    const { theme, setTheme } = useTheme();
    const navigate = useNavigate();
    const [toggling2FA, setToggling2FA] = useState(false);

    const [notifications, setNotifications] = useState({
        email: true,
        product: true,
        security: true,
    });

    const [chatPrefs, setChatPrefs] = useState({
        sendWithEnter: true,
        showSources: true,
        compactMode: false,
    });

    const [apiKeys, setApiKeys] = useState([
        {
            id: 1,
            name: "Production Key",
            key: "sk-prod-****...a3f2",
            createdAt: "2025-08-10",
        },
        {
            id: 2,
            name: "Development Key",
            key: "sk-dev-****...b7c1",
            createdAt: "2025-08-12",
        },
    ]);

    const [showNewKeyModal, setShowNewKeyModal] = useState(false);
    const [newKeyName, setNewKeyName] = useState("");
    const [newKeyValue, setNewKeyValue] = useState(null);
    const [copied, setCopied] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showLogoutModal, setShowLogoutModal] = useState(false);

    const handleToggleNotif = useCallback(
        (key) => {
            setNotifications((prev) => ({
                ...prev,
                [key]: !prev[key],
            }));
            toast.success("Preference saved");
        },
        []
    );

    const handleToggleChat = useCallback(
        (key) => {
            setChatPrefs((prev) => ({
                ...prev,
                [key]: !prev[key],
            }));
            toast.success("Preference saved");
        },
        []
    );

    const handleCreateKey = () => {
        if (!newKeyName.trim()) return;
        const mockKey = `sk-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 8)}`;
        setApiKeys((prev) => [
            ...prev,
            {
                id: Date.now(),
                name: newKeyName.trim(),
                key: mockKey.slice(0, 8) + "****..." + mockKey.slice(-4),
                createdAt: new Date().toISOString().split("T")[0],
            },
        ]);
        setNewKeyValue(mockKey);
        setNewKeyName("");
    };

    const handleCopyKey = (text) => {
        navigator.clipboard.writeText(text);
        setCopied(true);
        toast.success("Copied to clipboard");
        setTimeout(() => setCopied(false), 2000);
    };

    const handleDeleteKey = (id) => {
        setApiKeys((prev) => prev.filter((k) => k.id !== id));
        toast.success("API key deleted");
    };

    const handleDeleteAccount = () => {
        toast.error("Account deletion is not available yet");
        setShowDeleteModal(false);
    };

    const handleLogoutAll = () => {
        logout();
        setShowLogoutModal(false);
    };

    const handleToggle2FA = async () => {
        setToggling2FA(true);
        try {
            await authService.toggle2FA();
            const updated = await refreshProfile();
            if (updated) {
                setCurrentUser(updated);
            }
            toast.success(
                updated?.twoFactorEnabled
                    ? "Two-factor authentication enabled"
                    : "Two-factor authentication disabled"
            );
        } catch (err) {
            toast.error(err?.response?.data?.message || "Failed to toggle 2FA");
        } finally {
            setToggling2FA(false);
        }
    };

    const accentColors = [
        { color: "bg-gray-900", label: "Gray" },
        { color: "bg-indigo-500", label: "Indigo" },
        { color: "bg-emerald-500", label: "Emerald" },
        { color: "bg-amber-500", label: "Amber" },
        { color: "bg-rose-500", label: "Rose" },
        { color: "bg-sky-500", label: "Sky" },
    ];

    return (
        <div className="mx-auto max-w-3xl space-y-6">
            <div className="flex items-center gap-3">
                <button
                    onClick={() => navigate("/dashboard/chat")}
                    className="flex h-9 w-9 items-center justify-center rounded-xl border border-gray-200 bg-white text-gray-500 transition hover:bg-gray-50 hover:text-gray-700"
                    title="Back to chat"
                >
                    <ArrowLeft className="h-4 w-4" />
                </button>
                <div>
                    <h1 className="text-2xl font-bold tracking-tight text-gray-900">
                        Settings
                    </h1>
                    <p className="mt-0.5 text-sm text-gray-500">
                        Manage your preferences and account configuration
                    </p>
                </div>
            </div>

            <Card className="divide-y divide-gray-100 px-0! py-0!">
                <div className="px-6 pt-5 pb-2">
                    <SectionHeader
                        icon={Palette}
                        title="Appearance"
                        description="Customize how the app looks and feels"
                    />
                </div>

                <div className="px-6 py-2">
                    <SettingRow
                        label="Theme"
                        description="Select your preferred color scheme"
                    >
                        <div className="flex gap-2">
                            <ThemeCard
                                icon={Sun}
                                label="Light"
                                active={theme === "light"}
                                onClick={() => setTheme("light")}
                            />
                            <ThemeCard
                                icon={Moon}
                                label="Dark"
                                active={theme === "dark"}
                                onClick={() => setTheme("dark")}
                            />
                            <ThemeCard
                                icon={Smartphone}
                                label="System"
                                active={theme === "system"}
                                onClick={() => setTheme("system")}
                            />
                        </div>
                    </SettingRow>

                    <SettingRow
                        label="Accent Color"
                        description="Choose your primary accent color"
                    >
                        <div className="flex gap-2">
                            {accentColors.map((c) => (
                                <ColorDot
                                    key={c.label}
                                    color={c.color}
                                    active={false}
                                    onClick={() =>
                                        toast(
                                            "Accent color coming soon",
                                            { icon: "🎨" }
                                        )
                                    }
                                />
                            ))}
                        </div>
                    </SettingRow>
                </div>
            </Card>

            <Card className="divide-y divide-gray-100 px-0! py-0!">
                <div className="px-6 pt-5 pb-2">
                    <SectionHeader
                        icon={Zap}
                        title="Chat Preferences"
                        description="Configure how the AI chat behaves"
                    />
                </div>

                <div className="px-6 py-2">
                    <SettingRow
                        label="Send with Enter"
                        description="Press Enter to send messages, Shift+Enter for new lines"
                    >
                        <Toggle
                            enabled={chatPrefs.sendWithEnter}
                            onChange={() => handleToggleChat("sendWithEnter")}
                        />
                    </SettingRow>

                    <SettingRow
                        label="Show Sources"
                        description="Display source documents alongside AI responses"
                    >
                        <Toggle
                            enabled={chatPrefs.showSources}
                            onChange={() => handleToggleChat("showSources")}
                        />
                    </SettingRow>

                    <SettingRow
                        label="Compact Mode"
                        description="Reduce spacing for a denser message layout"
                    >
                        <Toggle
                            enabled={chatPrefs.compactMode}
                            onChange={() => handleToggleChat("compactMode")}
                        />
                    </SettingRow>
                </div>
            </Card>

            <Card className="divide-y divide-gray-100 px-0! py-0!">
                <div className="px-6 pt-5 pb-2">
                    <div className="flex items-center justify-between">
                        <SectionHeader
                            icon={Key}
                            title="API Keys"
                            description="Manage your programmatic access keys"
                        />
                        <Button
                            variant="secondary"
                            size="sm"
                            className="gap-1.5"
                            onClick={() => setShowNewKeyModal(true)}
                        >
                            <Plus className="h-3.5 w-3.5" />
                            New Key
                        </Button>
                    </div>
                </div>

                <div className="px-6 py-3">
                    {apiKeys.length > 0 ? (
                        <div className="space-y-2">
                            {apiKeys.map((k) => (
                                <APIKeyItem
                                    key={k.id}
                                    name={k.name}
                                    keyPreview={k.key}
                                    createdAt={k.createdAt}
                                    onCopy={() => handleCopyKey(k.key)}
                                    onDelete={() =>
                                        toast(
                                            (t) => (
                                                <div className="flex flex-col gap-2">
                                                    <p className="text-sm font-medium">
                                                        Delete "{k.name}"?
                                                    </p>
                                                    <div className="flex gap-2">
                                                        <Button
                                                            size="sm"
                                                            variant="danger"
                                                            onClick={() => {
                                                                handleDeleteKey(
                                                                    k.id
                                                                );
                                                                toast.dismiss(
                                                                    t.id
                                                                );
                                                            }}
                                                        >
                                                            Delete
                                                        </Button>
                                                        <Button
                                                            size="sm"
                                                            variant="secondary"
                                                            onClick={() =>
                                                                toast.dismiss(
                                                                    t.id
                                                                )
                                                            }
                                                        >
                                                            Cancel
                                                        </Button>
                                                    </div>
                                                </div>
                                            ),
                                            { duration: Infinity }
                                        )
                                    }
                                />
                            ))}
                        </div>
                    ) : (
                        <div className="rounded-xl border border-dashed border-gray-300 py-8 text-center">
                            <Key className="mx-auto h-8 w-8 text-gray-400" />
                            <p className="mt-2 text-sm font-medium text-gray-600">
                                No API keys
                            </p>
                            <p className="mt-0.5 text-xs text-gray-500">
                                Create a key to access the API programmatically
                            </p>
                        </div>
                    )}
                </div>
            </Card>

            <Card className="divide-y divide-gray-100 px-0! py-0!">
                <div className="px-6 pt-5 pb-2">
                    <SectionHeader
                        icon={Bell}
                        title="Notifications"
                        description="Control what alerts you receive"
                    />
                </div>

                <div className="px-6 py-2">
                    <SettingRow
                        label="Email Notifications"
                        description="Receive updates and alerts via email"
                    >
                        <Toggle
                            enabled={notifications.email}
                            onChange={() => handleToggleNotif("email")}
                        />
                    </SettingRow>

                    <SettingRow
                        label="Product Updates"
                        description="News about features and improvements"
                    >
                        <Toggle
                            enabled={notifications.product}
                            onChange={() => handleToggleNotif("product")}
                        />
                    </SettingRow>

                    <SettingRow
                        label="Security Alerts"
                        description="Important notifications about your account"
                        className="border-b-0!"
                    >
                        <Toggle
                            enabled={notifications.security}
                            onChange={() => handleToggleNotif("security")}
                        />
                    </SettingRow>
                </div>
            </Card>

            <Card className="divide-y divide-gray-100 px-0! py-0!">
                <div className="px-6 pt-5 pb-2">
                    <SectionHeader
                        icon={ShieldCheck}
                        title="Account"
                        description="Manage your account and security"
                    />
                </div>

                <div className="px-6 py-2">
                    <SettingRow
                        label="Two-Factor Authentication"
                        description="Add an extra layer of security to your account"
                    >
                        <div className="flex items-center gap-2.5">
                            <Badge
                                variant={
                                    user?.twoFactorEnabled ? "green" : "neutral"
                                }
                                size="sm"
                            >
                                {user?.twoFactorEnabled ? "On" : "Off"}
                            </Badge>
                            <Toggle
                                enabled={!!user?.twoFactorEnabled}
                                onChange={handleToggle2FA}
                                disabled={toggling2FA}
                            />
                        </div>
                    </SettingRow>

                    <SettingRow
                        label="Email Address"
                        description="Your primary email for account notifications"
                    >
                        <span className="text-sm text-gray-600">
                            {user?.email}
                        </span>
                    </SettingRow>

                    <Divider className="my-1" />

                    <SettingRow label="Sign Out" description="Sign out from this device">
                        <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => setShowLogoutModal(true)}
                            className="gap-1.5"
                        >
                            <LogOut className="h-3.5 w-3.5" />
                            Sign Out
                        </Button>
                    </SettingRow>

                    <SettingRow
                        label="Delete Account"
                        description="Permanently remove your account and all data"
                        className="border-b-0!"
                    >
                        <Button
                            variant="dangerGhost"
                            size="sm"
                            onClick={() => setShowDeleteModal(true)}
                            className="gap-1.5"
                        >
                            <Trash2 className="h-3.5 w-3.5" />
                            Delete
                        </Button>
                    </SettingRow>
                </div>
            </Card>

            <Modal
                open={showNewKeyModal}
                onClose={() => {
                    setShowNewKeyModal(false);
                    setNewKeyValue(null);
                    setNewKeyName("");
                }}
                title={newKeyValue ? "Key Created" : "Create API Key"}
                size="sm"
                footer={
                    newKeyValue ? (
                        <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => {
                                handleCopyKey(newKeyValue);
                            }}
                            className="gap-1.5"
                        >
                            {copied ? (
                                <>
                                    <Check className="h-3.5 w-3.5" />
                                    Copied
                                </>
                            ) : (
                                <>
                                    <Copy className="h-3.5 w-3.5" />
                                    Copy Key
                                </>
                            )}
                        </Button>
                    ) : null
                }
            >
                {newKeyValue ? (
                    <div className="space-y-3">
                        <div className="rounded-xl bg-amber-50 p-3">
                            <div className="flex items-start gap-2">
                                <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0 text-amber-600" />
                                <p className="text-xs text-amber-800">
                                    Copy this key now — it will not be shown
                                    again.
                                </p>
                            </div>
                        </div>
                        <div className="rounded-lg bg-gray-50 p-3 font-mono text-xs break-all text-gray-800">
                            {newKeyValue}
                        </div>
                    </div>
                ) : (
                    <div className="space-y-3">
                        <Input
                            label="Key name"
                            placeholder="e.g. Production, Development"
                            value={newKeyName}
                            onChange={(e) => setNewKeyName(e.target.value)}
                        />
                        <Button
                            variant="primary"
                            size="md"
                            fullWidth
                            onClick={handleCreateKey}
                            disabled={!newKeyName.trim()}
                        >
                            Create Key
                        </Button>
                    </div>
                )}
            </Modal>

            <Modal
                open={showLogoutModal}
                onClose={() => setShowLogoutModal(false)}
                title="Sign Out"
                size="sm"
                footer={
                    <>
                        <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => setShowLogoutModal(false)}
                        >
                            Cancel
                        </Button>
                        <Button
                            variant="danger"
                            size="sm"
                            onClick={handleLogoutAll}
                            className="gap-1.5"
                        >
                            <LogOut className="h-3.5 w-3.5" />
                            Sign Out
                        </Button>
                    </>
                }
            >
                <p className="text-sm text-gray-600">
                    Are you sure you want to sign out? You will need to log in
                    again to access your account.
                </p>
            </Modal>

            <Modal
                open={showDeleteModal}
                onClose={() => setShowDeleteModal(false)}
                title="Delete Account"
                size="sm"
                footer={
                    <>
                        <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => setShowDeleteModal(false)}
                        >
                            Cancel
                        </Button>
                        <Button
                            variant="danger"
                            size="sm"
                            onClick={handleDeleteAccount}
                            className="gap-1.5"
                        >
                            <Trash2 className="h-3.5 w-3.5" />
                            Delete Account
                        </Button>
                    </>
                }
            >
                <div className="space-y-3">
                    <div className="rounded-xl bg-red-50 p-3">
                        <div className="flex items-start gap-2">
                            <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0 text-red-600" />
                            <p className="text-xs text-red-800">
                                This action is permanent and cannot be undone.
                                All your data will be permanently deleted.
                            </p>
                        </div>
                    </div>
                    <p className="text-sm text-gray-600">
                        Type <span className="font-semibold">DELETE</span> to
                        confirm.
                    </p>
                    <input
                        type="text"
                        placeholder='Type "DELETE" to confirm'
                        className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-red-500 focus:ring-2 focus:ring-red-100"
                    />
                </div>
            </Modal>

            <div className="pb-8" />
        </div>
    );
}
