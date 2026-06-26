# PetShop Manager

Sistema completo de gerenciamento de pet shop desenvolvido com Java Spring Boot, Vue 3 + Vuetify e PostgreSQL.

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Backend | Java 17 + Spring Boot 3.2 |
| Persistência | Spring Data JPA + PostgreSQL |
| Relatórios | iText PDF 7 |
| Frontend | Vue 3 + Vuetify 3 + Pinia |
| Build Frontend | Vite 5 |

## Estrutura do Projeto

```
poo/
├── backend/          # Spring Boot API REST
│   └── src/main/java/com/petshop/
│       ├── model/         # Entidades JPA
│       ├── repository/    # Interfaces JPA Repository
│       ├── service/       # Lógica de negócio
│       ├── controller/    # Controllers REST (MVC)
│       ├── dto/           # Data Transfer Objects
│       ├── report/        # Geração de PDF com iText
│       └── config/        # CORS e configurações
└── frontend/         # Vue 3 + Vuetify
    └── src/
        ├── views/         # Telas da aplicação
        ├── api/           # Chamadas Axios ao backend
        ├── router/        # Vue Router
        └── plugins/       # Configuração Vuetify
```

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Node.js 18+
- PostgreSQL 14+

## Como Rodar

### 1. Banco de Dados

```sql
-- No psql ou pgAdmin, execute:
CREATE DATABASE petshop_db;
```

Ajuste as credenciais em `backend/src/main/resources/application.properties`:
```properties
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

O servidor sobe em `http://localhost:8080`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

A aplicação abre em `http://localhost:5173`.

## Endpoints REST

| Método | URL | Descrição |
|---|---|---|
| GET/POST | `/api/proprietarios` | Listar/Criar proprietários |
| GET/PUT/DELETE | `/api/proprietarios/{id}` | Operações por ID |
| GET/POST | `/api/animais` | Listar/Criar animais |
| POST | `/api/animais/{id}/foto` | Upload de foto |
| GET/POST | `/api/servicos` | Listar/Criar serviços |
| GET/POST | `/api/lancamentos` | Listar/Criar lançamentos |
| GET | `/api/lancamentos/animal/{id}` | Histórico por animal |
| GET | `/api/lancamentos/proprietario/{id}` | Histórico por proprietário |
| GET | `/api/relatorios/cliente/{id}` | Relatório PDF do cliente |

## Funcionalidades

- **Cadastro de Proprietários**: nome, endereço, telefone, email
- **Cadastro de Animais**: nome, espécie, raça, idade, sexo, peso, foto
- **Cadastro de Serviços**: nome, descrição, preço
- **Lançamento de Serviços**: data, serviço, valor, observações
- **Histórico de Serviços**: filtro por animal, proprietário, período e tipo
- **Relatório PDF**: dados do cliente + animais + serviços + totais por serviço
- **Dashboard**: visão geral com estatísticas em tempo real
