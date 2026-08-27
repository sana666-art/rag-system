import { useEffect } from "react";
import { X } from "lucide-react";
import { AnimatePresence, motion } from "framer-motion";
import clsx from "clsx";

export default function Modal({
    open,
    onClose,
    title,
    children,
    footer,
    size = "md",
}) {

    useEffect(() => {

        if (!open) return;

        const handleKey = (event) => {
            if (event.key === "Escape") onClose();
        };

        document.addEventListener("keydown", handleKey);
        document.body.style.overflow = "hidden";

        return () => {
            document.removeEventListener("keydown", handleKey);
            document.body.style.overflow = "";
        };
    }, [open, onClose]);

    const sizes = {
        sm: "max-w-sm",
        md: "max-w-md",
        lg: "max-w-lg",
        xl: "max-w-2xl",
    };

    return (
        <AnimatePresence>
            {open && (
                <motion.div
                    className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    transition={{ duration: 0.15 }}
                    onMouseDown={(event) => {
                        if (event.target === event.currentTarget) onClose();
                    }}
                >
                    <motion.div
                        className={clsx(
                            "w-full overflow-hidden rounded-2xl bg-white shadow-2xl",
                            sizes[size]
                        )}
                        initial={{ opacity: 0, scale: 0.95, y: 10 }}
                        animate={{ opacity: 1, scale: 1, y: 0 }}
                        exit={{ opacity: 0, scale: 0.95, y: 10 }}
                        transition={{ duration: 0.18, ease: "easeOut" }}
                    >
                        <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4">
                            <h3 className="text-base font-semibold text-gray-900">
                                {title}
                            </h3>

                            <button
                                type="button"
                                onClick={onClose}
                                className="rounded-lg p-1 text-gray-400 transition hover:bg-gray-100 hover:text-gray-600"
                                aria-label="Close"
                            >
                                <X className="h-5 w-5" />
                            </button>
                        </div>

                        <div className="px-5 py-4">
                            {children}
                        </div>

                        {footer && (
                            <div className="flex justify-end gap-2 border-t border-gray-100 px-5 py-4">
                                {footer}
                            </div>
                        )}
                    </motion.div>
                </motion.div>
            )}
        </AnimatePresence>
    );
}
