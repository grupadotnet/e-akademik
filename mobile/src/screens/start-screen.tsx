import { useRouter } from "expo-router";
import { Pressable, StyleSheet, Text, View } from "react-native";

export default function StartScreen() {
  const router = useRouter();

  return (
    <View style={styles.screen}>
      <Text style={styles.title}>e-akademik</Text>
      <Pressable
        style={({ pressed }) => [
          styles.button,
          pressed && styles.buttonPressed,
        ]}
        onPress={() => router.push("/login")}
      >
        <Text style={styles.buttonText}>Log in</Text>
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
