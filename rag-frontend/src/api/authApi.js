import api from "./axios";

export const register=(data)=>
    api.post("/api/register",data);

export const login=(data)=>
    api.post("/api/login",data);

export const verifyEmail=(data)=>
    api.post("/api/verify-email",data);

export const resendVerification=(data)=>
    api.post("/api/resend-verification",data);

export const verify2FA=(data)=>
    api.post("/api/verify-2fa",data);

export const refresh=(data)=>
    api.post("/api/refresh",data);

export const logout=(data)=>
    api.post("/api/logout",data);

export const getMe=()=>
    api.get("/api/users/me");

export const toggle2FA=()=>
    api.post("/api/users/toggle-2fa");