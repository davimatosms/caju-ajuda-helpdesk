import React from 'react';
import { View, Text, StyleSheet, Button } from 'react-native';

// Uma tela simples que será o nosso dashboard
export default function HomeScreen({ navigation }) {
  // A função para "fazer logout" (neste caso, apenas voltar para a tela de login)
  const handleLogout = () => {
    // No futuro, aqui vamos limpar o token guardado
    navigation.replace('Login');
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Bem-vindo!</Text>
      <Text style={styles.subtitle}>Você está logado no Caju Ajuda.</Text>
      <Button title="Sair" onPress={handleLogout} color="#ef4444" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
    backgroundColor: '#f8f9fa',
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#1a202c',
  },
  subtitle: {
    fontSize: 18,
    color: '#718096',
    marginBottom: 30,
  },
});