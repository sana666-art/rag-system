import { Link } from "react-router-dom";
import AuthLayout from "../../layouts/AuthLayout";
import LoginForm from "../../components/auth/LoginForm";

export default function Login() {

    return (
        <AuthLayout
            title="Welcome back"
            subtitle="Log in to continue chatting with your data"
        >
            <LoginForm />

            <p className="mt-6 text-center text-sm text-gray-500">
                Don't have an account?{" "}
                <Link
                    to="/register"
                    className="font-medium text-gray-900 hover:underline"
                >
                    Register
                </Link>
            </p>

            <p className="mt-3 text-center text-xs text-gray-400">
                <Link
                    to="/"
                    className="hover:text-gray-600 hover:underline"
                >
                    ← Back to home
                </Link>
            </p>
        </AuthLayout>
    );
}
