import { useNavigate } from "react-router-dom";
import clsx from "clsx";
import Button from "../ui/Button";

const plans = [
    {
        name: "Free",
        price: "$0",
        period: "forever",
        description: "Explore the assistant with a sample portfolio.",
        features: [
            "5 questions per day",
            "Sample portfolio access",
            "Grounded, cited answers",
            "No account needed",
        ],
        cta: "Get started",
        highlighted: false,
    },
    {
        name: "Plus",
        price: "$9",
        period: "per month",
        description: "For individual investors who want their own data.",
        features: [
            "100 questions per day",
            "Your own portfolio",
            "Full chat history",
            "Rename and manage chats",
            "Priority response queue",
        ],
        cta: "Try Plus",
        highlighted: true,
    },
    {
        name: "Pro",
        price: "$20",
        period: "per month",
        description: "For power users who live in their numbers.",
        features: [
            "Unlimited questions",
            "Advanced analytics",
            "Higher quality retrieval",
            "Everything in Plus",
        ],
        cta: "Go Pro",
        highlighted: false,
    },
];

export default function Pricing() {

    const navigate = useNavigate();

    return (
        <section id="pricing" className="bg-gray-50 py-24">
            <div className="mx-auto max-w-6xl px-6">
                <div className="mx-auto max-w-2xl text-center">
                    <h2 className="text-3xl font-bold tracking-tight text-gray-900">
                        Simple pricing
                    </h2>
                    <p className="mt-3 text-gray-500">
                        Start free and upgrade when you're ready. No hidden fees.
                    </p>
                </div>

                <div className="mt-14 grid gap-6 lg:grid-cols-3">
                    {plans.map((plan) => (
                        <div
                            key={plan.name}
                            className={clsx(
                                "flex flex-col rounded-2xl border bg-white p-8",
                                plan.highlighted
                                    ? "border-gray-900 shadow-lg"
                                    : "border-gray-200"
                            )}
                        >
                            <h3 className="text-lg font-semibold text-gray-900">
                                {plan.name}
                            </h3>

                            <p className="mt-1 text-sm text-gray-500">
                                {plan.description}
                            </p>

                            <div className="mt-6 flex items-baseline gap-1.5">
                                <span className="text-4xl font-bold tracking-tight text-gray-900">
                                    {plan.price}
                                </span>
                                <span className="text-sm text-gray-500">
                                    {plan.period}
                                </span>
                            </div>

                            <ul className="mt-8 flex-1 space-y-3">
                                {plan.features.map((feature) => (
                                    <li
                                        key={feature}
                                        className="flex items-start gap-2.5 text-sm text-gray-600"
                                    >
                                        <svg
                                            viewBox="0 0 20 20"
                                            className="mt-0.5 h-4 w-4 shrink-0 text-gray-900"
                                            fill="currentColor"
                                            aria-hidden="true"
                                        >
                                            <path
                                                fillRule="evenodd"
                                                d="M16.7 5.3a1 1 0 0 1 0 1.4l-7.5 7.5a1 1 0 0 1-1.4 0l-3.5-3.5a1 1 0 1 1 1.4-1.4l2.8 2.79 6.8-6.8a1 1 0 0 1 1.4 0Z"
                                                clipRule="evenodd"
                                            />
                                        </svg>
                                        {feature}
                                    </li>
                                ))}
                            </ul>

                            <Button
                                variant={plan.highlighted ? "primary" : "secondary"}
                                onClick={() => navigate("/register")}
                                className={clsx(
                                    "mt-8 w-full",
                                    plan.highlighted &&
                                        "bg-gray-900! hover:bg-black!"
                                )}
                            >
                                {plan.cta}
                            </Button>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
}
