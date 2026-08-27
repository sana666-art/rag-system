import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import AuthLayout from "../../layouts/AuthLayout";
import VerifyOtpForm from "../../components/auth/VerifyOtpForm";
import authService from "../../services/authService";
import { useAuth } from "../../hooks/useAuth";
import { getEmail } from "../../utils/storage";

export default function TwoFactor() {

    const navigate = useNavigate();
    const { login } = useAuth();
    const email = getEmail();

    useEffect(() => {

        if (!email) {
            navigate("/login", { replace: true });
        }

    }, [email, navigate]);

    const handleVerify = async (code) => {

        const result = await authService.verify2FA({ email, code });

        login(result.user);

        toast.success("Welcome back!");

        navigate("/dashboard/chat");

    };

    if (!email) {
        return null;
    }

    return (
        <AuthLayout
            title="Two-factor authentication"
            subtitle={`Enter the 6-digit code sent to ${email}`}
        >
            <VerifyOtpForm
                buttonLabel="Verify & Login"
                onSubmit={handleVerify}
            />
        </AuthLayout>
    );
}
