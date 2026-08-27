import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import AuthLayout from "../../layouts/AuthLayout";
import VerifyOtpForm from "../../components/auth/VerifyOtpForm";
import authService from "../../services/authService";
import { getEmail } from "../../utils/storage";

export default function VerifyEmail() {

    const navigate = useNavigate();
    const email = getEmail();

    useEffect(() => {

        if (!email) {
            navigate("/register", { replace: true });
        }

    }, [email, navigate]);

    const handleVerify = async (otp) => {

        await authService.verifyEmail({ email, otp });

        toast.success("Email verified. You can now log in.");

        navigate("/login");

    };

    const handleResend = async () => {

        await authService.resendVerification({ email });

        toast.success("A new verification code has been sent");

    };

    if (!email) {
        return null;
    }

    return (
        <AuthLayout
            title="Verify your email"
            subtitle={`Enter the verification code sent to ${email}`}
        >
            <VerifyOtpForm onSubmit={handleVerify} onResend={handleResend} />
        </AuthLayout>
    );
}
