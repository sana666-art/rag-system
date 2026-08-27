import { useState } from "react";
import { ChevronDown } from "lucide-react";
import { AnimatePresence, motion } from "framer-motion";
import clsx from "clsx";

const faqs = [
    {
        question: "What is RAG Assistant?",
        answer:
            "RAG Assistant is a retrieval-augmented chatbot for your simulated portfolio. It indexes your transactions, deposits and holdings as embeddings, then answers questions in plain English with citations you can verify.",
    },
    {
        question: "Do I need an account to try it?",
        answer:
            "No. The guest demo gives you 5 free questions a day against a sample portfolio. Creating an account lets you ask questions about your own portfolio and keeps your chat history.",
    },
    {
        question: "How does the free plan work?",
        answer:
            "Free accounts get 100 questions per day, full chat history, and retrieval grounded in your own data. It never expires and requires no credit card.",
    },
    {
        question: "Where do the answers come from?",
        answer:
            "Answers are grounded in your own portfolio documents. Your question is embedded and matched with vector similarity, then the model composes a response from only the retrieved context — it doesn't guess from other users' data.",
    },
    {
        question: "What data does the assistant see?",
        answer:
            "Only your own account's documents. Retrieval is strictly scoped per user, so your portfolio is never exposed to other users or to guest sessions.",
    },
    {
        question: "Can I cancel or change plans anytime?",
        answer:
            "Yes. Upgrade, downgrade or cancel at any time from your settings — there are no contracts or lock-ins.",
    },
];

function FaqItem({ faq }) {

    const [open, setOpen] = useState(false);

    return (
        <div className="border-b border-gray-200">
            <button
                type="button"
                onClick={() => setOpen((v) => !v)}
                className="flex w-full items-center justify-between gap-4 py-5 text-left"
            >
                <span className="text-base font-medium text-gray-900">
                    {faq.question}
                </span>

                <ChevronDown
                    className={clsx(
                        "h-5 w-5 shrink-0 text-gray-400 transition-transform",
                        open && "rotate-180"
                    )}
                />
            </button>

            <AnimatePresence initial={false}>
                {open && (
                    <motion.div
                        initial={{ height: 0, opacity: 0 }}
                        animate={{ height: "auto", opacity: 1 }}
                        exit={{ height: 0, opacity: 0 }}
                        transition={{ duration: 0.2, ease: "easeOut" }}
                        className="overflow-hidden"
                    >
                        <p className="pb-5 pr-10 text-sm leading-relaxed text-gray-600">
                            {faq.answer}
                        </p>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
}

export default function FAQ() {

    return (
        <section id="faq" className="bg-white py-24">
            <div className="mx-auto max-w-3xl px-6">
                <div className="mx-auto mb-10 max-w-2xl text-center">
                    <h2 className="text-3xl font-bold tracking-tight text-gray-900">
                        Frequently asked questions
                    </h2>
                    <p className="mt-3 text-gray-500">
                        Everything you need to know before getting started.
                    </p>
                </div>

                <div className="border-t border-gray-200">
                    {faqs.map((faq) => (
                        <FaqItem key={faq.question} faq={faq} />
                    ))}
                </div>
            </div>
        </section>
    );
}
