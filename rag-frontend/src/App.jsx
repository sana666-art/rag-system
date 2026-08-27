import { lazy, Suspense } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import ProtectedRoute from "./routes/ProtectedRoute";
import ChatLayout from "./layouts/ChatLayout";
import DashboardLayout from "./layouts/DashboardLayout";
import Spinner from "./components/ui/Spinner";

const Landing = lazy(() => import("./pages/Landing"));
const GuestChat = lazy(() => import("./pages/GuestChat"));
const Login = lazy(() => import("./pages/auth/Login"));
const Register = lazy(() => import("./pages/auth/Register"));
const VerifyEmail = lazy(() => import("./pages/auth/VerifyEmail"));
const TwoFactor = lazy(() => import("./pages/auth/TwoFactor"));
const Chat = lazy(() => import("./pages/dashboard/Chat"));
const Profile = lazy(() => import("./pages/dashboard/Profile"));
const Settings = lazy(() => import("./pages/dashboard/Settings"));
const NotFound = lazy(() => import("./pages/NotFound"));

function PageFallback() {
    return (
        <div className="flex min-h-screen items-center justify-center bg-white">
            <Spinner />
        </div>
    );
}

function App() {

    return (
        <Suspense fallback={<PageFallback />}>
            <Routes>

            <Route
                path="/"
                element={<Landing />}
            />

            <Route
                path="/guest-chat"
                element={<GuestChat />}
            />

            <Route
                path="/login"
                element={<Login />}
            />

            <Route
                path="/register"
                element={<Register />}
            />

            <Route
                path="/verify-email"
                element={<VerifyEmail />}
            />

            <Route
                path="/2fa"
                element={<TwoFactor />}
            />

            <Route element={<ProtectedRoute />}>
                <Route element={<ChatLayout />}>
                    <Route
                        path="/dashboard/chat"
                        element={<Chat />}
                    />
                </Route>
            </Route>

            <Route element={<ProtectedRoute />}>
                <Route element={<DashboardLayout />}>
                    <Route
                        path="/dashboard/profile"
                        element={<Profile />}
                    />
                    <Route
                        path="/dashboard/settings"
                        element={<Settings />}
                    />
                </Route>
            </Route>

            <Route
                path="/chat"
                element={<Navigate to="/dashboard/chat" replace />}
            />

            <Route
                path="*"
                element={<NotFound />}
            />

            </Routes>
        </Suspense>
    );
}

export default App;
