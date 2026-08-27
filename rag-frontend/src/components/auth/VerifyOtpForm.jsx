import { useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import toast from "react-hot-toast";
import Button from "../ui/Button";
import OTPInput from "./OTPInput";

const otpSchema = z.object({
    code: z.string().length(6, "Enter the 6-digit code"),
});

export default function VerifyOtpForm({ buttonLabel = "Verify", onSubmit, onResend }) {

    const [submitting, setSubmitting] = useState(false);
    const [resending, setResending] = useState(false);

    const { control, handleSubmit, formState: { errors } } = useForm({
        resolver: zodResolver(otpSchema),
        defaultValues: { code: "" },
    });

    const submitCode = async ({ code }) => {

        setSubmitting(true);

        try {
            await onSubmit(code);
        } catch (error) {
            toast.error(error.response?.data?.message || "Something went wrong");
        } finally {
            setSubmitting(false);
        }

    };

    const resendCode = async () => {

        setResending(true);

        try {
            await onResend();
            toast.success("A new verification code has been sent");
        } catch (error) {
            toast.error(error.response?.data?.message || "Something went wrong");
        } finally {
            setResending(false);
        }

    };

    return (
        <form onSubmit={handleSubmit(submitCode)} className="space-y-6">
            <Controller
                control={control}
                name="code"
                render={({ field }) => (
                    <OTPInput value={field.value} onChange={field.onChange} />
                )}
            />

            {errors.code && (
                <p className="text-center text-sm text-red-500">
                    {errors.code.message}
                </p>
            )}

            <Button type="submit" loading={submitting}>
                {buttonLabel}
            </Button>

            {onResend && (
                <button
                    type="button"
                    onClick={resendCode}
                    disabled={resending}
                    className="w-full text-center text-sm font-medium text-gray-900 hover:underline disabled:opacity-60"
                >
                    {resending ? "Resending..." : "Resend code"}
                </button>
            )}
        </form>
    );
}
