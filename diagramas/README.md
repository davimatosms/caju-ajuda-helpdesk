# Diagramas UML do Projeto: Caju Ajuda

Este diretório contém a documentação visual e os diagramas UML do sistema de helpdesk **Caju Ajuda**, projetado para a empresa de gateway de pagamentos **CajuPay**.

Os diagramas foram criados para modelar e esclarecer os requisitos, a estrutura e o comportamento do sistema, servindo como um guia para a equipe de desenvolvimento e para futuras manutenções.

## 1. Diagramas de Casos de Uso (`/Casos de uso`)

Os diagramas de casos de uso descrevem as interações funcionais entre os atores (usuários do sistema) e as principais funcionalidades que o Caju Ajuda oferece.

#### Atores Principais:
* **Cliente:** O usuário final que abre e acompanha os chamados de suporte.
* **Técnico:** O funcionário do suporte que atende, interage e resolve os chamados.
* **Administrador:** O usuário com permissões para gerenciar técnicos e configurar regras do sistema (como SLAs).
* **Sistema (IA):** Representa o serviço do Google Gemini que atua de forma autônoma para analisar os chamados.

#### Principais Casos de Uso:
Os diagramas nesta pasta detalham fluxos como:
* `[Ex: Abrir Novo Chamado]`
* `[Ex: Enviar Mensagem em Chamado]`
* `[Ex: Técnico Atender Chamado]`
* `[Ex: Analisar Chamado com IA (para definir categoria e prioridade)]`
* `[Ex: Gerar Sugestão de Resposta com IA]`

## 2. Diagrama de Classes (`/Classes`)

Este diagrama apresenta a estrutura estática do sistema, detalhando as principais classes, seus atributos, métodos e os relacionamentos entre elas. Ele serve como a planta baixa do nosso código.

As classes centrais do sistema representadas são:
* **`Chamado`**: A entidade principal, que contém informações como título, status, cliente, técnico, categoria e prioridade.
* **`Usuario`**: Superclasse para `Cliente` e `Tecnico`, contendo dados comuns como nome e email.
* **`Mensagem`**: Representa cada interação dentro de um chamado. Relaciona-se com `Chamado` e `Usuario` (autor).
* **`SlaRegra`**: Define as regras de tempo de resposta e solução baseadas na prioridade do chamado.
* **Serviços**: Classes como `ChamadoService` (regras de negócio) e `GeminiService` (comunicação com a IA).
* **DTOs**: Classes como `NovoChamadoDto` e `AnaliseChamadoDto` para transferência de dados.


## 3. Diagramas de Sequência (`/Sequencia`)

Estes diagramas mostram a ordem cronológica das interações entre objetos para realizar um caso de uso específico. Eles são excelentes para entender o fluxo de execução do código.

Esta seção detalha cenários como:

* **Fluxo de Criação e Análise de Chamado:**
    1.  O `Cliente` envia uma requisição HTTP para o `ChamadoController`.
    2.  O `ChamadoController` chama o método `criarNovoChamadoPeloCliente` no `ChamadoService`.
    3.  O `ChamadoService` cria e salva uma primeira versão do `Chamado` no banco de dados.
    4.  O `ChamadoService` chama o método `analisarChamado` no `GeminiService`.
    5.  O `GeminiService` faz uma chamada HTTP externa para a API do Google Gemini.
    6.  A API retorna um JSON com a `categoria` e `prioridade`.
    7.  O `ChamadoService` atualiza o objeto `Chamado` com esses dados e o salva novamente no banco.
    8.  A resposta final é retornada ao `Cliente`.

`[Adicione ou modifique com outros fluxos importantes que você tenha modelado, como o de 'Enviar Mensagem' ou 'Técnico Gerar Sugestão'.]`

## 4. Diagramas de Implementação (`/Implementação`)

Este diagrama (geralmente um Diagrama de Componentes ou de Implantação) ilustra a arquitetura física e a organização dos componentes de software do sistema.

O sistema Caju Ajuda é composto por:
* **Componente Backend (`caju-ajuda-backend`):** Uma aplicação Spring Boot que contém toda a lógica de negócio, APIs REST e comunicação com o banco de dados.
* **Componente Desktop (`desktop-tecnico`):** Uma aplicação cliente (JavaFX) para a equipe de suporte.
* **Componente Mobile (`mobile-cliente`):** Uma aplicação cliente (React Native, Node.js) para os clientes
* **Banco de Dados (`caju_ajuda_db`):** Um servidor MySQL que armazena todos os dados da aplicação.
* **APIs Externas:**
    * **Google Gemini API:** Para as funcionalidades de inteligência artificial.
    * **Servidor SMTP (Gmail):** Para o envio de notificações por e-mail.

---

### Como Abrir os Diagramas

Os diagramas foram criados com a ferramenta **Astah Community**. O arquivo-fonte principal é o `diagramas 2.0.asta`.

Para visualizar e editar os diagramas, é necessário instalar o Astah.
* **Download:** [https://astah.net/downloads/](https://astah.net/downloads/)