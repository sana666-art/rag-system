import { useEffect, useRef, useState } from "react";

export default function useTypewriter(text, { speed = 12 } = {}) {

    const [displayed, setDisplayed] = useState("");
    const [done, setDone] = useState(false);
    const indexRef = useRef(0);
    const textRef = useRef(text);

    useEffect(() => {
        textRef.current = text;
        indexRef.current = 0;
        setDisplayed("");
        setDone(false);
    }, [text]);

    useEffect(() => {

        if (!text) {
            setDone(true);
            return;
        }

        const interval = setInterval(() => {

            if (indexRef.current >= textRef.current.length) {
                clearInterval(interval);
                setDone(true);
                return;
            }

            indexRef.current += 1;
            setDisplayed(textRef.current.slice(0, indexRef.current));
        }, speed);

        return () => clearInterval(interval);
    }, [text, speed]);

    return { displayed, done };
}
