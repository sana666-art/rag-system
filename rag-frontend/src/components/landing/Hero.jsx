import { ArrowRight } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import Button from "../ui/Button";
import DemoComposer from "./DemoComposer";

export default function Hero() {

    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();

    return (
        <section id="top" className="bg-white">
            <div className="mx-auto max-w-4xl px-6 pb-20 pt-28 text-center">
                <h1 className="text-4xl font-bold tracking-tight text-gray-900 sm:text-5xl md:text-6xl">
                    Your portfolio,
                    <span className="block">answered instantly.</span>
                </h1>

                <p className="mx-auto mt-5 max-w-2xl text-lg text-gray-600">
                    Chat with your financial data. Ask about your positions, trades,
                    deposits and performance — get grounded answers with citations
                    you can verify.
                </p>

                {!isAuthenticated && <DemoComposer />}

                <div className="mt-10 flex flex-col items-center justify-center gap-3 sm:flex-row">
                    <Button
                        size="lg"
                        onClick={() => navigate(isAuthenticated ? "/dashboard/chat" : "/register")}
                        className="gap-2"
                    >
                        {isAuthenticated ? "Open dashboard" : "Start chatting free"}
                        <ArrowRight className="h-4 w-4" />
                    </Button>

                    {!isAuthenticated && (
                        <Button
                            size="lg"
                            variant="secondary"
                            onClick={() => navigate("/guest-chat")}
                        >
                            Try as guest
                        </Button>
                    )}
                </div>

                <p className="mt-5 text-xs text-gray-400">
                    No credit card required · 20 free questions a day as a guest
                </p>
            </div>
        </section>
    );
}
