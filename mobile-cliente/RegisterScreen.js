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

// --- IMPORTANTE ---
// Este é o IP do seu computador, apontando para o novo endpoint de registo
const API_REGISTER_URL = 'http://192.168.15.12:8080/api/auth/register';

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
            const response = await fetch(API_REGISTER_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    nome: nome,
                    email: email,
                    senha: senha,
                }),
            });

            if (response.ok) {
                // Se o registo for bem-sucedido (status 200)
                Alert.alert(
                    'Sucesso!',
                    'Sua conta foi criada. Enviámos um e-mail de verificação para você.',
                    [
                        // Botão no alerta que, ao ser pressionado, volta para a tela de login
                        { text: 'OK', onPress: () => navigation.goBack() }
                    ]
                );
            } else {
                // Se houver um erro (ex: e-mail já existe), mostra a mensagem do backend
                const errorText = await response.text();
                Alert.alert('Erro no Registo', errorText || 'Não foi possível criar a sua conta.');
            }

        } catch (error) {
            console.error(error);
            Alert.alert('Erro de Conexão', 'Não foi possível conectar ao servidor.');
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