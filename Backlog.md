# Product Backlog: Caju Ajuda

_Última Atualização: 25 de Agosto de 2025_

### Visão Geral
Este backlog documenta as funcionalidades, melhorias e débitos técnicos para o ecossistema Caju Ajuda. O objetivo é evoluir a plataforma para um sistema de helpdesk robusto, escalável e com uma experiência de utilizador coesa em todas as frentes (Web, Mobile, Desktop).

---

### **Prioridade Alta (Ciclo de Desenvolvimento Atual)**
*Funcionalidades essenciais para completar os fluxos de trabalho existentes e entregar valor imediato.*

#### 🚀 **Épico 1: Gestão de Contas de Administrador**
* **Descrição:** Expandir as funcionalidades do painel de administração para fornecer um ciclo de vida completo para a gestão de contas de técnicos.

| ID    | User Story                                                                                                              | Tarefas Técnicas Sugeridas                                                                                                                              |
| :---- | :---------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **A-1** | **Como Admin**, quero poder **editar os dados** (nome, e-mail) de um técnico existente para corrigir ou atualizar informações.     | - Criar endpoint `PUT /api/admin/tecnicos/{id}`.<br>- Implementar `AdminService.updateTecnico()` com validação de e-mail duplicado.<br>- Adicionar botão "Editar" na UI `admin-tecnicos.html` que abre um modal de edição. |
| **A-2** | **Como Admin**, quero poder **desativar/reativar** uma conta de técnico para controlar o acesso ao sistema.                     | - Adicionar um campo `boolean active` na entidade `Usuario`.<br>- Criar endpoint `PATCH /api/admin/tecnicos/{id}/status`.<br>- Adicionar um switch "Ativo/Inativo" na tabela de técnicos. |
| **A-3** | **Como Admin**, quero poder **redefinir a senha** de um técnico, que irá gerar uma senha temporária e enviá-la por e-mail. | - Criar endpoint `POST /api/admin/tecnicos/{id}/reset-password`.<br>- Integrar com o `EmailService` para o envio da nova senha.<br>- Adicionar botão "Redefinir Senha" na UI. |

---

#### 👤 **Épico 2: Paridade de Funcionalidades do Cliente (Mobile & Web)**
* **Descrição:** Garantir que a experiência do cliente seja consistente e completa tanto na plataforma web quanto na aplicação mobile.

| ID    | User Story                                                                                                                    | Tarefas Técnicas Sugeridas                                                                                                                                                                                                                         |
| :---- | :---------------------------------------------------------------------------------------------------------------------------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **C-1** | **Como Cliente (Mobile)**, quero poder **enviar e visualizar anexos** no chat para partilhar imagens e documentos.              | - Implementar upload `multipart/form-data` no React Native.<br>- Adaptar a UI do `TicketDetailScreen.js` para renderizar links de download ou pré-visualizações de imagens.<br>- Garantir que o `FileStorageService` no backend lida com os uploads do mobile. |
| **C-2** | **Como Cliente (Web e Mobile)**, quero ver uma **notificação visual (badge)** nos meus chamados quando houver uma nova resposta. | - Modificar o endpoint `/api/cliente/chamados` para incluir um campo `boolean hasUnreadMessages`.<br>- Implementar lógica no frontend para exibir o "badge" com base neste campo. |
| **C-3** | **Como Cliente (Web e Mobile)**, quero poder **fechar o meu próprio chamado** se considerar que o problema foi resolvido.      | - Criar endpoint `POST /api/cliente/chamados/{id}/fechar`.<br>- Adicionar validações de negócio no `ChamadoService` para garantir que apenas o "dono" do chamado o pode fechar. |

---

### **Prioridade Média (Próximo Ciclo)**
*Funcionalidades que agregam valor significativo e melhoram a experiência geral.*

#### ⚙️ **Épico 3: Melhorias de Produtividade do Técnico (Desktop)**
* **Descrição:** Otimizar a interface desktop para melhorar a eficiência do fluxo de trabalho da equipa de suporte.

| ID    | User Story                                                                                                                            | Tarefas Técnicas Sugeridas                                                                                                                                                                                            |
| :---- | :------------------------------------------------------------------------------------------------------------------------------------ | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **T-1** | **Como Técnico**, quero **receber uma notificação em tempo real** (pop-up ou alerta sonoro) na aplicação desktop quando um novo chamado for criado. | - Criar um tópico WebSocket `/topic/chamados/novos`.<br>- O `ChamadoService`, ao criar um novo chamado, deve publicar uma mensagem neste tópico.<br>- O cliente JavaFX deve subscrever este tópico e acionar um alerta visual. |
| **T-2** | **Como Técnico**, quero poder **filtrar e ordenar a lista de chamados** por ID, status ou prioridade.                                 | - Adicionar controlos de UI (ComboBox, TextField) no `chamados-view.fxml`.<br>- Modificar o `ApiService` e o `TecnicoApiController` para aceitar parâmetros de query (ex: `/api/tecnico/chamados?status=ABERTO`). |
| **T-3** | **Como Técnico**, quero poder **atribuir um chamado a mim mesmo** para sinalizar à equipa que estou a trabalhar nele.                  | - Adicionar uma relação `ManyToOne tecnicoResponsavel` na entidade `Chamado`.<br>- Criar endpoint `PUT /api/tecnico/chamados/{id}/atribuir`.<br>- Adicionar um botão "Atribuir a mim" na UI `detalhes-chamado-view.fxml`. |

---

### **Débito Técnico & Otimizações (Contínuo)**
*Tarefas internas focadas em melhorar a qualidade do código, a performance e a manutenibilidade do sistema a longo prazo.*

| ID    | Tarefa Técnica                                                                                                                                    | Justificação                                                                                                                                                                                                       |
| :---- | :------------------------------------------------------------------------------------------------------------------------------------------------ | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **D-1** | **Implementar Testes Unitários e de Integração:** Adicionar cobertura de testes para os `Services` e `Controllers` do backend.                  | - Mitigar regressões e garantir a estabilidade do código-fonte à medida que novas funcionalidades são adicionadas.                                                                                                    |
| **D-2** | **Refatorar DTOs Duplicados:** Unificar os DTOs entre os projetos `backend` e `desktop-tecnico` num módulo Maven partilhado.                          | - O `DetalhesChamadoDto` e outros existem em ambos os projetos, violando o princípio DRY (Don't Repeat Yourself) e dificultando a manutenção. |
| **D-3** | **Implementar Paginação (Pagination):** Substituir o retorno de listas completas por respostas paginadas nos endpoints da API.                        | - Prevenir problemas de performance e timeouts à medida que o volume de dados (chamados, utilizadores) cresce. Atinge principalmente `TecnicoApiController`.                                                      |
| **D-4** | **Centralizar e Padronizar o Logging:** Substituir todos os `System.out.println` por um logger SLF4J com níveis adequados (INFO, DEBUG, ERROR). | - Ficheiros como o `DataLoader` usam `System.out`, o que é inadequado para ambientes de produção e dificulta a monitorização e a depuração.                                      |
| **D-5** | **Configurar um Error Handler Global:** Implementar um `@ControllerAdvice` no backend para gerir exceções e retornar respostas de erro padronizadas. | - Evitar páginas de erro genéricas como a "Whitelabel Error Page" e fornecer feedback claro e consistente para os clientes da API e para o frontend web. |