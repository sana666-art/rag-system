import { useNavigate } from "react-router-dom";
import { ArrowRight, Lock } from "lucide-react";
import Button from "../ui/Button";

export default function CTA() {

    const navigate = useNavigate();

    return (
        <section className="bg-white py-20">
            <div className="mx-auto max-w-4xl px-6">
                <div className="overflow-hidden rounded-3xl bg-gradient-to-br from-indigo-600 to-violet-600 px-8 py-14 text-center shadow-xl shadow-indigo-200">
                    <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-white/10">
                        <Lock className="h-6 w-6 text-white" />
                    </div>

                    <h2 className="text-3xl font-bold tracking-tight text-white">
                        Unlock unlimited portfolio insights
                    </h2>

                    <p className="mx-auto mt-3 max-w-xl text-indigo-100">
                        Create a free account to keep your chat history, ask up to
                        100 questions a day, and get answers grounded in your own
                        portfolio.
                    </p>

                    <div className="mt-8 flex flex-col items-center justify-center gap-3 sm:flex-row">
                        <Button
                            size="lg"
                            variant="secondary"
                            onClick={() => navigate("/register")}
                            className="gap-2 !text-indigo-700"
                        >
                            Create free account
                            <ArrowRight className="h-4 w-4" />
                        </Button>

                        <Button
                            size="lg"
                            className="bg-white/10 !text-white hover:bg-white/20"
                            onClick={() => navigate("/login")}
                        >
                            I already have an account
                        </Button>
                    </div>
                </div>
            </div>
        </section>
    );
}
