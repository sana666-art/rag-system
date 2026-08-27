import {
    BarChart3,
    LayoutGrid,
    LineChart,
    PieChart,
} from "lucide-react";
import { motion } from "framer-motion";

const SUGGESTIONS = [
    {
        icon: PieChart,
        title: "Show my portfolio",
        description: "See my current holdings and their values",
        prompt: "Show my portfolio",
    },
    {
        icon: LineChart,
        title: "Analyze trades",
        description: "Break down my recent buys and sells",
        prompt: "Analyze my recent trades",
    },
    {
        icon: BarChart3,
        title: "Explain my P/L",
        description: "Understand how my investments performed",
        prompt: "Explain my P/L",
    },
    {
        icon: LayoutGrid,
        title: "Summarize holdings",
        description: "Get a quick overview of my account",
        prompt: "Summarize my holdings",
    },
];

function greeting() {
    const hour = new Date().getHours();
    if (hour < 12) return "Good morning";
    if (hour < 17) return "Good afternoon";
    return "Good evening";
}

export default function EmptyState({ onSuggest }) {

    return (
        <div className="flex min-h-full flex-col items-center justify-center px-6 py-12 text-center">
            <div className="mb-8 flex items-center gap-2 text-gray-900">
                <svg
                    viewBox="0 0 24 24"
                    className="h-6 w-6"
                    fill="none"
                    aria-hidden="true"
                >
                    <path
                        d="M12 2 3.5 6.5v11L12 22l8.5-4.5v-11L12 2Z"
                        stroke="currentColor"
                        strokeWidth="1.5"
                    />
                    <path
                        d="M12 22v-9.5M3.5 6.5 12 12l8.5-5.5"
                        stroke="currentColor"
                        strokeWidth="1.5"
                    />
                </svg>
                <span className="text-sm font-semibold">RAG Assistant</span>
            </div>

            <h2 className="text-2xl font-semibold tracking-tight text-gray-900">
                {greeting()}
            </h2>

            <p className="mt-1 text-2xl font-semibold tracking-tight text-gray-900">
                How can I help you today?
            </p>

            <div className="mt-8 grid w-full max-w-2xl grid-cols-1 gap-3 sm:grid-cols-2">
                {SUGGESTIONS.map((suggestion, index) => (
                    <motion.button
                        key={suggestion.title}
                        type="button"
                        onClick={() => onSuggest?.(suggestion.prompt)}
                        initial={{ opacity: 0, y: 8 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.05 * index, duration: 0.25 }}
                        className="flex items-start gap-3 rounded-2xl border border-gray-200 p-4 text-left transition hover:bg-gray-50"
                    >
                        <suggestion.icon className="mt-0.5 h-5 w-5 shrink-0 text-gray-500" />
                        <span className="min-w-0">
                            <span className="block text-sm font-semibold text-gray-900">
                                {suggestion.title}
                            </span>
                            <span className="mt-0.5 block text-xs leading-relaxed text-gray-500">
                                {suggestion.description}
                            </span>
                        </span>
                    </motion.button>
                ))}
            </div>
        </div>
    );
}
