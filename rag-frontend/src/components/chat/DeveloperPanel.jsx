import { useState } from "react";
import {
    ChevronDown,
    Cpu,
    Gauge,
    Timer,
    Zap,
} from "lucide-react";
import clsx from "clsx";

function MetricRow({ icon: Icon, label, value, unit }) {

    return (
        <div className="flex items-center justify-between rounded-lg bg-gray-50 px-2.5 py-1.5">
            <span className="flex items-center gap-1.5 text-xs text-gray-500">
                <Icon className="h-3.5 w-3.5 text-gray-400" />
                {label}
            </span>
            <span className="font-mono text-xs font-medium text-gray-800">
                {value}
                {unit && <span className="ml-0.5 text-gray-400">{unit}</span>}
            </span>
        </div>
    );
}

export default function DeveloperPanel({ message }) {

    const [collapsed, setCollapsed] = useState(true);

    if (!message) return null;

    return (
        <div className="mt-3 overflow-hidden rounded-xl border border-dashed border-gray-300">
            <button
                type="button"
                onClick={() => setCollapsed((v) => !v)}
                className="flex w-full items-center gap-2 bg-gray-50/60 px-3 py-2 text-left transition hover:bg-gray-50"
            >
                <span className="flex items-center gap-1.5 text-xs font-semibold text-gray-600">
                    <Cpu className="h-3.5 w-3.5" />
                    Developer
                </span>

                <ChevronDown
                    className={clsx(
                        "ml-auto h-4 w-4 text-gray-400 transition-transform",
                        !collapsed && "rotate-180"
                    )}
                />
            </button>

            {!collapsed && (
                <div className="grid gap-1.5 p-3">
                    <MetricRow
                        icon={Cpu}
                        label="Model"
                        value={message.model || "unknown"}
                    />

                    <div className="grid grid-cols-3 gap-1.5">
                        <MetricRow
                            icon={Zap}
                            label="Prompt"
                            value={message.promptTokens ?? 0}
                        />
                        <MetricRow
                            icon={Zap}
                            label="Completion"
                            value={message.completionTokens ?? 0}
                        />
                        <MetricRow
                            icon={Zap}
                            label="Total"
                            value={message.totalTokens ?? 0}
                        />
                    </div>

                    <div className="grid grid-cols-4 gap-1.5">
                        <MetricRow
                            icon={Timer}
                            label="Embed"
                            value={message.timings?.embedding ?? 0}
                            unit="ms"
                        />
                        <MetricRow
                            icon={Gauge}
                            label="Retrieve"
                            value={message.timings?.retrieval ?? 0}
                            unit="ms"
                        />
                        <MetricRow
                            icon={Timer}
                            label="LLM"
                            value={message.timings?.llm ?? 0}
                            unit="ms"
                        />
                        <MetricRow
                            icon={Timer}
                            label="Total"
                            value={message.timings?.total ?? 0}
                            unit="ms"
                        />
                    </div>

                    <div className="flex flex-wrap gap-1.5 pt-1 text-[11px] text-gray-400">
                        <span>Context docs: {message.contextDocuments ?? 0}</span>
                        <span>·</span>
                        <span>Prompt chars: {message.promptCharacters ?? 0}</span>
                        <span>·</span>
                        <span>Est. tokens: {message.estimatedPromptTokens ?? 0}</span>
                    </div>
                </div>
            )}
        </div>
    );
}
