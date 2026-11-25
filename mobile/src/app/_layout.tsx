import { SessionProvider, useSession } from "@/api/SessionProvider";
import { Stack } from "expo-router";
import { StyleSheet } from "react-native";

function RootStack() {
  const { token } = useSession();
  return (
    <Stack>
      <Stack.Protected guard={!!token}>
        <Stack.Screen name="home" />
      </Stack.Protected>

      <Stack.Protected guard={!token}>
        <Stack.Screen
          name="index"
          options={{ headerShown: false, title: "e-akademik" }}
        />
        <Stack.Screen
          name="login"
          options={{
            title: "Log in",
            headerTitleStyle: styles.text,
            headerBackButtonDisplayMode: "minimal",
          }}
        />
      </Stack.Protected>
    </Stack>
  );
}

export default function RootLayout() {
  return (
    <SessionProvider>
      <RootStack />
    </SessionProvider>
  );
}

const styles = StyleSheet.create({
  text: {
    color: "#FFFFFF",
    fontSize: 20,
    fontWeight: "600",
  },
});
