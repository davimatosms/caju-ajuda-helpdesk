# Product Backlog: Caju Ajuda

_Última Atualização: 25 de Agosto de 2025_

### Visão Geral
Este backlog documenta as funcionalidades, melhorias e débitos técnicos para o ecossistema Caju Ajuda. O objetivo é evoluir a plataforma para um sistema de helpdesk robusto, escalável e com uma experiência de utilizador coesa em todas as frentes (Web, Mobile, Desktop).

---

### **Prioridade Alta (Ciclo de Desenvolvimento Atual)**

*Funcionalidades essenciais para completar os fluxos de trabalho existentes e entregar valor imediato.*

#### 🚀 **Épico 1: Gestão de Contas de Administrador**
* **Descrição:** Expandir as funcionalidades do painel de administração para fornecer um ciclo de vida completo para a gestão de contas de técnicos.
* **ID:** `A-1`
* **User Story:** **Como Admin**, quero poder **editar os dados** (nome, e-mail) e **redefinir a senha** de um técnico para gerir as suas credenciais.
* **Tarefas Técnicas:**
    - Criar endpoints `PUT /api/admin/tecnicos/{id}` e `POST /api/admin/tecnicos/{id}/reset-password`.
    - Implementar a lógica de negócio no `AdminService` com validações.
    - Adicionar botões "Editar" e "Redefinir Senha" na UI de `admin-tecnicos.html`.

#### 👤 **Épico 2: Paridade de Funcionalidades do Cliente Mobile**
* **Descrição:** Garantir que a experiência mobile seja tão completa quanto a da web, focando em funcionalidades críticas de interação com os chamados.
* **ID:** `C-1`
* **User Story:** **Como Cliente (Mobile)**, quero poder **enviar e visualizar anexos** no chat para partilhar imagens e documentos importantes.
* **Tarefas Técnicas:**
    - Implementar a lógica de upload de ficheiros (`multipart/form-data`) no `TicketDetailScreen.js`.
    - Adaptar a UI do chat para renderizar links de download ou pré-visualizações de imagens.
    - Garantir que o `FileStorageService` no backend lida corretamente com os uploads do cliente React Native.

#### ⚙️ **Épico 3: Melhorias no Fluxo de Trabalho do Técnico**
* **Descrição:** Otimizar a aplicação desktop para tornar o trabalho da equipa de suporte mais proativo e organizado.
* **ID:** `T-1`
* **User Story:** **Como Técnico**, quero **receber uma notificação em tempo real** na aplicação desktop quando um novo chamado for criado.
* **Tarefas Técnicas:**
    - Criar um novo tópico WebSocket no backend (ex: `/topic/chamados/novos`).
    - Modificar o `ChamadoService` para que, ao criar um novo chamado, publique uma mensagem neste tópico.
    - O `WebSocketService` do cliente JavaFX deve subscrever este novo tópico e acionar um alerta visual/sonoro.

---

### **Prioridade Média (Próximo Ciclo)**

*Funcionalidades que agregam valor significativo e melhoram a experiência geral.*

#### 🚀 **Épico 1: Gestão de Contas de Administrador (Continuação)**
* **ID:** `A-2`
* **User Story:** **Como Admin**, quero poder **desativar/reativar** uma conta de técnico para controlar o acesso de forma temporária.

#### 👤 **Épico 2: Autonomia do Cliente**
* **ID:** `C-2`
* **User Story:** **Como Cliente**, quero poder **fechar o meu próprio chamado** se o meu problema já tiver sido resolvido.
* **ID:** `C-3`
* **User Story:** **Como Cliente**, quero poder **reabrir um chamado recém-fechado** se o problema voltar a ocorrer.

#### ⚙️ **Épico 3: Melhorias no Fluxo de Trabalho do Técnico (Continuação)**
* **ID:** `T-2`
* **User Story:** **Como Técnico**, quero poder **atribuir um chamado a mim mesmo** para sinalizar à equipa quem é o responsável.
* **ID:** `T-3`
* **User Story:** **Como Técnico**, quero poder **filtrar a lista de chamados** por status, prioridade ou cliente.

---

### **Débito Técnico & Otimizações (Contínuo)**

*Tarefas de "limpeza" e melhoria da base do código para garantir a saúde e a escalabilidade do projeto a longo prazo.*

| ID    | Tarefa Técnica                                                                                                                                    | Justificação                                                                                                                                                                                                       |
| :---- | :------------------------------------------------------------------------------------------------------------------------------------------------ | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **D-1** | **Implementar Testes Unitários e de Integração:** Adicionar cobertura de testes para os `Services` e `Controllers` do backend.                  | - Mitigar regressões e garantir a estabilidade do código-fonte à medida que novas funcionalidades são adicionadas.                                                                                                    |
| **D-2** | **Refatorar DTOs Duplicados:** Unificar os DTOs entre os projetos `backend` e `desktop-tecnico` num módulo Maven partilhado.                          | - O `DetalhesChamadoDto` e outros existem em ambos os projetos, violando o princípio DRY (Don't Repeat Yourself) e dificultando a manutenção. |
| **D-3** | **Implementar Paginação (Pagination):** Substituir o retorno de listas completas por respostas paginadas nos endpoints da API.                        | - Prevenir problemas de performance e timeouts à medida que o volume de dados (chamados, utilizadores) cresce. Atinge principalmente `TecnicoApiController`.                                                      |
| **D-4** | **Centralizar e Padronizar o Logging:** Substituir todos os `System.out.println` por um logger SLF4J com níveis adequados (INFO, DEBUG, ERROR). | - Ficheiros como o `DataLoader` usam `System.out`, o que é inadequado para ambientes de produção e dificulta a monitorização e a depuração.                                      |
| **D-5** | **Configurar um Error Handler Global:** Implementar um `@ControllerAdvice` no backend para gerir exceções e retornar respostas de erro padronizadas. | - Evitar páginas de erro genéricas como a "Whitelabel Error Page" e fornecer feedback claro e consistente para os clientes da API e para o frontend web. |