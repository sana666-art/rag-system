import { Outlet } from "react-router-dom";

export default function ChatLayout() {

    return (
        <div className="flex h-screen w-full overflow-hidden bg-white">
            <Outlet />
        </div>
    );
}
