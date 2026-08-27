import { useState } from "react";
import clsx from "clsx";

export default function Tooltip({
    content,
    children,
    side = "top",
    className,
}) {

    const [visible, setVisible] = useState(false);

    const positions = {
        top: "bottom-full left-1/2 mb-1.5 -translate-x-1/2",
        bottom: "top-full left-1/2 mt-1.5 -translate-x-1/2",
        right: "left-full top-1/2 ml-1.5 -translate-y-1/2",
        left: "right-full top-1/2 mr-1.5 -translate-y-1/2",
    };

    return (
        <div
            className={clsx("relative inline-flex", className)}
            onMouseEnter={() => setVisible(true)}
            onMouseLeave={() => setVisible(false)}
            onFocus={() => setVisible(true)}
            onBlur={() => setVisible(false)}
        >
            {children}

            {visible && (
                <div
                    className={clsx(
                        "pointer-events-none absolute z-40 whitespace-nowrap rounded-md bg-gray-900 px-2 py-1 text-xs font-medium text-white shadow-md",
                        positions[side]
                    )}
                >
                    {content}
                </div>
            )}
        </div>
    );
}
