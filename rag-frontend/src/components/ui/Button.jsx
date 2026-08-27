import clsx from "clsx";
import Spinner from "./Spinner";

const variants = {
    primary:
        "bg-gray-900 text-white hover:bg-black focus-visible:ring-gray-500",
    secondary:
        "bg-white text-gray-700 border border-gray-300 hover:bg-gray-50 focus-visible:ring-gray-400",
    ghost:
        "bg-transparent text-gray-600 hover:bg-gray-100 focus-visible:ring-gray-400",
    danger:
        "bg-red-600 text-white hover:bg-red-700 focus-visible:ring-red-500",
    dangerGhost:
        "bg-transparent text-red-600 hover:bg-red-50 focus-visible:ring-red-400",
};

const sizes = {
    sm: "px-3 py-1.5 text-xs",
    md: "px-4 py-2 text-sm",
    lg: "px-5 py-3 text-base",
};

export default function Button({
    children,
    variant = "primary",
    size = "md",
    fullWidth = false,
    loading = false,
    className,
    ...props
}) {

    return (
        <button
            className={clsx(
                "inline-flex items-center justify-center gap-2 rounded-xl font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60",
                variants[variant],
                sizes[size],
                fullWidth && "w-full",
                className
            )}
            disabled={loading || props.disabled}
            {...props}
        >
            {loading ? <Spinner light={variant !== "secondary" && variant !== "ghost" && variant !== "dangerGhost"} className="h-4 w-4" /> : children}
        </button>
    );
}
