import { SessionProvider, useSession } from "@/features/user/providers/session-provider";
import { Stack } from "expo-router";
import { StyleSheet } from "react-native";

function RootStack() {
  const { token } = useSession();
  return (
    <Stack
      screenOptions={{
        headerStyle: { backgroundColor: "#0F2744" },
        headerTintColor: "#FFFFFF",
        headerTitleAlign: "center",
        headerTitleStyle: styles.text,
        headerShadowVisible: false,
        contentStyle: { backgroundColor: "#0F2744" },
      }}
    >
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
            headerTransparent: true,
            headerStyle: { backgroundColor: "transparent" },
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
