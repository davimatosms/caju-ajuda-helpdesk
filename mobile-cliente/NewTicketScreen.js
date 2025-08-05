import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

export default function NewTicketScreen() {
    return (
        <View style={styles.container}>
            <Text>Tela de Novo Chamado</Text>
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, justifyContent: 'center', alignItems: 'center' }
});