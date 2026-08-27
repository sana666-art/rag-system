import { useRef } from "react";

export default function OTPInput({ value = "", onChange, length = 6 }) {

    const inputs = useRef([]);

    const handleChange = (index, digit) => {

        const chars = value.split("");

        if (digit === "") {

            chars[index] = "";
            onChange(chars.join(""));
            return;

        }

        const last = digit.slice(-1);

        if (!/^\d$/.test(last)) {
            return;
        }

        chars[index] = last;
        onChange(chars.join(""));

        if (index < length - 1) {
            inputs.current[index + 1]?.focus();
        }

    };

    const handleKeyDown = (index, event) => {

        if (event.key === "Backspace") {

            event.preventDefault();

            const chars = value.split("");
            chars[index] = "";
            onChange(chars.join(""));

            if (index > 0) {
                inputs.current[index - 1]?.focus();
            }

        }

        if (event.key === "ArrowLeft" && index > 0) {
            inputs.current[index - 1]?.focus();
        }

        if (event.key === "ArrowRight" && index < length - 1) {
            inputs.current[index + 1]?.focus();
        }

    };

    const handlePaste = (event) => {

        event.preventDefault();

        const digits = event.clipboardData
            .getData("text")
            .replace(/\D/g, "")
            .slice(0, length);

        if (!digits) {
            return;
        }

        onChange(digits);

        inputs.current[Math.min(digits.length, length - 1)]?.focus();

    };

    return (
        <div className="flex justify-center gap-2">
            {Array.from({ length }).map((_, index) => (
                <input
                    key={index}
                    ref={(el) => {
                        inputs.current[index] = el;
                    }}
                    type="text"
                    inputMode="numeric"
                    autoComplete="one-time-code"
                    maxLength={1}
                    value={value[index] ?? ""}
                    autoFocus={index === 0}
                    onChange={(event) => handleChange(index, event.target.value)}
                    onKeyDown={(event) => handleKeyDown(index, event)}
                    onPaste={handlePaste}
                    className="h-12 w-12 rounded-lg border border-gray-300 text-center text-xl font-semibold text-gray-900 focus:border-gray-500 focus:ring-2 focus:ring-gray-100 focus:outline-none"
                />
            ))}
        </div>
    );
}
