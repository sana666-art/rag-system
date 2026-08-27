import clsx from "clsx";

const variants = {
    neutral: "bg-gray-100 text-gray-700",
    indigo: "bg-indigo-50 text-indigo-700",
    green: "bg-emerald-50 text-emerald-700",
    amber: "bg-amber-50 text-amber-700",
    red: "bg-red-50 text-red-700",
    blue: "bg-blue-50 text-blue-700",
};

const sizes = {
    sm: "px-2 py-0.5 text-[11px]",
    md: "px-2.5 py-1 text-xs",
};

export default function Badge({
    children,
    variant = "neutral",
    size = "md",
    className,
    ...props
}) {

    return (
        <span
            className={clsx(
                "inline-flex items-center gap-1 rounded-full font-medium",
                variants[variant],
                sizes[size],
                className
            )}
            {...props}
        >
            {children}
        </span>
    );
}
