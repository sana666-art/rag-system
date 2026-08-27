import { Link } from "react-router-dom";

export default function NotFound() {

    return (
        <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50 p-4 text-center">
            <h1 className="text-6xl font-bold text-indigo-600">404</h1>

            <p className="mt-4 text-lg text-gray-500">
                The page you are looking for does not exist.
            </p>

            <Link
                to="/"
                className="mt-6 rounded-xl bg-indigo-600 px-6 py-3 font-semibold text-white hover:bg-indigo-700"
            >
                Go home
            </Link>
        </div>
    );
}
