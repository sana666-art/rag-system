import {
    Database,
    MessageSquareText,
    Fingerprint,
    Zap,
} from "lucide-react";

const features = [
    {
        icon: Database,
        title: "Semantic search over your data",
        description:
            "Every transaction, deposit and portfolio note is embedded and indexed with pgvector. Questions match meaning, not just keywords.",
    },
    {
        icon: Fingerprint,
        title: "Answers you can verify",
        description:
            "Each answer cites its sources with similarity scores. Expand a source to inspect the exact document that informed the response.",
    },
    {
        icon: Zap,
        title: "Grounded in your account",
        description:
            "Retrieval is scoped to your data only — the model never sees another user's portfolio.",
    },
    {
        icon: MessageSquareText,
        title: "Chat that remembers",
        description:
            "Conversation history is saved per session. Pick up where you left off, rename, or start fresh anytime.",
    },
];

export default function Features() {

    return (
        <section id="features" className="bg-gray-50 py-24">
            <div className="mx-auto max-w-7xl px-6">
                <div className="mx-auto max-w-2xl text-center">
                    <h2 className="text-3xl font-bold tracking-tight text-gray-900">
                        Everything you need to understand your portfolio
                    </h2>
                    <p className="mt-3 text-gray-500">
                        A retrieval-augmented assistant built for real financial
                        data.
                    </p>
                </div>

                <div className="mt-14 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
                    {features.map((feature) => (
                        <div
                            key={feature.title}
                            className="rounded-2xl border border-gray-200 bg-white p-6 transition hover:border-gray-300 hover:shadow-sm"
                        >
                            <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-gray-100">
                                <feature.icon className="h-5 w-5 text-gray-900" />
                            </div>

                            <h3 className="mt-4 text-base font-semibold text-gray-900">
                                {feature.title}
                            </h3>

                            <p className="mt-2 text-sm leading-relaxed text-gray-600">
                                {feature.description}
                            </p>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
}
