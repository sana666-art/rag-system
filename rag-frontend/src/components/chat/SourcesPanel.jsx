import { useState } from "react";
import { ChevronDown } from "lucide-react";
import clsx from "clsx";
import SourceCard from "./SourceCard";

export default function SourcesPanel({ sources = [], maxVisible = 0 }) {

    const [collapsed, setCollapsed] = useState(false);
    const [showAll, setShowAll] = useState(false);

    if (!sources || sources.length === 0) return null;

    const hasMore = maxVisible > 0 && sources.length > maxVisible;
    const visible = hasMore && !showAll
        ? sources.slice(0, maxVisible)
        : sources;
    const hiddenCount = sources.length - visible.length;

    return (
        <div className="mt-3">
            <button
                type="button"
                onClick={() => setCollapsed((v) => !v)}
                className="flex items-center gap-1.5 text-xs font-semibold text-gray-600 transition hover:text-gray-900"
            >
                Sources ({sources.length})

                <ChevronDown
                    className={clsx(
                        "h-3.5 w-3.5 text-gray-400 transition-transform",
                        collapsed && "rotate-180"
                    )}
                />
            </button>

            {!collapsed && (
                <>
                    <div className="mt-2 grid gap-2 md:grid-cols-2">
                        {visible.map((source) => (
                            <SourceCard key={source.id} source={source} />
                        ))}
                    </div>

                    {hasMore && (
                        <button
                            type="button"
                            onClick={() => setShowAll((v) => !v)}
                            className="mt-2 text-xs font-medium text-gray-500 transition hover:text-gray-900"
                        >
                            {showAll
                                ? "Show less"
                                : `View ${hiddenCount} more source${hiddenCount === 1 ? "" : "s"}`}
                        </button>
                    )}
                </>
            )}
        </div>
    );
}
