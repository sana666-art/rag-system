import {z} from "zod";

export const registerSchema=z.object({

    fullName:z.string().min(3),

    email:z.email(),

    password:z.string().min(8),

    confirmPassword:z.string()

}).refine(

    data=>data.password===data.confirmPassword,

    {

        path:["confirmPassword"],

        message:"Passwords do not match"

    }

);

export const loginSchema=z.object({

    email:z.email(),

    password:z.string().min(1)

});

export const otpSchema=z.object({

    email:z.email(),

    otp:z.string().length(6)

});

export const twoFactorSchema=z.object({

    email:z.email(),

    code:z.string().length(6)

});