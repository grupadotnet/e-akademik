import { useState } from "react";
import { Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { login } from "@/features/user/user-api";
import { useSession } from "@/features/user/providers/session-provider";

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
  const [error, setError] = useState("");
  const [pending, setPending] = useState(false);

  const onLogin = async () => {
    if (!email.trim() || !password) {
      setError("Enter an email and password");
      return;
    }

    setPending(true);
    setError("");
    try {
      const result = await login(email.trim(), password);
      if (!result.ok) {
        setError(messages[result.reason]);
        return;
      }
      await signIn(result.token);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Could not log in");
    } finally {
      setPending(false);
    }
  };

  return (
    <View style={styles.screen}>
      <TextInput
        value={email}
        onChangeText={setEmail}
        autoCapitalize="none"
        autoComplete="email"
        keyboardType="email-address"
        placeholder="Email"
        placeholderTextColor="#8aa0bb"
        style={styles.input}
      />
      <TextInput
        value={password}
        onChangeText={setPassword}
        autoComplete="password"
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
      {error ? <Text style={styles.error}>{error}</Text> : null}
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
  },
});
