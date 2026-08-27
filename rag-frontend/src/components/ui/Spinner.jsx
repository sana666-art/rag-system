import clsx from "clsx";

export default function Spinner({ light = false, className }) {

    return (
        <div
            className={clsx(
                "h-5 w-5 animate-spin rounded-full border-2",
                light
                    ? "border-white/40 border-t-white"
                    : "border-gray-300 border-t-gray-900",
                className
            )}
        />
    );
}
