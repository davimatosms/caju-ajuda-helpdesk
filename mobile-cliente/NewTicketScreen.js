import React, { useState } from 'react';
import { View, Text, TextInput, StyleSheet, TouchableOpacity, Alert, ActivityIndicator } from 'react-native';
import * as SecureStore from 'expo-secure-store';

const API_CLIENTE_URL = 'http://192.168.15.12:8080/api/cliente';

export default function NewTicketScreen({ navigation }) {
    const [titulo, setTitulo] = useState('');
    const [descricao, setDescricao] = useState('');
    const [prioridade, setPrioridade] = useState('BAIXA');
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async () => {
        if (!titulo || !descricao) {
            Alert.alert('Erro', 'Por favor, preencha o título e a descrição.');
            return;
        }
        setIsLoading(true);

        try {
            const token = await SecureStore.getItemAsync('userToken');
            const response = await fetch(`${API_CLIENTE_URL}/chamados`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify({ titulo, descricao, prioridade }),
            });

            if (response.ok) {
                Alert.alert('Sucesso', 'Seu chamado foi aberto com sucesso!');
                // Limpa os campos e volta para a lista de chamados
                setTitulo('');
                setDescricao('');
                setPrioridade('BAIXA');
                navigation.navigate('Meus Chamados', { refresh: true });
            } else {
                throw new Error('Não foi possível abrir o chamado.');
            }
        } catch (error) {
            console.error(error);
            Alert.alert('Erro', 'Ocorreu um erro ao enviar seu chamado.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.label}>Título do Chamado</Text>
            <TextInput
                style={styles.input}
                value={titulo}
                onChangeText={setTitulo}
                placeholder="Ex: Problema ao acessar a fatura"
            />

            <Text style={styles.label}>Descrição do Problema</Text>
            <TextInput
                style={[styles.input, styles.textArea]}
                value={descricao}
                onChangeText={setDescricao}
                placeholder="Descreva o problema com o máximo de detalhes possível."
                multiline
            />

            <Text style={styles.label}>Prioridade</Text>
            <View style={styles.priorityContainer}>
                {['BAIXA', 'MEDIA', 'ALTA'].map((p) => (
                    <TouchableOpacity
                        key={p}
                        style={[styles.priorityButton, prioridade === p && styles.prioritySelected]}
                        onPress={() => setPrioridade(p)}
                    >
                        <Text style={[styles.priorityText, prioridade === p && styles.priorityTextSelected]}>{p}</Text>
                    </TouchableOpacity>
                ))}
            </View>

            <TouchableOpacity style={styles.submitButton} onPress={handleSubmit} disabled={isLoading}>
                {isLoading ? <ActivityIndicator color="#fff" /> : <Text style={styles.submitButtonText}>Abrir Chamado</Text>}
            </TouchableOpacity>
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, padding: 20, backgroundColor: '#f8f9fa' },
    label: { fontSize: 16, fontWeight: '500', color: '#4a5568', marginBottom: 8, },
    input: {
        backgroundColor: '#fff',
        borderWidth: 1,
        borderColor: '#dee2e6',
        borderRadius: 8,
        paddingHorizontal: 15,
        paddingVertical: 12,
        fontSize: 16,
        marginBottom: 20,
    },
    textArea: { height: 120, textAlignVertical: 'top' },
    priorityContainer: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 30, },
    priorityButton: {
        flex: 1,
        paddingVertical: 12,
        borderRadius: 8,
        borderWidth: 1,
        borderColor: '#dee2e6',
        alignItems: 'center',
        marginHorizontal: 4,
    },
    prioritySelected: { backgroundColor: '#f97316', borderColor: '#f97316', },
    priorityText: { color: '#4a5568', fontWeight: '500', textTransform: 'capitalize', },
    priorityTextSelected: { color: '#fff', },
    submitButton: {
        backgroundColor: '#f97316',
        paddingVertical: 15,
        borderRadius: 8,
        alignItems: 'center',
    },
    submitButtonText: { color: '#fff', fontSize: 16, fontWeight: 'bold' },
});