import { Navigate, Outlet } from "react-router-dom";
import Spinner from "../components/ui/Spinner";
import { useAuth } from "../hooks/useAuth";

export default function ProtectedRoute() {

    const { loading, isAuthenticated } = useAuth();

    if (loading) {
        return (
            <div className="flex h-screen items-center justify-center bg-gray-50">
                <Spinner className="h-8 w-8" />
            </div>
        );
    }

    return isAuthenticated
        ? <Outlet />
        : <Navigate to="/login" replace />;
}
