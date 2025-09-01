# Documentação da API - Caju Ajuda

A API para os clientes (Desktop e Mobile) está construída sob o prefixo `/api`. A autenticação para rotas protegidas deve ser feita enviando um Token JWT no cabeçalho `Authorization`.

**Formato do Header de Autorização:**
`Authorization: Bearer <seu_token_jwt>`

---

## Autenticação

### `POST /api/auth/login`
Autentica um usuário (atualmente, apenas do tipo `TECNICO`) e retorna um Token JWT para ser usado em requisições subsequentes.

* **Autorização:** `Público`
* **Corpo da Requisição (Request Body):** `application/json`
    ```json
    {
      "email": "tecnico@cajuajuda.com",
      "senha": "senha123"
    }
    ```
* **Resposta de Sucesso (200 OK):**
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZWNuaWNvQGNhanVhanVkYS5jb20iLCJpYXQiOjE3MjUyODc4ODIsImV4cCI6MTcyNTM3NDI4Mn0.abcdefg12345..."
    }
    ```
* **Resposta de Erro (401 Unauthorized):**
    ```json
    {
      "status": 401,
      "error": "Unauthorized",
      "message": "Credenciais inválidas",
      "path": "/api/auth/login"
    }
    ```

---

## Chamados (Técnico)

### `GET /api/tecnico/chamados`
Retorna uma lista de todos os chamados abertos no sistema.

* **Autorização:** `TECNICO` (Requer Bearer Token)
* **Parâmetros de Query (Query Parameters):**
    * `status` (opcional): Filtra os chamados por status. Ex: `?status=ABERTO`.
    * `prioridade` (opcional): Filtra por prioridade. Ex: `?prioridade=ALTA`.
    * *Futuro: Será implementada paginação (`?page=0&size=20`)*.
* **Resposta de Sucesso (200 OK):**
    ```json
    [
      {
        "id": 1,
        "titulo": "Impressora não funciona na contabilidade",
        "clienteNome": "Ana Silva",
        "dataCriacao": "2025-08-30T14:30:00",
        "status": "ABERTO",
        "prioridade": "ALTA"
      },
      {
        "id": 2,
        "titulo": "Solicitação de mouse novo",
        "clienteNome": "Carlos Pereira",
        "dataCriacao": "2025-08-29T10:15:00",
        "status": "EM_ANDAMENTO",
        "prioridade": "BAIXA"
      }
    ]
    ```
* **Resposta de Erro (403 Forbidden):** (Caso um não-técnico tente acessar)
    ```json
    {
        "status": 403,
        "error": "Forbidden",
        "message": "Acesso negado",
        "path": "/api/tecnico/chamados"
    }
    ```