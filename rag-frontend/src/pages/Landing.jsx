import { useEffect } from "react";
import { useLocation } from "react-router-dom";
import Navbar from "../components/landing/Navbar";
import Hero from "../components/landing/Hero";
import Features from "../components/landing/Features";
import HowItWorks from "../components/landing/HowItWorks";
import Pricing from "../components/landing/Pricing";
import FAQ from "../components/landing/FAQ";
import Footer from "../components/landing/Footer";

export default function Landing() {

    const location = useLocation();

    useEffect(() => {
        const target = location.state?.scrollTo;

        if (target) {
            const el = document.getElementById(target);
            if (el) {
                setTimeout(() => el.scrollIntoView({ behavior: "smooth" }), 50);
            }
            window.history.replaceState({}, "");
        }
    }, [location.state]);

    return (
        <div className="min-h-screen bg-white">
            <Navbar />

            <Hero />

            <Features />

            <HowItWorks />

            <Pricing />

            <FAQ />

            <Footer />
        </div>
    );
}
