import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { loginSchema } from "../../schemas/authSchemas";
import authService from "../../services/authService";
import { useAuth } from "../../hooks/useAuth";
import Button from "../ui/Button";
import Input from "../ui/Input";

export default function LoginForm() {

    const navigate = useNavigate();
    const { login } = useAuth();
    const [submitting, setSubmitting] = useState(false);

    const { register, handleSubmit, formState: { errors } } = useForm({
        resolver: zodResolver(loginSchema),
    });

    const onSubmit = async (data) => {

        setSubmitting(true);

        try {

            const result = await authService.login(data);

            if (result.requiresTwoFactor) {
                navigate("/2fa");
                return;
            }

            login(result.user);

            navigate("/dashboard/chat");

        } catch (error) {
            toast.error(error.response?.data?.message || "Login failed");
        } finally {
            setSubmitting(false);
        }

    };

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
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
                placeholder="••••••••"
                error={errors.password?.message}
                {...register("password")}
            />

            <Button type="submit" loading={submitting}>
                Login
            </Button>
        </form>
    );
}
