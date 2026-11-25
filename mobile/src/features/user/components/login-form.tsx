import { useState } from "react";
import { Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { login } from "@/features/user/user-api";
import { useSession } from "@/features/user/providers/session-provider";
import { loginSchema } from "../user-schema";
import { z } from "zod/mini";

const messages = {
  bad_credentials: "Wrong email or password",
  network: "Cannot reach the server",
  no_token: "Could not log in",
  other: "Could not log in",
} as const;

export function LoginForm() {
  const { signIn } = useSession();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [fieldsErrors, setFieldsErrors] = useState<{
    email?: string;
    password?: string;
  }>({});
  const [error, setError] = useState("");
  const [pending, setPending] = useState(false);

  const clearErrors = () => {
    setFieldsErrors({});
    setError("");
  };

  const onLogin = async () => {
    if (pending) return;
    const parsed = loginSchema.safeParse({ email, password });
    if (!parsed.success) {
      const flat = z.flattenError(parsed.error);
      setFieldsErrors({
        email: flat.fieldErrors.email?.[0],
        password: flat.fieldErrors.password?.[0],
      });
      return;
    }

    setPending(true);
    clearErrors();
    try {
      const result = await login(parsed.data.email, parsed.data.password);
      if (!result.ok) {
        setError(messages[result.reason]);
        return;
      }
      await signIn(result.token);
    } catch {
      setError(messages.other);
    } finally {
      setPending(false);
    }
  };

  return (
    <View style={styles.screen}>
      <TextInput
        value={email}
        onChangeText={(v) => {
          setEmail(v);
          clearErrors();
        }}
        autoCapitalize="none"
        autoComplete="email"
        keyboardType="email-address"
        placeholder="Email"
        placeholderTextColor="#8aa0bb"
        style={styles.input}
      />

      <TextInput
        value={password}
        onChangeText={(v) => {
          setPassword(v);
          clearErrors();
        }}
        autoComplete="current-password"
        secureTextEntry
        placeholder="Password"
        placeholderTextColor="#8aa0bb"
        style={styles.input}
      />

      <Pressable
        disabled={pending}
        onPress={onLogin}
        style={({ pressed }) => [
          styles.button,
          pressed && styles.buttonPressed,
          pending && styles.buttonDisabled,
        ]}
      >
        <Text style={styles.buttonText}>
          {pending ? "Logging in..." : "Log in"}
        </Text>
      </Pressable>

      <Text style={styles.error} accessibilityRole="alert">
        {fieldsErrors.email ?? fieldsErrors.password ?? error}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: "#0F2744",
    padding: 24,
    justifyContent: "center",
    gap: 12,
  },
  input: {
    backgroundColor: "#1a3a5e",
    color: "white",
    padding: 14,
    borderRadius: 10,
  },
  button: {
    backgroundColor: "#208AEF",
    padding: 16,
    borderRadius: 12,
  },
  buttonPressed: {
    backgroundColor: "#176FB8",
  },
  buttonDisabled: {
    opacity: 0.7,
  },
  buttonText: {
    color: "white",
    textAlign: "center",
    fontWeight: "600",
  },
  error: {
    color: "#ffb4b4",
    textAlign: "center",
    minHeight: 20,
  },
});
