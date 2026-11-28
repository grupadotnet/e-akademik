import * as z from "zod/mini";

export const loginSchema = z.object({
  email: z
    .string()
    .check(
      z.trim(),
      z.minLength(1, "Enter your email"),
      z.regex(/^[^@\s]+@[^@\s]+\.[^@\s]+$/, "Enter a valid email"),
    ),
  password: z.string().check(z.minLength(1, "Enter your password")),
});
