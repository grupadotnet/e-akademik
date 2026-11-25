import { useSession } from "@/api/SessionProvider";
import { Pressable, StyleSheet, Text, View } from "react-native";

export default function HomeScreen() {
  const { signOut } = useSession();

  return (
    <View style={styles.screen}>
      <Text style={styles.title}>Logged in</Text>
      <Pressable
        style={({ pressed }) => [
          styles.button,
          pressed && styles.buttonPressed,
        ]}
        onPress={signOut}
      >
        <Text style={styles.buttonText}>Log out</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#0F2744",
    gap: 24,
  },
  title: {
    color: "#FFFFFF",
    fontSize: 32,
    fontWeight: "600",
  },
  button: {
    backgroundColor: "#208AEF",
    paddingVertical: 12,
    paddingHorizontal: 28,
    borderRadius: 8,
  },
  buttonPressed: {
    backgroundColor: "#176FB8",
  },
  buttonText: {
    color: "#FFFFFF",
    fontSize: 16,
    fontWeight: "600",
  },
});
