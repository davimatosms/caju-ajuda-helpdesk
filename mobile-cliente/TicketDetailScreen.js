import React, { useState, useEffect, useRef, useCallback } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, TextInput, TouchableOpacity, KeyboardAvoidingView, Platform, Alert } from 'react-native';
import * as SecureStore from 'expo-secure-store';
import { useSocket } from './useSocket';

const API_URL = 'http://192.168.15.12:8080';

export default function TicketDetailScreen({ route }) {
    const { chamadoId } = route.params;
    const [chamadoInfo, setChamadoInfo] = useState(null);
    const [mensagens, setMensagens] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [novaMensagem, setNovaMensagem] = useState('');
    const flatListRef = useRef(null);

    const handleMessageReceived = useCallback((newMessage) => {
        setMensagens(prevMensagens => [...prevMensagens, newMessage]);
    }, []);

    const { isConnected } = useSocket(`chamado_${chamadoId}`, handleMessageReceived);

    useEffect(() => {
        const fetchDetalhes = async () => {
            setIsLoading(true);
            try {
                const token = await SecureStore.getItemAsync('userToken');
                const response = await fetch(`${API_URL}/api/cliente/chamados/${chamadoId}`, {
                    headers: { 'Authorization': `Bearer ${token}` },
                });
                if (response.ok) {
                    const data = await response.json();
                    setChamadoInfo(data);
                    setMensagens(data.mensagens || []);
                }
            } catch (error) {
                console.error("Erro ao buscar detalhes:", error);
            } finally {
                setIsLoading(false);
            }
        };
        fetchDetalhes();
    }, [chamadoId]);

    const handleSendMessage = async () => {
        if (!novaMensagem.trim()) return;
        try {
            const token = await SecureStore.getItemAsync('userToken');
            await fetch(`${API_URL}/api/cliente/chamados/${chamadoId}/mensagens`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                body: JSON.stringify({ texto: novaMensagem }),
            });
            setNovaMensagem('');
        } catch (error) {
            Alert.alert('Erro', 'Não foi possível enviar a sua mensagem.');
        }
    };

    const renderMensagem = ({ item }) => (
        <View style={[styles.messageBubble, item.autor.role === 'TECNICO' ? styles.tecnicoBubble : styles.clienteBubble]}>
            <Text style={styles.authorText}>{item.autor.nome}</Text>
            <Text style={styles.messageText}>{item.texto}</Text>
            <Text style={styles.timestampText}>{new Date(item.dataEnvio).toLocaleString('pt-BR')}</Text>
        </View>
    );

    if (isLoading || !chamadoInfo) {
        return <ActivityIndicator style={styles.loader} size="large" color="#f97316" />;
    }

    return (
        <KeyboardAvoidingView
            behavior={Platform.OS === "ios" ? "padding" : "height"}
            style={styles.container}
            keyboardVerticalOffset={90}
        >
            <View style={styles.header}>
                <Text style={styles.title} numberOfLines={1}>{chamadoInfo.titulo}</Text>
                <Text style={styles.status}>{chamadoInfo.status.replace('_', ' ')}</Text>
            </View>
            <FlatList
                ref={flatListRef}
                data={mensagens}
                renderItem={renderMensagem}
                keyExtractor={(item, index) => item.id?.toString() || index.toString()}
                contentContainerStyle={styles.chatContainer}
                onContentSizeChange={() => flatListRef.current?.scrollToEnd({ animated: true })}
                onLayout={() => flatListRef.current?.scrollToEnd({ animated: true })}
            />
            <View style={styles.inputContainer}>
                <TextInput
                    style={styles.textInput}
                    value={novaMensagem}
                    onChangeText={setNovaMensagem}
                    placeholder={isConnected ? "Digite sua mensagem..." : "Conectando ao chat..."}
                    editable={isConnected}
                />
                <TouchableOpacity
                    style={[styles.sendButton, !isConnected && styles.sendButtonDisabled]}
                    onPress={handleSendMessage}
                    disabled={!isConnected}
                >
                    <Text style={styles.sendButtonText}>Enviar</Text>
                </TouchableOpacity>
            </View>
        </KeyboardAvoidingView>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#f0f2f5' },
    loader: { flex: 1, justifyContent: 'center', alignItems: 'center' },
    header: { padding: 16, backgroundColor: '#fff', borderBottomWidth: 1, borderColor: '#e2e8f0' },
    title: { fontSize: 20, fontWeight: 'bold', color: '#1a202c', marginBottom: 4 },
    status: { fontSize: 14, color: '#4a5568', textTransform: 'capitalize' },
    chatContainer: { padding: 16 },
    messageBubble: { borderRadius: 12, padding: 12, marginBottom: 10, maxWidth: '80%' },
    clienteBubble: { backgroundColor: '#e2e8f0', alignSelf: 'flex-start' },
    tecnicoBubble: { backgroundColor: '#fde68a', alignSelf: 'flex-end' },
    authorText: { fontWeight: 'bold', marginBottom: 4, color: '#4a5568' },
    messageText: { fontSize: 16, color: '#1a202c' },
    timestampText: { fontSize: 10, color: '#718096', alignSelf: 'flex-end', marginTop: 8 },
    inputContainer: { flexDirection: 'row', padding: 10, backgroundColor: '#fff', borderTopWidth: 1, borderColor: '#e2e8f0' },
    textInput: { flex: 1, height: 40, borderWidth: 1, borderColor: '#dee2e6', borderRadius: 20, paddingHorizontal: 15, backgroundColor: '#f8f9fa' },
    sendButton: { marginLeft: 10, justifyContent: 'center', alignItems: 'center', backgroundColor: '#f97316', borderRadius: 20, paddingHorizontal: 20 },
    sendButtonText: { color: '#fff', fontWeight: 'bold' },
    sendButtonDisabled: { backgroundColor: '#fdba74' }
});