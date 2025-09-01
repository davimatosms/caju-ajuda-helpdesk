# Planejamento do Projeto: Caju Ajuda

* **Última Atualização:** 01 de Setembro de 2025
* **Visão Geral:** Este documento contém o Product Backlog completo e o Roadmap de Sprints estimado para o desenvolvimento da versão 1.0 do sistema Caju Ajuda.

---

## 1. Product Backlog

Este backlog documenta as funcionalidades, melhorias e débitos técnicos para o ecossistema Caju Ajuda.

### **Prioridade Alta**

#### 🚀 **Épico 1: Gestão de Contas de Administrador**
* **Descrição:** Expandir as funcionalidades do painel de administração para fornecer um ciclo de vida completo para a gestão de contas de técnicos.

| ID | User Story |
| :--- | :--- |
| **A-1** | **Como Admin**, quero poder **editar os dados** de um técnico. |
| **A-2** | **Como Admin**, quero poder **desativar/reativar** uma conta de técnico. |
| **A-3** | **Como Admin**, quero poder **redefinir a senha** de um técnico. |

#### 👤 **Épico 2: Paridade de Funcionalidades do Cliente (Mobile & Web)**
* **Descrição:** Garantir que a experiência do cliente seja consistente e completa tanto na plataforma web quanto na aplicação mobile.

| ID | User Story |
| :--- | :--- |
| **C-1** | **Como Cliente (Mobile)**, quero poder **enviar e visualizar anexos** no chat. |
| **C-2** | **Como Cliente (Web e Mobile)**, quero ver uma **notificação visual (badge)** nos meus chamados. |
| **C-3** | **Como Cliente (Web e Mobile)**, quero poder **fechar o meu próprio chamado**. |

---

### **Prioridade Média**

#### ⚙️ **Épico 3: Melhorias de Produtividade do Técnico (Desktop)**
* **Descrição:** Otimizar a interface desktop para melhorar a eficiência do fluxo de trabalho da equipa de suporte.

| ID | User Story |
| :--- | :--- |
| **T-1** | **Como Técnico**, quero **receber uma notificação em tempo real** quando um novo chamado for criado. |
| **T-2** | **Como Técnico**, quero poder **filtrar e ordenar a lista de chamados**. |
| **T-3** | **Como Técnico**, quero poder **atribuir um chamado a mim mesmo**. |
| **T-4** | **Como Técnico**, quero poder adicionar **notas internas** a um chamado que não são visíveis para o cliente. |
| **T-5** | **Como Técnico**, quero usar **respostas prontas (templates)** para resolver problemas comuns rapidamente. |

#### 📊 **Épico 4: Dashboards e Métricas**
* **Descrição:** Fornecer dados visuais para administradores e recolher feedback dos clientes.

| ID | User Story |
| :--- | :--- |
| **A-4** | **Como Admin**, quero ver um **dashboard com métricas chave** (total de chamados, tempo médio de resposta, etc.). |
| **C-4** | **Como Cliente**, quero poder **avaliar o atendimento** após a resolução de um chamado. |

---

### **Prioridade Baixa (Icebox)**
* **Descrição:** Ideias e melhorias que agregam valor, mas não são essenciais para o lançamento da V1.0.

| ID | User Story / Feature |
| :--- | :--- |
| **F-1** | **Knowledge Base (FAQ):** Permitir que clientes pesquisem artigos de ajuda antes de abrir um chamado. |
| **T-6** | **Mesclar Chamados:** Permitir que um técnico mescle dois chamados duplicados abertos pelo mesmo cliente. |
| **A-5** | **Gestão de Clientes:** Permitir que um Admin possa visualizar e desativar contas de clientes. |

---

### **Débito Técnico & Otimizações (Contínuo)**
* **Descrição:** Tarefas internas focadas em melhorar a qualidade, performance e manutenibilidade do sistema.

| ID | Tarefa Técnica |
| :--- | :--- |
| **D-1** | Implementar Testes Unitários e de Integração. |
| **D-2** | Refatorar DTOs Duplicados. |
| **D-3** | Implementar Paginação (Pagination) nas APIs. |
| **D-4** | Centralizar e Padronizar o Logging (SLF4J/Logback). |
| **D-5** | Configurar um Error Handler Global (`@ControllerAdvice`). |
| **D-6** | Criar uma interface `FileStorageService` para abstrair o armazenamento de ficheiros. |

---
---

## 2. Roadmap de Sprints (Estimativa)

* **Estimativa Total:** 6 Sprints (12 semanas)
* **Data de Início:** 02 de Setembro de 2025
* **Estimativa de Lançamento (V1.0):** Final de Novembro de 2025

---

### **Sprint 1: Fundações da Gestão e Experiência do Cliente**
* **Período:** 02 de Setembro – 16 de Setembro de 2025
* **Sprint Goal:** Concluir o ciclo de vida da gestão de técnicos no painel de administração e alcançar a paridade de funcionalidades críticas para o cliente nas plataformas web e mobile, garantindo a qualidade com testes e documentação inicial.
* **Itens do Backlog:**
    * **Épico 1 (Completo):** Gestão de Contas de Administrador (User Stories A-1, A-2, A-3).
    * **Épico 2 (Completo):** Paridade de Funcionalidades do Cliente (User Stories C-1, C-2, C-3).
    * **Débito Técnico:** Iniciar implementação de testes unitários (D-1) e configuração de logging (D-4).

---

### **Sprint 2: Produtividade da Equipe Técnica**
* **Período:** 17 de Setembro – 30 de Setembro de 2025
* **Sprint Goal:** Otimizar o fluxo de trabalho da equipe de suporte, entregando funcionalidades essenciais no cliente desktop para aumentar a eficiência na resolução de chamados.
* **Itens do Backlog:**
    * **Épico 3 (Completo):** Melhorias de Produtividade do Técnico (User Stories T-1, T-2, T-3, T-4, T-5).
    * **Épico 4 (Parcial):** Dashboards e Métricas (User Story A-4 - Dashboard do Admin).
    * **Débito Técnico:** Implementar Paginação nas APIs (D-3).

---

### **Sprint 3: Métricas e Autonomia do Cliente**
* **Período:** 01 de Outubro – 14 de Outubro de 2025
* **Sprint Goal:** Aumentar a satisfação e autonomia do cliente com a implementação de avaliação de atendimento e uma base de conhecimento, enquanto melhoramos a saúde técnica do backend.
* **Itens do Backlog:**
    * **Épico 4 (Finalização):** Dashboards e Métricas (User Story C-4 - Avaliação de Atendimento).
    * **Icebox (Priorizado):** Lançar a primeira versão da Knowledge Base (FAQ) (F-1).
    * **Débito Técnico:** Configurar um Error Handler Global (D-5).

---

### **Sprint 4: Funcionalidades Avançadas e Qualidade de Código**
* **Período:** 15 de Outubro – 28 de Outubro de 2025
* **Sprint Goal:** Finalizar as funcionalidades de gestão planejadas e realizar refatorações críticas no backend para garantir a manutenibilidade e escalabilidade do sistema.
* **Itens do Backlog:**
    * **Icebox (Priorizado):** Mesclar Chamados Duplicados (T-6).
    * **Icebox (Priorizado):** Gestão de Contas de Clientes pelo Admin (A-5).
    * **Débito Técnico:** Refatorar DTOs duplicados (D-2) e criar interface `FileStorageService` (D-6).

---

### **Sprint 5: Testes Integrados e Preparação para Produção**
* **Período:** 29 de Outubro – 11 de Novembro de 2025
* **Sprint Goal:** Garantir a estabilidade da V1.0 através de testes de ponta-a-ponta (E2E) em todas as plataformas e preparar a infraestrutura para o lançamento.
* **Itens do Backlog:**
    * **Testes:** Executar testes manuais completos nos fluxos do cliente e do técnico.
    * **Documentação:** Escrever a primeira versão do Manual do Usuário.
    * **Infra/DevOps:** Configurar o ambiente de produção na Azure (App Service, SQL Database).
    * **Bugs:** Corrigir todos os bugs críticos e de alta prioridade encontrados.

---

### **Sprint 6: Lançamento e Estabilização (Go-Live)**
* **Período:** 12 de Novembro – 25 de Novembro de 2025
* **Sprint Goal:** Lançar a versão 1.0 do Caju Ajuda, monitorar a saúde da aplicação em produção e realizar os ajustes finais.
* **Itens do Backlog:**
    * **Infra/DevOps:** Realizar o deploy da aplicação no ambiente da Azure.
    * **Monitoramento:** Acompanhar os logs e a performance da aplicação.
    * **Hotfixes:** Corrigir eventuais bugs críticos que surjam após o lançamento.
    * **Planejamento:** Realizar a retrospectiva do projeto e planejar o backlog para a V1.1.