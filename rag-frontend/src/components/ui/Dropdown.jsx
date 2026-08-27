import { useEffect, useRef, useState } from "react";
import clsx from "clsx";

export default function Dropdown({
    trigger,
    children,
    align = "right",
    menuClassName,
    flip = false,
}) {

    const [open, setOpen] = useState(false);
    const [flipped, setFlipped] = useState(false);
    const ref = useRef(null);
    const menuRef = useRef(null);

    useEffect(() => {

        if (!open) return;

        const handleClick = (event) => {
            if (ref.current && !ref.current.contains(event.target)) {
                setOpen(false);
            }
        };

        const handleKey = (event) => {
            if (event.key === "Escape") setOpen(false);
        };

        document.addEventListener("mousedown", handleClick);
        document.addEventListener("keydown", handleKey);

        return () => {
            document.removeEventListener("mousedown", handleClick);
            document.removeEventListener("keydown", handleKey);
        };
    }, [open]);

    useEffect(() => {

        if (!open || !flip) return;

        const wrapper = ref.current;
        const menu = menuRef.current;
        if (!wrapper || !menu) return;

        const wrapperRect = wrapper.getBoundingClientRect();
        const menuHeight = menu.offsetHeight;
        const gap = 8;
        const spaceBelow = window.innerHeight - wrapperRect.bottom - gap;
        const spaceAbove = wrapperRect.top;

        setFlipped(
            menuHeight > spaceBelow && spaceAbove >= menuHeight + gap
        );

    }, [open, flip, children]);

    return (
        <div ref={ref} className="relative inline-block">
            <div
                onClick={(event) => {
                    event.stopPropagation();
                    setOpen((v) => !v);
                }}
            >
                {trigger}
            </div>

            {open && (
                <div
                    ref={menuRef}
                    className={clsx(
                        "absolute z-30 min-w-44 overflow-hidden rounded-xl border border-gray-200 bg-white py-1 shadow-lg",
                        flipped ? "bottom-full mb-2" : "mt-2",
                        align === "right" ? "right-0" : "left-0",
                        menuClassName
                    )}
                    onClick={() => setOpen(false)}
                >
                    {children}
                </div>
            )}
        </div>
    );
}
