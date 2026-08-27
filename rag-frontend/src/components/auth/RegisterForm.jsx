import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { registerSchema } from "../../schemas/authSchemas";
import authService from "../../services/authService";
import Button from "../ui/Button";
import Input from "../ui/Input";

export default function RegisterForm() {

    const navigate = useNavigate();
    const [submitting, setSubmitting] = useState(false);

    const { register, handleSubmit, formState: { errors } } = useForm({
        resolver: zodResolver(registerSchema),
    });

    const onSubmit = async (data) => {

        setSubmitting(true);

        try {
            await authService.register(data);
            toast.success("Account created. Check your email for the verification code.");
            navigate("/verify-email");
        } catch (error) {
            toast.error(error.response?.data?.message || "Registration failed");
        } finally {
            setSubmitting(false);
        }

    };

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input
                label="Full name"
                type="text"
                placeholder="John Doe"
                error={errors.fullName?.message}
                {...register("fullName")}
            />

            <Input
                label="Email"
                type="email"
                placeholder="you@example.com"
                error={errors.email?.message}
                {...register("email")}
            />

            <Input
                label="Password"
                type="password"
                placeholder="At least 8 characters"
                error={errors.password?.message}
                {...register("password")}
            />

            <Input
                label="Confirm password"
                type="password"
                placeholder="Repeat your password"
                error={errors.confirmPassword?.message}
                {...register("confirmPassword")}
            />

            <Button type="submit" loading={submitting}>
                Create account
            </Button>
        </form>
    );
}
