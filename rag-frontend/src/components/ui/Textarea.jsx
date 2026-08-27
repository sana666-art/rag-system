import clsx from "clsx";

export default function Textarea({
    label,
    error,
    className,
    ...props
}) {

    return (
        <div className="space-y-1">
            {label && (
                <label className="block text-sm font-medium text-gray-700">
                    {label}
                </label>
            )}

            <textarea
                className={clsx(
                    "w-full rounded-xl border border-gray-300 px-3 py-2 text-sm text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-gray-500 focus:ring-2 focus:ring-gray-100",
                    error && "border-red-500 focus:border-red-500 focus:ring-red-100",
                    className
                )}
                {...props}
            />

            {error && (
                <p className="text-sm text-red-500">
                    {error}
                </p>
            )}
        </div>
    );
}
