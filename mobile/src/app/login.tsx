import { StyleSheet, Text, View } from "react-native";

export default function Login() {
  return (
    <View style={styles.screen}>
      <Text style={styles.title}>Login page</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#0F2744",
  },
  title: {
    color: "#FFFFFF",
    fontSize: 32,
    fontWeight: "600",
  },
});
