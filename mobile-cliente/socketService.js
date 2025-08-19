import { io } from 'socket.io-client';
import * as SecureStore from 'expo-secure-store';

const API_URL = 'http://192.168.15.12:9092'; // A porta do seu servidor Socket.IO

class SocketService {
    socket = null;

    async connect() {
        // Evita criar múltiplas conexões
        if (this.socket && this.socket.connected) {
            return;
        }

        console.log("--- [SocketService] Tentando conectar...");
        const token = await SecureStore.getItemAsync('userToken');
        if (!token) {
            console.error("--- [SocketService] ERRO: Token não encontrado.");
            return;
        }

        // A autenticação no Socket.IO é feita de forma diferente, mas por agora vamos focar na conexão
        this.socket = io(API_URL, {
            // No futuro, podemos adicionar a autenticação aqui se necessário
            // auth: { token }
        });

        this.socket.on('connect', () => {
            console.log(`--- [SocketService] Conectado com sucesso! ID: ${this.socket.id}`);
        });

        this.socket.on('disconnect', () => {
            console.log("--- [SocketService] Desconectado.");
        });
    }

    disconnect() {
        if (this.socket) {
            console.log("--- [SocketService] Desconectando...");
            this.socket.disconnect();
            this.socket = null;
        }
    }

    joinRoom(chamadoId) {
        if (this.socket && this.socket.connected) {
            const roomName = `chamado_${chamadoId}`;
            console.log(`--- [SocketService] Entrando na sala: ${roomName}`);
            this.socket.emit('joinRoom', roomName);
        }
    }

    onNewMessage(callback) {
        if (this.socket) {
            // Remove qualquer ouvinte antigo para evitar duplicação
            this.socket.off('nova_mensagem');

            // Adiciona o novo ouvinte
            this.socket.on('nova_mensagem', callback);
        }
    }
}

// Exportamos uma única instância do serviço (Singleton)
const socketService = new SocketService();
export default socketService;