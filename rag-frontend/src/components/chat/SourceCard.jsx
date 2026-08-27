import { useState } from "react";
import { ChevronDown } from "lucide-react";
import clsx from "clsx";

const dotColor = {
    SimulatedPortfolio: "bg-indigo-400",
    SimulatedPortfolioTransaction: "bg-emerald-400",
    SimulatedPortfolioTransactions: "bg-emerald-400",
    SimulatedPortfolioDeposit: "bg-amber-400",
    SimulatedPortfolioWithdrawal: "bg-amber-400",
    SimulatedPortfolioOption: "bg-sky-400",
    SimulatedPortfolioOptionsTransaction: "bg-sky-400",
};

const sourceLabel = (source) => {
    const map = {
        SimulatedPortfolio: "Portfolio",
        SimulatedPortfolioTransaction: "Transaction",
        SimulatedPortfolioTransactions: "Transactions",
        SimulatedPortfolioDeposit: "Deposit",
        SimulatedPortfolioWithdrawal: "Withdrawal",
        SimulatedPortfolioOption: "Option",
        SimulatedPortfolioOptionsTransaction: "Option Transaction",
    };
    return map[source] || source;
};

export default function SourceCard({ source }) {

    const [expanded, setExpanded] = useState(false);

    const similarityPct = Math.round(source.similarity * 100);
    const metadata = source.metadata || {};
    const hasContent = !!source.content;
    const snippet = hasContent && source.content.length > 160
        ? source.content.slice(0, 160) + "…"
        : source.content;

    return (
        <div className="overflow-hidden rounded-xl border border-gray-200 bg-white">
            <button
                type="button"
                onClick={() => setExpanded((v) => !v)}
                className="flex w-full items-center gap-2.5 px-3 py-2.5 text-left transition hover:bg-gray-50"
            >
                <span className="w-5 shrink-0 text-right text-xs font-medium text-gray-400">
                    #{source.rank ?? "?"}
                </span>

                <span
                    className={clsx(
                        "h-2 w-2 shrink-0 rounded-full",
                        dotColor[source.source] || "bg-gray-300"
                    )}
                />

                <span className="min-w-0 flex-1 truncate text-sm font-medium text-gray-700">
                    {sourceLabel(source.source)}
                </span>

                <span className="shrink-0 text-sm font-semibold text-gray-700">
                    {similarityPct}%
                </span>

                <ChevronDown
                    className={clsx(
                        "h-4 w-4 shrink-0 text-gray-400 transition-transform",
                        expanded && "rotate-180"
                    )}
                />
            </button>

            <div className="px-3 pb-2 text-xs text-gray-500">
                {hasContent ? snippet : "Source content unavailable for this archived message."}
            </div>

            {expanded && hasContent && (
                <div className="space-y-3 border-t border-gray-100 bg-gray-50 px-3 py-3">
                    <div>
                        <p className="mb-1 text-[11px] font-semibold uppercase tracking-wide text-gray-400">
                            Document content
                        </p>
                        <pre className="whitespace-pre-wrap rounded-lg bg-white p-2.5 text-xs leading-relaxed text-gray-700">
                            {source.content}
                        </pre>
                    </div>

                    <div className="flex flex-wrap gap-2 text-[11px] text-gray-500">
                        <span>Similarity: {source.similarity.toFixed(4)}</span>
                        <span>Source ID: {source.sourceId}</span>
                    </div>
                </div>
            )}
        </div>
    );
}
