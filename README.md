# Caju Ajuda - Sistema de Helpdesk Multiplataforma

[![Status](https://img.shields.io/badge/status-em_desenvolvimento-yellowgreen.svg)](https://shields.io/)

Um sistema de suporte e gerenciamento de tickets (helpdesk) completo, projetado com uma arquitetura moderna API-First para servir múltiplos clientes, incluindo uma aplicação web para clientes e um aplicativo desktop dedicado para a equipe técnica.

---

## Tabela de Conteúdos
1. [Sobre o Projeto](#1-sobre-o-projeto)
2. [Arquitetura do Sistema](#2-arquitetura-do-sistema)
3. [Funcionalidades](#3-funcionalidades)
4. [Pilha de Tecnologias](#4-pilha-de-tecnologias)
5. [Como Executar o Projeto](#5-como-executar-o-projeto)
6. [Endpoints da API](#6-endpoints-da-api)

---

### 1. Sobre o Projeto

O **Caju Ajuda** é uma solução de suporte ao cliente que visa otimizar o fluxo de atendimento. O sistema permite que clientes abram e acompanhem chamados através de uma interface web intuitiva, enquanto a equipe de suporte utiliza uma aplicação desktop focada para gerenciar e resolver essas solicitações de forma eficiente.

### 2. Arquitetura do Sistema

O projeto é construído sobre uma filosofia **API-First**, onde um backend robusto e centralizado (localizado na pasta `/backend`) serve como a única fonte de verdade para os diferentes clientes (frontends).

<img width="606" height="367" alt="image" src="https://github.com/user-attachments/assets/64c956a8-d4d9-4461-b04a-8d9df86c126e" />


### 3. Funcionalidades

#### Módulo do Cliente (Web)
- [x] Cadastro e Autenticação de Usuários.
- [x] Dashboard principal após o login.
- [x] Criação de novos chamados com anexos.
- [x] Visualização e edição de chamados pessoais.
- [x] Download de anexos.

#### Módulo do Técnico (Desktop)
- [x] Autenticação segura via API com Token JWT.
- [x] Tela de login funcional.
- [x] Visualização da lista de todos os chamados de clientes.
- [ ] Implementação das telas de detalhes e resposta de chamados.

### 4. Pilha de Tecnologias

* **Backend:** Java 17+, Spring Boot, Spring Security (JWT), Spring Data JPA, Hibernate, Maven.
* **Frontend (Web):** Thymeleaf, HTML, CSS, Bootstrap.
* **Frontend (Desktop):** JavaFX, FXML.
* **Banco de Dados:** MySQL.
* **Ferramentas:** Git, Postman, IntelliJ IDEA.

### 5. Como Executar o Projeto

Para executar o ecossistema completo, você precisará rodar o backend e o cliente desktop separadamente.

#### Pré-requisitos
* JDK 17 ou superior
* Maven 3.8+
* Um servidor MySQL local em execução

#### Backend (`/backend`)
1.  **Crie o Banco de Dados:** Conecte-se ao seu MySQL e crie um banco de dados vazio:
    ```sql
    CREATE DATABASE caju_ajuda_db;
    ```
2.  **Configure a Conexão:** Navegue até `/backend` e edite o arquivo `src/main/resources/application.properties`, atualizando as credenciais do seu banco de dados.
3.  **Execute a Aplicação:** Abra a pasta `/backend` em sua IDE e execute a classe principal `ApploginApplication.java`. O servidor irá iniciar na porta `8080`.

#### Cliente Desktop (`/desktop-tecnico`)
1.  Abra a pasta `/desktop-tecnico` em uma **nova janela** da sua IDE.
2.  **Certifique-se de que o backend já está rodando.**
3.  Execute a classe principal `MainApp.java`. A tela de login do técnico deverá aparecer.

### 6. Endpoints da API

A API para o cliente técnico está construída sob o prefixo `/api`.

| Método | Rota                   | Descrição                                 | Acesso     |
| :----- | :--------------------- | :---------------------------------------- | :--------- |
| `POST` | `/api/auth/login`      | Autentica um usuário e retorna um Token JWT. | Público    |
| `GET`  | `/api/tecnico/chamados`| Retorna a lista de todos os chamados.     | `TECNICO`  |

### 7. Próximos Passos

[ ] Implementar um módulo de administração.

[ ] Iniciar o desenvolvimento do cliente mobile para os usuários.

[ ] Adicionar um sistema de notificações.
