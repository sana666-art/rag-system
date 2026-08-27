import * as authApi from "../api/authApi";

import {
    saveTokens,
    saveEmail,
    saveUser,
    clearStorage
} from "../utils/storage";

const authService={

    async register(data){

        const response=await authApi.register(data);

        saveEmail(data.email);

        saveUser({
            email: data.email,
            fullName: data.fullName,
            twoFactorEnabled: false
        });

        return response.data;

    },

    async fetchCurrentUser(){

        const response=await authApi.getMe();

        saveUser(response.data);

        if(response.data.email){
            saveEmail(response.data.email);
        }

        return response.data;

    },

    async login(data){

        const response=await authApi.login(data);

        const result=response.data;

        if(result.requiresTwoFactor){

            saveEmail(data.email);

            return result;

        }

        saveTokens(
            result.accessToken,
            result.refreshToken
        );

        return {
            ...result,
            user: await this.fetchCurrentUser()
        };

    },

    async verifyEmail(data){

        const response=await authApi.verifyEmail(data);

        return response.data;

    },

    async resendVerification(data){

        const response=await authApi.resendVerification(data);

        return response.data;

    },

    async verify2FA(data){

        const response=await authApi.verify2FA(data);

        saveTokens(
            response.data.accessToken,
            response.data.refreshToken
        );

        return {
            ...response.data,
            user: await this.fetchCurrentUser()
        };

    },

    async logout(refreshToken){

        await authApi.logout({refreshToken});

        clearStorage();

    },

    async toggle2FA(){

        const response=await authApi.toggle2FA();

        return response.data;

    }

};

export default authService;