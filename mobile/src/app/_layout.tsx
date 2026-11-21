import { Stack } from "expo-router";
import { StyleSheet } from "react-native";

export default function RootLayout() {
  return (
    <Stack>
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
    </Stack>
  );
}

const styles = StyleSheet.create({
  text: {
    color: "#FFFFFF",
    fontSize: 20,
    fontWeight: "600",
  },
});
