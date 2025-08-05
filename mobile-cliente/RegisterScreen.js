import React, { useState } from 'react';
import {
    View,
    Text,
    TextInput,
    TouchableOpacity,
    StyleSheet,
    Alert,
    ActivityIndicator,
    Image
} from 'react-native';

// --- IMPORTANTE: USE O SEU IP AQUI ---
// URLs da API para os dois endpoints que vamos chamar
const API_BASE_URL = 'http://192.168.15.12:8080';
const API_REGISTER_URL = `${API_BASE_URL}/api/auth/register`;
const API_RESEND_EMAIL_URL = `${API_BASE_URL}/api/reenviar-verificacao`;

export default function RegisterScreen({ navigation }) {
    const [nome, setNome] = useState('');
    const [email, setEmail] = useState('');
    const [senha, setSenha] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    // Função que será chamada ao pressionar o botão "Registar"
    const handleRegister = async () => {
        if (!nome || !email || !senha) {
            Alert.alert('Erro', 'Por favor, preencha todos os campos.');
            return;
        }
        setIsLoading(true);

        try {
            // --- PASSO 1: Tenta registar o utilizador ---
            const registerResponse = await fetch(API_REGISTER_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nome, email, senha }),
            });

            if (!registerResponse.ok) {
                const errorText = await registerResponse.text();
                throw new Error(errorText || 'Não foi possível criar a sua conta.');
            }

            // --- PASSO 2: Se o registo funcionou, dispara o e-mail ---
            await fetch(API_RESEND_EMAIL_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email }),
            });

            // Mostra a mensagem de sucesso
            Alert.alert(
                'Sucesso!',
                'Sua conta foi criada. Enviámos um e-mail de verificação para você.',
                [{ text: 'OK', onPress: () => navigation.goBack() }] // Volta para a tela de login
            );

        } catch (error) {
            Alert.alert('Erro no Registo', error.message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <View style={styles.container}>
            <Image source={{ uri: 'https://i.imgur.com/yyFJDsb.png' }} style={styles.logo} />
            <Text style={styles.title}>Crie sua Conta</Text>

            <TextInput
                style={styles.input}
                placeholder="Nome Completo"
                value={nome}
                onChangeText={setNome}
                autoCapitalize="words"
            />
            <TextInput
                style={styles.input}
                placeholder="E-mail"
                value={email}
                onChangeText={setEmail}
                keyboardType="email-address"
                autoCapitalize="none"
            />
            <TextInput
                style={styles.input}
                placeholder="Senha"
                value={senha}
                onChangeText={setSenha}
                secureTextEntry
            />

            <TouchableOpacity style={styles.button} onPress={handleRegister} disabled={isLoading}>
                {isLoading ? <ActivityIndicator color="#fff" /> : <Text style={styles.buttonText}>Registar</Text>}
            </TouchableOpacity>

            <TouchableOpacity onPress={() => navigation.goBack()}>
                <Text style={styles.backLink}>Já tem uma conta? Faça o login</Text>
            </TouchableOpacity>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f8f9fa',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 20,
    },
    logo: {
        width: 80,
        height: 80,
        marginBottom: 20,
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        color: '#1a202c',
        marginBottom: 30,
    },
    input: {
        width: '100%',
        height: 50,
        backgroundColor: '#ffffff',
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
    backLink: {
        marginTop: 20,
        color: '#f97316',
        fontWeight: '600',
    }
});