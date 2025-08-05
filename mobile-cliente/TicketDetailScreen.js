import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator } from 'react-native';
import * as SecureStore from 'expo-secure-store';

const API_CLIENTE_URL = 'http://192.168.15.12:8080/api/cliente';

export default function TicketDetailScreen({ route }) {
    const { chamadoId } = route.params;
    const [chamado, setChamado] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchDetalhes = async () => {
            try {
                const token = await SecureStore.getItemAsync('userToken');
                const response = await fetch(`${API_CLIENTE_URL}/chamados/${chamadoId}`, {
                    headers: { 'Authorization': `Bearer ${token}` },
                });
                if (response.ok) {
                    const data = await response.json();
                    setChamado(data);
                }
            } catch (error) {
                console.error("Erro ao buscar detalhes do chamado:", error);
            } finally {
                setIsLoading(false);
            }
        };
        fetchDetalhes();
    }, [chamadoId]);

    const renderMensagem = ({ item }) => (
        <View style={[
            styles.messageBubble,
            item.autor.role === 'TECNICO'
                ? styles.tecnicoBubble
                : styles.clienteBubble
        ]}>
            <Text style={styles.authorText}>{item.autor.nome}</Text>
            <Text style={styles.messageText}>{item.texto}</Text>
            <Text style={styles.timestampText}>
                {new Date(item.dataEnvio).toLocaleString('pt-BR')}
            </Text>
        </View>
    );

    if (isLoading || !chamado) {
        return <ActivityIndicator style={styles.loader} size="large" color="#f97316" />;
    }

    return (
        <View style={styles.container}>
            <View style={styles.header}>
                <Text style={styles.title}>{chamado.titulo}</Text>
                <Text style={styles.status}>{chamado.status.replace('_', ' ')}</Text>
            </View>
            <FlatList
                data={chamado.mensagens}
                renderItem={renderMensagem}
                keyExtractor={(item) => item.id.toString()}
                contentContainerStyle={styles.chatContainer}
            />
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#f0f2f5' },
    loader: { flex: 1, justifyContent: 'center', alignItems: 'center' },
    header: { padding: 16, backgroundColor: '#fff', borderBottomWidth: 1, borderColor: '#e2e8f0' },
    title: { fontSize: 22, fontWeight: 'bold', color: '#1a202c', marginBottom: 4 },
    status: { fontSize: 14, color: '#4a5568', textTransform: 'capitalize' },
    chatContainer: { padding: 16 },
    messageBubble: { borderRadius: 12, padding: 12, marginBottom: 10, maxWidth: '80%' },
    clienteBubble: { backgroundColor: '#e2e8f0', alignSelf: 'flex-start' },
    tecnicoBubble: { backgroundColor: '#fde68a', alignSelf: 'flex-end' }, // Cor diferente para o técnico
    authorText: { fontWeight: 'bold', marginBottom: 4, color: '#4a5568' },
    messageText: { fontSize: 16, color: '#1a202c' },
    timestampText: { fontSize: 10, color: '#718096', alignSelf: 'flex-end', marginTop: 8 },
});