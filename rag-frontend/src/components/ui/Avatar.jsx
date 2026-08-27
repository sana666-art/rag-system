import clsx from "clsx";

const colors = [
    "bg-indigo-500",
    "bg-emerald-500",
    "bg-amber-500",
    "bg-rose-500",
    "bg-sky-500",
    "bg-violet-500",
    "bg-teal-500",
    "bg-fuchsia-500",
];

function initials(name = "") {
    const parts = name.trim().split(/\s+/).filter(Boolean);
    if (parts.length === 0) return "?";
    if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
    return (parts[0][0] + parts[1][0]).toUpperCase();
}

function hashIndex(name = "") {
    let hash = 0;
    for (let i = 0; i < name.length; i++) {
        hash = (hash * 31 + name.charCodeAt(i)) >>> 0;
    }
    return hash % colors.length;
}

export default function Avatar({
    name,
    src,
    size = "md",
    className,
    ...props
}) {

    const dims = {
        sm: "h-8 w-8 text-xs",
        md: "h-10 w-10 text-sm",
        lg: "h-14 w-14 text-lg",
    };

    if (src) {
        return (
            <img
                src={src}
                alt={name || "avatar"}
                className={clsx("rounded-full object-cover", dims[size], className)}
                {...props}
            />
        );
    }

    return (
        <div
            className={clsx(
                "flex items-center justify-center rounded-full font-semibold text-white",
                colors[hashIndex(name)],
                dims[size],
                className
            )}
            {...props}
        >
            {initials(name)}
        </div>
    );
}
