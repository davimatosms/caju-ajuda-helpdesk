import React, { useState } from 'react';
import {
    StyleSheet,
    Text,
    View,
    TextInput,
    TouchableOpacity,
    Alert,
    ActivityIndicator,
    Image
} from 'react-native';

// Imports para a navegação
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';

// Importa as outras telas
import HomeScreen from './HomeScreen';
import RegisterScreen from './RegisterScreen'; // Certifique-se de que o ficheiro RegisterScreen.js existe

// --- SEU IP ESTÁ CONFIGURADO AQUI ---
const API_URL = 'http://192.168.15.12:8080/api/auth/login';

// Cria a pilha de navegação
const Stack = createStackNavigator();

// --- Componente da Tela de Login ---
function LoginScreen({ navigation }) {
    const [email, setEmail] = useState('');
    const [senha, setSenha] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleLogin = async () => {
        if (!email || !senha) {
            Alert.alert('Erro', 'Por favor, preencha o e-mail e a senha.');
            return;
        }
        setIsLoading(true);
        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email, senha: senha }),
            });

            if (response.ok) {
                const data = await response.json();
                // No futuro, vamos guardar o data.token aqui
                navigation.replace('Home'); // Navega para a HomeScreen em caso de sucesso
            } else {
                const errorData = await response.text();
                Alert.alert('Falha no Login', errorData || 'E-mail ou senha inválidos.');
            }
        } catch (error) {
            console.error(error);
            Alert.alert('Erro de Conexão', 'Não foi possível conectar ao servidor. Verifique o seu IP e se o backend está a correr.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <View style={styles.container}>
            <Image source={{ uri: 'https://i.imgur.com/yyFJDsb.png' }} style={styles.logo} />
            <Text style={styles.title}>Acesse o Caju Ajuda</Text>

            <TextInput
                style={styles.input}
                placeholder="Digite seu e-mail"
                value={email}
                onChangeText={setEmail}
                keyboardType="email-address"
                autoCapitalize="none"
            />
            <TextInput
                style={styles.input}
                placeholder="Digite sua senha"
                value={senha}
                onChangeText={setSenha}
                secureTextEntry
            />

            <TouchableOpacity style={styles.button} onPress={handleLogin} disabled={isLoading}>
                {isLoading ? <ActivityIndicator color="#fff" /> : <Text style={styles.buttonText}>Entrar</Text>}
            </TouchableOpacity>

            <TouchableOpacity onPress={() => navigation.navigate('Register')}>
                <Text style={styles.registerLink}>Não tem uma conta? Registe-se</Text>
            </TouchableOpacity>
        </View>
    );
}

// --- Componente principal que gere a navegação ---
export default function App() {
    return (
        <NavigationContainer>
            <Stack.Navigator initialRouteName="Login">
                <Stack.Screen name="Login" component={LoginScreen} options={{ headerShown: false }} />
                <Stack.Screen name="Register" component={RegisterScreen} options={{ title: 'Crie sua Conta' }} />
                <Stack.Screen name="Home" component={HomeScreen} options={{ title: 'Dashboard Caju Ajuda' }}/>
            </Stack.Navigator>
        </NavigationContainer>
    );
}

// --- Estilos ---
const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f8f9fa',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 20,
    },
    logo: {
        width: 100,
        height: 100,
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
    registerLink: {
        marginTop: 20,
        color: '#f97316',
        fontWeight: '600',
    }
});