import { Bot } from "lucide-react";
import Card from "../components/ui/Card";

export default function AuthLayout({ title, subtitle, children }) {

    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-50 p-4">
            <div className="w-full max-w-md">
                <div className="mb-8 flex flex-col items-center text-center">
                    <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-gray-900 text-white shadow-lg shadow-gray-200">
                        <Bot className="h-7 w-7" />
                    </div>

                    <h1 className="text-3xl font-bold text-gray-900">
                        {title}
                    </h1>

                    {subtitle && (
                        <p className="mt-2 text-gray-500">
                            {subtitle}
                        </p>
                    )}
                </div>

                <Card>
                    {children}
                </Card>
            </div>
        </div>
    );
}
