const steps = [
    {
        number: "01",
        title: "Ask",
        description:
            "Type a question in plain English, like “What stocks do I own?” or “When did I buy MSFT?”",
    },
    {
        number: "02",
        title: "Retrieve",
        description:
            "Your question is embedded and matched against your portfolio documents using vector similarity.",
    },
    {
        number: "03",
        title: "Answer with sources",
        description:
            "The model composes an answer grounded in the retrieved context — with citations and similarity scores you can inspect.",
    },
];

export default function HowItWorks() {

    return (
        <section id="how-it-works" className="bg-white py-24">
            <div className="mx-auto max-w-5xl px-6">
                <div className="mx-auto max-w-2xl text-center">
                    <h2 className="text-3xl font-bold tracking-tight text-gray-900">
                        How it works
                    </h2>
                    <p className="mt-3 text-gray-500">
                        Three steps between you and an answer backed by your data.
                    </p>
                </div>

                <div className="mt-14 grid gap-8 md:grid-cols-3">
                    {steps.map((step, index) => (
                        <div key={step.number} className="relative">
                            {index < steps.length - 1 && (
                                <div className="absolute left-1/2 top-8 hidden h-px w-full bg-gray-200 md:block" />
                            )}

                            <div className="relative flex h-16 w-16 items-center justify-center rounded-2xl bg-gray-900 text-xl font-bold text-white">
                                {step.number}
                            </div>

                            <h3 className="mt-5 text-lg font-semibold text-gray-900">
                                {step.title}
                            </h3>

                            <p className="mt-2 text-sm leading-relaxed text-gray-600">
                                {step.description}
                            </p>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
}
