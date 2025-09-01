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