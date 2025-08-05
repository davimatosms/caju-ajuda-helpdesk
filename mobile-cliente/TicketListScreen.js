import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, TouchableOpacity } from 'react-native';
import * as SecureStore from 'expo-secure-store';
import Ionicons from '@expo/vector-icons/Ionicons';

const API_CLIENTE_URL = 'http://192.168.15.12:8080/api/cliente';

export default function TicketListScreen({ navigation }) {
    const [chamados, setChamados] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchChamados = async () => {
            try {
                const token = await SecureStore.getItemAsync('userToken');
                const response = await fetch(`${API_CLIENTE_URL}/chamados`, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });

                if (response.ok) {
                    const data = await response.json();
                    setChamados(data);
                } else {
                    console.error('Falha ao buscar chamados');
                }
            } catch (error) {
                console.error('Erro de conexão:', error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchChamados();
    }, []);

    const renderItem = ({ item }) => (
        <TouchableOpacity
            style={styles.card}
            onPress={() => navigation.navigate('TicketDetail', { chamadoId: item.id })}
        >
            <View style={styles.cardHeader}>
                <Text style={styles.cardTitle}>#{item.id} - {item.titulo}</Text>
                <Text style={styles.cardDate}>
                    {new Date(item.dataCriacao).toLocaleDateString('pt-BR')}
                </Text>
            </View>
            <View style={styles.cardBody}>
                <View style={styles.badgeContainer}>
                    <Text style={[styles.badge, styles.statusBadge]}>{item.status.replace('_', ' ')}</Text>
                    <Text style={[styles.badge, styles.priorityBadge(item.prioridade)]}>{item.prioridade}</Text>
                </View>
                <Ionicons name="chevron-forward" size={24} color="#cccccc" />
            </View>
        </TouchableOpacity>
    );

    if (isLoading) {
        return <ActivityIndicator style={styles.loader} size="large" color="#f97316" />;
    }

    return (
        <View style={styles.container}>
            <FlatList
                data={chamados}
                renderItem={renderItem}
                keyExtractor={item => item.id.toString()}
                contentContainerStyle={styles.list}
                ListEmptyComponent={<Text style={styles.emptyText}>Nenhum chamado aberto encontrado.</Text>}
            />
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#f0f2f5' },
    list: { padding: 16 },
    loader: { flex: 1, justifyContent: 'center', alignItems: 'center' },
    card: {
        backgroundColor: '#fff',
        borderRadius: 8,
        padding: 16,
        marginBottom: 12,
        shadowColor: "#000",
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.1,
        shadowRadius: 2,
        elevation: 3,
    },
    cardHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginBottom: 12,
        alignItems: 'center',
    },
    cardTitle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#1a202c',
        flexShrink: 1,
    },
    cardDate: {
        fontSize: 12,
        color: '#718096',
        marginLeft: 8,
    },
    cardBody: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    badgeContainer: {
        flexDirection: 'row',
    },
    badge: {
        borderRadius: 12,
        paddingVertical: 4,
        paddingHorizontal: 10,
        fontSize: 12,
        fontWeight: '500',
        marginRight: 8,
        overflow: 'hidden',
        textTransform: 'capitalize',
    },
    statusBadge: {
        backgroundColor: '#e6f7ff',
        color: '#1d6fa5'
    },
    priorityBadge: (priority) => {
        let colors = { backgroundColor: '#f3f4f6', color: '#4b5563' }; // Baixa
        if (priority === 'MEDIA') {
            colors = { backgroundColor: '#fffbe6', color: '#b45309' };
        } else if (priority === 'ALTA') {
            colors = { backgroundColor: '#fee2e2', color: '#b91c1c' };
        }
        return colors;
    },
    emptyText: {
        textAlign: 'center',
        marginTop: 50,
        color: '#718096',
    }
});