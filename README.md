# <img width="35" height="35" alt="logo caju (1)-Photoroom" src="https://github.com/user-attachments/assets/0ded85a6-c88c-416c-80d1-aaf5b5effb11" /> Caju Ajuda - Sistema de Helpdesk Multiplataforma




[![Status](https://img.shields.io/badge/status-em_desenvolvimento-yellowgreen.svg)](https://shields.io/)
[![Linguagem](https://img.shields.io/badge/Java-17-blue.svg)](https://shields.io/)
[![Framework](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://shields.io/)
[![Frontend](https://img.shields.io/badge/Frontend-Web%20%7C%20Desktop%20%7C%20Mobile-orange.svg)](https://shields.io/)

Um sistema de suporte e gerenciamento de tickets (helpdesk) completo, projetado com uma arquitetura moderna API-First para servir múltiplos clientes, incluindo uma aplicação web para clientes e um aplicativo desktop dedicado para a equipe técnica.

---

## Tabela de Conteúdos
1. [Sobre o Projeto](#1-sobre-o-projeto)
   
    1.1. [Backlog do projeto](https://github.com/davimatosms/caju-ajuda-helpdesk/blob/main/Backlog.md)
2. [Arquitetura do Sistema](#2-arquitetura-do-sistema)
3. [Funcionalidades](#3-funcionalidades-detalhadas)
4. [Pilha de Tecnologias](#4-pilha-de-tecnologias)
5. [Como Executar o Projeto](#5-como-executar-o-projeto)
6. [Endpoints da API](#6-endpoints-da-api)

---

### 1. Sobre o Projeto

O **Caju Ajuda** é uma solução de suporte ao cliente que visa otimizar o fluxo de atendimento. O sistema permite que clientes abram e acompanhem chamados através de uma interface web intuitiva, enquanto a equipe de suporte utiliza uma aplicação desktop focada para gerenciar e resolver essas solicitações de forma eficiente.

### 2. Arquitetura do Sistema

#### 2.1 Visão Geral
O projeto é construído sobre uma filosofia API-First, onde um backend robusto e centralizado serve como o cérebro da operação e a única fonte de verdade para os diferentes clientes (frontends). Esta abordagem garante a consistência dos dados e da lógica de negócio, além de permitir a fácil adição de novos clientes no futuro.

<img width="606" height="367" alt="Arquitetura do Sistema Caju Ajuda" src="https://github.com/user-attachments/assets/64c956a8-d4d9-4461-b04a-8d9df86c126e" />

#### 2.2 Componentes
* **Backend (/backend):** Uma API RESTful desenvolvida em Java com Spring Boot. É responsável por toda a lógica de negócio, gerenciamento de usuários, processamento de chamados, segurança e a integração com o banco de dados e serviços de IA.

* **Cliente Web (/backend/src/main/resources/templates):** Uma aplicação renderizada no servidor com Thymeleaf, destinada ao Cliente Final para acesso via navegador.

* **Cliente Desktop (/desktop-tecnico):** Uma aplicação nativa desenvolvida em JavaFX, destinada à Equipe Técnica para o gerenciamento completo dos chamados.

* **Cliente Mobile (/mobile-cliente):** Uma aplicação desenvolvida em React Native, destinada ao Cliente Final para uma experiência otimizada em dispositivos móveis.


### 3. funcionalidades detalhadas

#### 3.1 Funcionalidades Implementadas

* **Autenticação Híbrida:** Sistema de segurança robusto com Spring Security, suportando login via formulário (sessões) para a web e autenticação via Token JWT (stateless) para a API (consumida pelos clientes Desktop e Mobile).

* **Cadastro com Verificação de E-mail:** Novos clientes recebem um e-mail de confirmação para validar a conta antes de poderem fazer login.

* **Gerenciamento Completo de Chamados (Cliente Web):** Clientes podem criar, visualizar, editar e anexar múltiplos arquivos aos seus chamados.

* **Comunicação e Notificações:** Sistema de chat em tempo real (baseado em WebSocket) e notificações por e-mail para o cliente quando um técnico responde.

* **Priorização com IA:** Integração com a API do Google Gemini para analisar o conteúdo de novos chamados e sugerir uma prioridade automaticamente.

* **Listagem de Chamados (Técnico Desktop):** O cliente desktop já é capaz de se autenticar na API e obter uma lista completa de todos os chamados.

#### 3.2 Roadmap de Funcionalidades Futuras

* **Módulo de Administração:** Uma área dedicada para administradores gerenciarem usuários, configurações do sistema e políticas de SLA.

* **Dashboard de Métricas:** Painéis com gráficos e relatórios sobre o tempo de resposta, volume de chamados e performance da equipe.

* **IA Avançada:** Expansão do uso da IA para sugerir soluções com base em chamados históricos e categorizar chamados por assunto.

* **Conclusão do Cliente Mobile:** Finalizar o desenvolvimento de todas as funcionalidades do cliente web no aplicativo React Native.

### 4. Pilha de Tecnologias
* **Backend:** Java 17+, Spring Boot, Spring Security (JWT), Spring Data JPA, Hibernate, Maven.

* **Frontend (Web):** Thymeleaf, HTML, CSS, Bootstrap.

* **Frontend (Desktop):** JavaFX, FXML.

* **Frontend (Mobile):** React Native.

* **Banco de Dados:** MySQL.

* **Inteligência Artificial:** Google Gemini API.

* **Ferramentas:** Git, Postman, IntelliJ IDEA.

### 5. Como Executar o Projeto

Para executar o ecossistema completo, você precisará rodar o backend e o cliente desktop separadamente.

#### Pré-requisitos

* Java (JDK) 17+

* Node.js e npm (para o cliente mobile)

* Maven 3.8+

* Um servidor MySQL local em execução

#### Backend (`/backend`)
1.  **Crie o Banco de Dados:** Conecte-se ao seu MySQL e crie um banco de dados vazio:
    ```sql
    CREATE DATABASE caju_ajuda_db;
    ```
2.  **Configure a Conexão:** Navegue até `/backend` e edite o arquivo `src/main/resources/application.properties`, atualizando as credenciais do seu banco de dados.
3.  **Configure a Aplicação:** Edite o arquivo `src/main/resources/application.properties`, com suas credenciais do banco e a chave da API do Gemini.
4.  **Execute:** Utilize o Maven `(./mvnw spring-boot:run)`, ou sua IDE para iniciar a classe ApploginApplication.java. O servidor iniciará na porta 8080.
5.  **Execute a Aplicação:** Abra a pasta `/backend` em sua IDE e execute a classe principal `ApploginApplication.java`. O servidor irá iniciar na porta `8080`.

#### Cliente Desktop (`/desktop-tecnico`)
1.  Abra a pasta `/desktop-tecnico` em uma **nova janela** da sua IDE.
2.  **Certifique-se de que o backend já está rodando.**
3.  Execute a classe principal `MainApp.java`. A tela de login do técnico deverá aparecer.

#### Configuração do Cliente Mobile
1. Navegue até a pasta: cd mobile-cliente

2. Instale as dependências: npm install

3. Execute o App:

4. Para Android: npx react-native run-android

5. Para iOS: npx react-native run-ios

### 6. Endpoints da API

A API para o cliente técnico está construída sob o prefixo `/api`.

| Método | Rota                   | Descrição                                 | Acesso     |
| :----- | :--------------------- | :---------------------------------------- | :--------- |
| `POST` | `/api/auth/login`      | Autentica um usuário e retorna um Token JWT. | Público    |
| `GET`  | `/api/tecnico/chamados`| Retorna a lista de todos os chamados.     | `TECNICO`  |

### 7. Autor
<img src="https://avatars.githubusercontent.com/u/101799753?v=4" width=115><br><sub>Davi Matos Marques Silva</sub>

Este projeto foi desenvolvido por Davi Matos como parte de seu portfólio e estudos em desenvolvimento de software full-stack.
