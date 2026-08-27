import api from "../api/axios";

const GUEST_ID_KEY = "guestId";

export function getGuestId() {

    let guestId = localStorage.getItem(GUEST_ID_KEY);

    if (!guestId) {
        guestId = crypto.randomUUID();
        localStorage.setItem(GUEST_ID_KEY, guestId);
    }

    return guestId;
}

export async function askGuest({ question }) {

    const response = await api.post("/api/guest/ask", {
        guestId: getGuestId(),
        question,
    });

    return response.data;
}
