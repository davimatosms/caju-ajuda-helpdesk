import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, TextInput, TouchableOpacity, Alert, ActivityIndicator, ScrollView } from 'react-native';
import * as SecureStore from 'expo-secure-store';
import Ionicons from '@expo/vector-icons/Ionicons';

const API_URL = 'http://192.168.15.12:8080/api/cliente/perfil';

export default function ProfileScreen({ navigation }) {
    const [user, setUser] = useState({ nome: '', email: '' });
    const [senhaAtual, setSenhaAtual] = useState('');
    const [novaSenha, setNovaSenha] = useState('');
    const [confirmaSenha, setConfirmaSenha] = useState('');
    const [isLoading, setIsLoading] = useState(true);
    const [isUpdating, setIsUpdating] = useState(false);

    useEffect(() => {
        const fetchUserData = async () => {
            setIsLoading(true);
            try {
                const token = await SecureStore.getItemAsync('userToken');
                const response = await fetch(API_URL, {
                    headers: { 'Authorization': `Bearer ${token}` },
                });
                if (response.ok) {
                    const data = await response.json();
                    setUser(data);
                }
            } catch (error) {
                Alert.alert("Erro", "Não foi possível carregar os dados do perfil.");
            } finally {
                setIsLoading(false);
            }
        };
        fetchUserData();
    }, []);

    const handleChangePassword = async () => {
        if (!senhaAtual || !novaSenha || !confirmaSenha) {
            Alert.alert('Atenção', 'Preencha todos os campos de senha.');
            return;
        }
        if (novaSenha !== confirmaSenha) {
            Alert.alert('Erro', 'As novas senhas não coincidem.');
            return;
        }
        setIsUpdating(true);
        try {
            const token = await SecureStore.getItemAsync('userToken');
            const response = await fetch(`${API_URL}/alterar-senha`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify({ senhaAtual, novaSenha }),
            });

            const responseText = await response.text();
            if (response.ok) {
                Alert.alert('Sucesso', 'Senha alterada com sucesso!');
                setSenhaAtual('');
                setNovaSenha('');
                setConfirmaSenha('');
            } else {
                throw new Error(responseText);
            }
        } catch (error) {
            Alert.alert('Erro ao Alterar Senha', error.message);
        } finally {
            setIsUpdating(false);
        }
    };

    const handleLogout = async () => {
        await SecureStore.deleteItemAsync('userToken');
        navigation.replace('Login');
    };

    if (isLoading) {
        return <ActivityIndicator style={styles.loader} size="large" color="#f97316" />;
    }

    return (
        <ScrollView style={styles.container}>
            <View style={styles.card}>
                <Text style={styles.cardTitle}>Informações da Conta</Text>
                <View style={styles.infoRow}>
                    <Ionicons name="person-outline" size={20} color="#6c757d" />
                    <Text style={styles.infoText}>{user.nome}</Text>
                </View>
                <View style={styles.infoRow}>
                    <Ionicons name="mail-outline" size={20} color="#6c757d" />
                    <Text style={styles.infoText}>{user.email}</Text>
                </View>
            </View>

            <View style={styles.card}>
                <Text style={styles.cardTitle}>Alterar Senha</Text>
                <TextInput
                    style={styles.input}
                    placeholder="Senha Atual"
                    secureTextEntry
                    value={senhaAtual}
                    onChangeText={setSenhaAtual}
                />
                <TextInput
                    style={styles.input}
                    placeholder="Nova Senha"
                    secureTextEntry
                    value={novaSenha}
                    onChangeText={setNovaSenha}
                />
                <TextInput
                    style={styles.input}
                    placeholder="Confirmar Nova Senha"
                    secureTextEntry
                    value={confirmaSenha}
                    onChangeText={setConfirmaSenha}
                />
                <TouchableOpacity style={styles.button} onPress={handleChangePassword} disabled={isUpdating}>
                    {isUpdating ? <ActivityIndicator color="#fff" /> : <Text style={styles.buttonText}>Salvar Nova Senha</Text>}
                </TouchableOpacity>
            </View>

            <TouchableOpacity style={[styles.button, styles.logoutButton]} onPress={handleLogout}>
                <Text style={styles.buttonText}>Sair da Conta</Text>
            </TouchableOpacity>
        </ScrollView>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#f0f2f5', padding: 16 },
    loader: { flex: 1, justifyContent: 'center', alignItems: 'center' },
    card: {
        backgroundColor: '#fff',
        borderRadius: 8,
        padding: 20,
        marginBottom: 20,
        shadowColor: "#000",
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.1,
        shadowRadius: 2,
        elevation: 3,
    },
    cardTitle: {
        fontSize: 18,
        fontWeight: 'bold',
        color: '#1a202c',
        marginBottom: 20,
    },
    infoRow: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: 15,
    },
    infoText: {
        fontSize: 16,
        marginLeft: 10,
        color: '#4a5568',
    },
    input: {
        width: '100%',
        height: 50,
        backgroundColor: '#f8f9fa',
        borderWidth: 1,
        borderColor: '#dee2e6',
        borderRadius: 8,
        paddingHorizontal: 15,
        marginBottom: 15,
        fontSize: 16,
    },
    button: {
        width: '100%',
        height: 50,
        backgroundColor: '#f97316',
        borderRadius: 8,
        alignItems: 'center',
        justifyContent: 'center',
        marginTop: 10,
    },
    buttonText: {
        color: '#ffffff',
        fontSize: 16,
        fontWeight: 'bold',
    },
    logoutButton: {
        backgroundColor: '#ef4444',
    }
});