import { useState, useEffect, useRef } from 'react';
import { io } from 'socket.io-client';

const SOCKET_URL = 'http://192.168.15.12:9092'; // URL com a nova porta

export const useSocket = (roomId, onMessageReceived) => {
    const [isConnected, setIsConnected] = useState(false);
    const socketRef = useRef(null);

    useEffect(() => {
        if (!roomId) return;

        console.log(`--- [Hook] Tentando conectar à sala: ${roomId}`);
        const socket = io(SOCKET_URL, {
            reconnectionAttempts: 5,
            transports: ['websocket'], // Força o uso de WebSocket puro
        });

        socketRef.current = socket;

        socket.on('connect', () => {
            console.log(`--- [Hook] Conectado! ID: ${socket.id}`);
            setIsConnected(true);
            socket.emit('joinRoom', roomId);
        });

        socket.on('disconnect', () => {
            console.log("--- [Hook] Desconectado.");
            setIsConnected(false);
        });

        socket.on('nova_mensagem', (message) => {
            console.log("--- [Hook] Mensagem recebida:", message);
            if (onMessageReceived) {
                onMessageReceived(message);
            }
        });

        socket.on('connect_error', (err) => {
            console.error("--- [Hook] Erro de conexão:", err.message);
        });

        return () => {
            console.log("--- [Hook] Desconectando...");
            socket.disconnect();
        };
    }, [roomId]);

    return { isConnected };
};