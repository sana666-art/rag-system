import { Link } from "react-router-dom";
import AuthLayout from "../../layouts/AuthLayout";
import RegisterForm from "../../components/auth/RegisterForm";

export default function Register() {

    return (
        <AuthLayout
            title="Create your account"
            subtitle="Join RAG Assistant and start chatting with your data"
        >
            <RegisterForm />

            <p className="mt-6 text-center text-sm text-gray-500">
                Already have an account?{" "}
                <Link
                    to="/login"
                    className="font-medium text-gray-900 hover:underline"
                >
                    Login
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
