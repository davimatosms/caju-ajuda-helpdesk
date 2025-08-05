import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import Ionicons from '@expo/vector-icons/Ionicons';

import LoginScreen from './LoginScreen';
import RegisterScreen from './RegisterScreen';
import TicketListScreen from './TicketListScreen';
import NewTicketScreen from './NewTicketScreen';
import ProfileScreen from './ProfileScreen';
import TicketDetailScreen from './TicketDetailScreen'; // Importa a nova tela

const Stack = createStackNavigator();
const Tab = createBottomTabNavigator();

function MainAppTabs() {
    return (
        <Tab.Navigator
            screenOptions={({ route }) => ({
                tabBarIcon: ({ focused, color, size }) => {
                    let iconName;
                    if (route.name === 'Meus Chamados') {
                        iconName = focused ? 'file-tray-full' : 'file-tray-full-outline';
                    } else if (route.name === 'Novo Chamado') {
                        iconName = focused ? 'add-circle' : 'add-circle-outline';
                    } else if (route.name === 'Perfil') {
                        iconName = focused ? 'person-circle' : 'person-circle-outline';
                    }
                    return <Ionicons name={iconName} size={size} color={color} />;
                },
                tabBarActiveTintColor: '#f97316',
                tabBarInactiveTintColor: 'gray',
            })}
        >
            <Tab.Screen name="Meus Chamados" component={TicketListScreen} />
            <Tab.Screen name="Novo Chamado" component={NewTicketScreen} />
            <Tab.Screen name="Perfil" component={ProfileScreen} />
        </Tab.Navigator>
    );
}

export default function App() {
    return (
        <NavigationContainer>
            <Stack.Navigator>
                <Stack.Screen name="Login" component={LoginScreen} options={{ headerShown: false }} />
                <Stack.Screen name="Register" component={RegisterScreen} options={{ title: 'Crie sua Conta' }} />
                <Stack.Screen
                    name="MainApp"
                    component={MainAppTabs}
                    options={{ headerShown: false }}
                />
                <Stack.Screen
                    name="TicketDetail"
                    component={TicketDetailScreen}
                    options={{ title: 'Detalhes do Chamado' }}
                />
            </Stack.Navigator>
        </NavigationContainer>
    );
}