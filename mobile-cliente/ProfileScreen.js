import React from 'react';
import { View, Text, StyleSheet, Button } from 'react-native';

export default function ProfileScreen({ navigation }) {
    return (
        <View style={styles.container}>
            <Text>Tela de Perfil</Text>
            {/* O botão de Sair agora viverá aqui */}
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, justifyContent: 'center', alignItems: 'center' }
});