import { Bot } from "lucide-react";

const links = [
    { href: "#features", label: "Features" },
    { href: "#how-it-works", label: "How it works" },
    { href: "#pricing", label: "Pricing" },
    { href: "#faq", label: "FAQ" },
    { href: "/guest-chat", label: "Try it" },
];

export default function Footer() {

    return (
        <footer className="border-t border-gray-200 bg-white py-10">
            <div className="mx-auto flex max-w-7xl flex-col items-center justify-between gap-4 px-6 sm:flex-row">
                <div className="flex items-center gap-2">
                    <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-gray-900 text-white">
                        <Bot className="h-4 w-4" />
                    </div>
                    <span className="text-sm font-semibold text-gray-900">
                        RAG Assistant
                    </span>
                </div>

                <p className="text-xs text-gray-400">
                    © {new Date().getFullYear()} RAG Assistant. All rights reserved.
                </p>

                <div className="flex items-center gap-6 text-sm text-gray-500">
                    {links.map((link) => (
                        <a
                            key={link.href}
                            href={link.href}
                            className="transition hover:text-gray-900"
                        >
                            {link.label}
                        </a>
                    ))}
                </div>
            </div>
        </footer>
    );
}
