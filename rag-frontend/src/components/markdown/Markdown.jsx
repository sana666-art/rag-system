import { useState } from "react";
import { Check, Copy } from "lucide-react";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { PrismLight as SyntaxHighlighter } from "react-syntax-highlighter";
import { oneLight } from "react-syntax-highlighter/dist/esm/styles/prism";

import javascript from "react-syntax-highlighter/dist/esm/languages/prism/javascript";
import typescript from "react-syntax-highlighter/dist/esm/languages/prism/typescript";
import jsx from "react-syntax-highlighter/dist/esm/languages/prism/jsx";
import tsx from "react-syntax-highlighter/dist/esm/languages/prism/tsx";
import python from "react-syntax-highlighter/dist/esm/languages/prism/python";
import java from "react-syntax-highlighter/dist/esm/languages/prism/java";
import json from "react-syntax-highlighter/dist/esm/languages/prism/json";
import sql from "react-syntax-highlighter/dist/esm/languages/prism/sql";
import bash from "react-syntax-highlighter/dist/esm/languages/prism/bash";
import markdown from "react-syntax-highlighter/dist/esm/languages/prism/markdown";
import markup from "react-syntax-highlighter/dist/esm/languages/prism/markup";
import css from "react-syntax-highlighter/dist/esm/languages/prism/css";
import yaml from "react-syntax-highlighter/dist/esm/languages/prism/yaml";
import c from "react-syntax-highlighter/dist/esm/languages/prism/c";
import cpp from "react-syntax-highlighter/dist/esm/languages/prism/cpp";
import csharp from "react-syntax-highlighter/dist/esm/languages/prism/csharp";
import go from "react-syntax-highlighter/dist/esm/languages/prism/go";
import rust from "react-syntax-highlighter/dist/esm/languages/prism/rust";

const registered = {
    javascript,
    typescript,
    jsx,
    tsx,
    python,
    java,
    json,
    sql,
    bash,
    markdown,
    markup,
    css,
    yaml,
    c,
    cpp,
    csharp,
    go,
    rust,
};

const aliases = {
    js: "javascript",
    jsx: "jsx",
    ts: "typescript",
    tsx: "tsx",
    py: "python",
    shell: "bash",
    sh: "bash",
    html: "markup",
    htm: "markup",
    xml: "markup",
    md: "markdown",
    yml: "yaml",
    "c++": "cpp",
    cs: "csharp",
    golang: "go",
    rs: "rust",
};

function resolveLanguage(raw) {
    const name = (raw || "").toLowerCase();
    return aliases[name] ?? (registered[name] ? name : null);
}

function CodeBlock({ inline, className, children, ...props }) {

    const [copied, setCopied] = useState(false);

    if (inline) {
        return (
            <code
                className="rounded-md bg-gray-100 px-1.5 py-0.5 font-mono text-[0.85em] text-gray-800"
                {...props}
            >
                {children}
            </code>
        );
    }

    const handleCopy = async () => {
        try {
            await navigator.clipboard.writeText(String(children));
            setCopied(true);
            setTimeout(() => setCopied(false), 2000);
        } catch {}
    };

    const language = resolveLanguage(className?.replace("language-", ""));

    const codeContent = String(children);

    return (
        <div className="group relative my-3 overflow-hidden rounded-xl border border-gray-200 bg-gray-50">
            <div className="flex items-center justify-between border-b border-gray-200 px-3 py-1.5">
                <span className="text-[11px] font-medium text-gray-500">
                    {language || "code"}
                </span>

                <button
                    type="button"
                    onClick={handleCopy}
                    className="flex items-center gap-1 rounded-md px-1.5 py-1 text-[11px] font-medium text-gray-400 transition hover:bg-gray-100 hover:text-gray-600"
                >
                    {copied ? (
                        <>
                            <Check className="h-3 w-3 text-emerald-600" />
                            Copied
                        </>
                    ) : (
                        <>
                            <Copy className="h-3 w-3" />
                            Copy
                        </>
                    )}
                </button>
            </div>

            {language ? (
                <SyntaxHighlighter
                    language={language}
                    style={oneLight}
                    PreTag="div"
                    customStyle={{
                        margin: 0,
                        background: "transparent",
                        padding: "0.75rem",
                        fontSize: "0.8125rem",
                    }}
                    codeTagProps={{ style: { fontFamily: "inherit" } }}
                >
                    {codeContent}
                </SyntaxHighlighter>
            ) : (
                <pre className="overflow-x-auto p-3 text-sm leading-relaxed text-gray-800">
                    <code className="font-mono">{codeContent}</code>
                </pre>
            )}
        </div>
    );
}

function Table({ children, ...props }) {

    return (
        <div className="my-3 overflow-x-auto rounded-xl border border-gray-200">
            <table
                className="w-full border-collapse text-sm"
                {...props}
            >
                {children}
            </table>
        </div>
    );
}

export default function Markdown({ content }) {

    return (
        <div className="prose-sm prose-gray max-w-none prose-headings:font-semibold prose-headings:text-gray-900 prose-p:text-[15px] prose-p:leading-relaxed prose-p:text-gray-800 prose-a:text-gray-900 prose-a:underline prose-strong:text-gray-900 prose-em:text-gray-700 prose-blockquote:border-l-gray-300 prose-blockquote:text-gray-500 prose-hr:border-gray-200">
            <ReactMarkdown
                remarkPlugins={[remarkGfm]}
                components={{
                    code: CodeBlock,
                    table: Table,
                }}
            >
                {content}
            </ReactMarkdown>
        </div>
    );
}
