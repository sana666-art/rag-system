import { useEffect, useRef, useState } from "react";
import { ArrowDown } from "lucide-react";
import { AnimatePresence, motion } from "framer-motion";

export default function ScrollToBottom({ messages, loading, children }) {

    const containerRef = useRef(null);
    const firstRender = useRef(true);
    const [showButton, setShowButton] = useState(false);

    useEffect(() => {

        const el = containerRef.current;

        if (!el) return;

        const behavior = firstRender.current ? "auto" : "smooth";

        firstRender.current = false;

        el.scrollTo({ top: el.scrollHeight, behavior });

    }, [messages, loading]);

    const handleScroll = () => {

        const el = containerRef.current;

        if (!el) return;

        const distance = el.scrollHeight - el.scrollTop - el.clientHeight;

        setShowButton(distance > 80);
    };

    const scrollToBottom = () => {
        const el = containerRef.current;
        el?.scrollTo({ top: el.scrollHeight, behavior: "smooth" });
    };

    return (
        <div
            ref={containerRef}
            onScroll={handleScroll}
            className="relative flex-1 overflow-y-auto"
        >
            {children}

            <AnimatePresence>
                {showButton && (
                    <motion.button
                        type="button"
                        onClick={scrollToBottom}
                        initial={{ opacity: 0, y: 8 }}
                        animate={{ opacity: 1, y: 0 }}
                        exit={{ opacity: 0, y: 8 }}
                        className="absolute bottom-4 left-1/2 flex h-9 w-9 -translate-x-1/2 items-center justify-center rounded-full border border-gray-300 bg-white text-gray-600 shadow-sm transition hover:bg-gray-50"
                        aria-label="Scroll to bottom"
                    >
                        <ArrowDown className="h-4 w-4" />
                    </motion.button>
                )}
            </AnimatePresence>
        </div>
    );
}
