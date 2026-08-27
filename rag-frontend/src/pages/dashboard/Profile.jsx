import { useState, useCallback } from "react";
import {
    BadgeCheck,
    Calendar,
    CheckCircle2,
    Edit3,
    Gauge,
    Mail,
    MessageSquare,
    RefreshCw,
    ShieldCheck,
    Sparkles,
    TrendingUp,
    User as UserIcon,
    Clock,
    ArrowUpRight,
} from "lucide-react";
import toast from "react-hot-toast";
import Card from "../../components/ui/Card";
import Badge from "../../components/ui/Badge";
import Avatar from "../../components/ui/Avatar";
import Button from "../../components/ui/Button";
import Input from "../../components/ui/Input";
import Modal from "../../components/ui/Modal";
import Divider from "../../components/ui/Divider";
import { useAuth } from "../../hooks/useAuth";

function StatCard({ icon: Icon, label, value, sub, color = "bg-gray-100" }) {
    return (
        <div className="flex items-center gap-3 rounded-xl border border-gray-100 bg-white p-4">
            <div
                className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-xl ${color}`}
            >
                <Icon className="h-5 w-5 text-gray-600" />
            </div>
            <div className="min-w-0">
                <p className="text-xl font-bold tracking-tight text-gray-900">
                    {value}
                </p>
                <p className="text-xs text-gray-500">{label}</p>
            </div>
            {sub && (
                <span className="ml-auto text-xs text-gray-400">{sub}</span>
            )}
        </div>
    );
}

function InfoRow({ icon: Icon, label, children }) {
    return (
        <div className="flex items-center justify-between py-3.5">
            <span className="flex items-center gap-2.5 text-sm text-gray-500">
                <Icon className="h-4 w-4 text-gray-400" />
                {label}
            </span>
            <span className="text-sm font-medium text-gray-800">
                {children}
            </span>
        </div>
    );
}

function formatDate(value) {
    if (!value) return "—";
    return new Date(value).toLocaleDateString([], {
        year: "numeric",
        month: "short",
        day: "numeric",
    });
}

function formatRelative(value) {
    if (!value) return "—";
    const diff = Date.now() - new Date(value).getTime();
    const days = Math.floor(diff / 86400000);
    if (days === 0) return "Today";
    if (days === 1) return "Yesterday";
    if (days < 30) return `${days}d ago`;
    if (days < 365) return `${Math.floor(days / 30)}mo ago`;
    return `${Math.floor(days / 365)}y ago`;
}

export default function Profile() {
    const { user, refreshProfile, setCurrentUser } = useAuth();
    const [refreshing, setRefreshing] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [editName, setEditName] = useState(user?.fullName || "");
    const [saving, setSaving] = useState(false);

    const handleRefresh = async () => {
        setRefreshing(true);
        try {
            await refreshProfile();
            toast.success("Profile refreshed");
        } catch {
            toast.error("Failed to refresh");
        } finally {
            setRefreshing(false);
        }
    };

    const handleSaveProfile = async () => {
        if (!editName.trim()) return;
        setSaving(true);
        try {
            setCurrentUser({ fullName: editName.trim() });
            toast.success("Profile updated");
            setShowEditModal(false);
        } catch {
            toast.error("Failed to update profile");
        } finally {
            setSaving(false);
        }
    };

    const isPro = user?.subscriptionPlan === "PRO_MONTHLY";
    const unlimited = user?.quotaLimit === -1;
    const quotaUsed = user?.quotaLimit != null && !unlimited
        ? user.quotaLimit - (user.remainingQuota ?? 0)
        : 0;
    const quotaTotal = unlimited ? "∞" : user?.quotaLimit ?? "—";
    const quotaPercent = unlimited
        ? 100
        : user?.quotaLimit
          ? Math.round(
                ((user.quotaLimit - (user.remainingQuota ?? 0)) /
                    user.quotaLimit) *
                    100
            )
          : 0;

    return (
        <div className="mx-auto max-w-3xl space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-bold tracking-tight text-gray-900">
                        Profile
                    </h1>
                    <p className="mt-1 text-sm text-gray-500">
                        Your account information and usage overview
                    </p>
                </div>

                <Button
                    variant="secondary"
                    size="sm"
                    onClick={handleRefresh}
                    loading={refreshing}
                    className="gap-1.5"
                >
                    <RefreshCw className="h-3.5 w-3.5" />
                    Refresh
                </Button>
            </div>

            <Card className="px-0! py-0! overflow-hidden">
                <div className="relative h-28 bg-linear-to-br from-gray-800 via-gray-900 to-black">
                    <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiNmZmYiIGZpbGwtb3BhY2l0eT0iMC4wNCI+PHBhdGggZD0iTTM2IDM0djItSDJ2LTJoMzR6TTAgNHYySDE0VjRIMHptMzYgMTZ2MkgyVjE2aDM0em0wIDE2djJoLTJ2LTJoMnoiLz48L2c+PC9nPjwvc3ZnPg==')] opacity-40" />
                </div>

                <div className="relative px-6 pb-6">
                    <div className="flex flex-col items-center -mt-12">
                        <div className="relative">
                            <div className="rounded-full bg-white p-1 shadow-lg ring-4 ring-white">
                                <Avatar
                                    name={user?.fullName || user?.email}
                                    size="lg"
                                    className="h-20! w-20! text-2xl!"
                                />
                            </div>
                            <button
                                onClick={() => {
                                    setEditName(
                                        user?.fullName || ""
                                    );
                                    setShowEditModal(true);
                                }}
                                className="absolute -bottom-1 -right-1 flex h-7 w-7 items-center justify-center rounded-full bg-gray-900 text-white shadow-md transition hover:bg-black hover:scale-110"
                                title="Edit profile"
                            >
                                <Edit3 className="h-3.5 w-3.5" />
                            </button>
                        </div>

                        <h2 className="mt-3 text-xl font-bold text-gray-900">
                            {user?.fullName || "RAG Assistant User"}
                        </h2>

                        <p className="mt-0.5 flex items-center gap-1.5 text-sm text-gray-500">
                            <Mail className="h-3.5 w-3.5" />
                            {user?.email}
                        </p>

                        <div className="mt-3 flex items-center gap-2">
                            <Badge variant={isPro ? "indigo" : "green"}>
                                {isPro ? (
                                    <>
                                        <Sparkles className="h-3 w-3" />
                                        PRO
                                    </>
                                ) : (
                                    "FREE"
                                )}
                            </Badge>

                            {user?.role === "ADMIN" && (
                                <Badge variant="amber">
                                    <UserIcon className="h-3 w-3" />
                                    ADMIN
                                </Badge>
                            )}

                            {user?.isEmailVerified && (
                                <Badge variant="green">
                                    <CheckCircle2 className="h-3 w-3" />
                                    Verified
                                </Badge>
                            )}
                        </div>
                    </div>
                </div>
            </Card>

            <div className="grid grid-cols-3 gap-3">
                <StatCard
                    icon={MessageSquare}
                    label="Questions Today"
                    value={quotaUsed}
                    sub={`/ ${quotaTotal}`}
                    color="bg-indigo-50"
                />
                <StatCard
                    icon={Gauge}
                    label="Remaining"
                    value={unlimited ? "∞" : user?.remainingQuota ?? 0}
                    sub={unlimited ? "" : "today"}
                    color="bg-emerald-50"
                />
                <StatCard
                    icon={TrendingUp}
                    label="Usage"
                    value={`${quotaPercent}%`}
                    color="bg-amber-50"
                />
            </div>

            <Card className="px-0! py-0!">
                <div className="px-6 pt-5 pb-1">
                    <h3 className="text-sm font-semibold text-gray-900">
                        Account Details
                    </h3>
                    <p className="mt-0.5 text-xs text-gray-500">
                        Personal information tied to your account
                    </p>
                </div>

                <div className="divide-y divide-gray-100 px-6">
                    <InfoRow icon={UserIcon} label="Full Name">
                        {user?.fullName || "—"}
                    </InfoRow>

                    <InfoRow icon={Mail} label="Email">
                        {user?.email || "—"}
                    </InfoRow>

                    <InfoRow icon={ShieldCheck} label="Two-Factor">
                        <span
                            className={
                                user?.twoFactorEnabled
                                    ? "rounded-full bg-green-100 px-2.5 py-0.5 text-xs font-medium text-green-700"
                                    : "rounded-full bg-gray-100 px-2.5 py-0.5 text-xs font-medium text-gray-600"
                            }
                        >
                            {user?.twoFactorEnabled ? "Enabled" : "Disabled"}
                        </span>
                    </InfoRow>

                    <InfoRow icon={Sparkles} label="Subscription">
                        <span
                            className={
                                isPro
                                    ? "rounded-full bg-indigo-100 px-2.5 py-0.5 text-xs font-medium text-indigo-700"
                                    : "rounded-full bg-gray-100 px-2.5 py-0.5 text-xs font-medium text-gray-600"
                            }
                        >
                            {isPro ? "PRO Monthly" : "Free Tier"}
                        </span>
                    </InfoRow>

                    <InfoRow icon={BadgeCheck} label="Account Created">
                        <div className="flex items-center gap-1.5">
                            {formatDate(user?.createdAt)}
                            <span className="text-xs text-gray-400">
                                ({formatRelative(user?.createdAt)})
                            </span>
                        </div>
                    </InfoRow>
                </div>
            </Card>

            <Card className="px-0! py-0!">
                <div className="px-6 pt-5 pb-1">
                    <h3 className="text-sm font-semibold text-gray-900">
                        Quick Actions
                    </h3>
                    <p className="mt-0.5 text-xs text-gray-500">
                        Common account management tasks
                    </p>
                </div>

                <div className="divide-y divide-gray-100 px-6">
                    <a
                        href="/dashboard/settings"
                        className="flex items-center justify-between py-3.5 group"
                    >
                        <div className="flex items-center gap-2.5">
                            <ShieldCheck className="h-4 w-4 text-gray-400" />
                            <span className="text-sm font-medium text-gray-800 group-hover:text-gray-900">
                                Security Settings
                            </span>
                        </div>
                        <ArrowUpRight className="h-4 w-4 text-gray-400 transition group-hover:text-gray-600 group-hover:translate-x-0.5 group-hover:-translate-y-0.5" />
                    </a>

                    <a
                        href="/dashboard/settings"
                        className="flex items-center justify-between py-3.5 group"
                    >
                        <div className="flex items-center gap-2.5">
                            <Gauge className="h-4 w-4 text-gray-400" />
                            <span className="text-sm font-medium text-gray-800 group-hover:text-gray-900">
                                Usage & Billing
                            </span>
                        </div>
                        <ArrowUpRight className="h-4 w-4 text-gray-400 transition group-hover:text-gray-600 group-hover:translate-x-0.5 group-hover:-translate-y-0.5" />
                    </a>

                    <a
                        href="/dashboard/chat"
                        className="flex items-center justify-between py-3.5 group"
                    >
                        <div className="flex items-center gap-2.5">
                            <MessageSquare className="h-4 w-4 text-gray-400" />
                            <span className="text-sm font-medium text-gray-800 group-hover:text-gray-900">
                                Go to Chat
                            </span>
                        </div>
                        <ArrowUpRight className="h-4 w-4 text-gray-400 transition group-hover:text-gray-600 group-hover:translate-x-0.5 group-hover:-translate-y-0.5" />
                    </a>
                </div>
            </Card>

            <Modal
                open={showEditModal}
                onClose={() => setShowEditModal(false)}
                title="Edit Profile"
                size="sm"
                footer={
                    <>
                        <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => setShowEditModal(false)}
                        >
                            Cancel
                        </Button>
                        <Button
                            variant="primary"
                            size="sm"
                            onClick={handleSaveProfile}
                            loading={saving}
                            className="gap-1.5"
                        >
                            Save Changes
                        </Button>
                    </>
                }
            >
                <div className="space-y-4">
                    <div className="flex justify-center">
                        <Avatar
                            name={editName || user?.email}
                            size="lg"
                            className="h-16! w-16! text-xl!"
                        />
                    </div>

                    <Input
                        label="Full Name"
                        value={editName}
                        onChange={(e) => setEditName(e.target.value)}
                        placeholder="Enter your full name"
                    />

                    <Input
                        label="Email"
                        value={user?.email || ""}
                        disabled
                        className="opacity-60 cursor-not-allowed"
                    />

                    <p className="text-xs text-gray-500">
                        Contact support to change your email address.
                    </p>
                </div>
            </Modal>

            <div className="pb-8" />
        </div>
    );
}
