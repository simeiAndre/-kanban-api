# Kanban API

API REST em Java para cadastrar responsáveis, gerenciar projetos e movimentá-los em um quadro Kanban. O status e as métricas são derivados das datas para evitar divergências entre os dados armazenados e as regras de negócio.

## Tecnologias e decisões

- Java 21, Spring Boot 3.3 e Maven;
- PostgreSQL 16 e migrações versionadas com Flyway;
- Spring Data JPA, Bean Validation e auditoria automática;
- OpenAPI/Swagger para documentação interativa;
- JUnit 5, Mockito, MockMvc e Testcontainers;
- Docker Compose para iniciar API e banco com um único comando.

UUID foi adotado para permitir criação distribuída de identificadores. Projetos aceitam um ou mais responsáveis através de uma relação muitos-para-muitos. O relógio é injetável na camada de serviço, tornando os cálculos de datas determinísticos nos testes.

## Executar com Docker (recomendado)

### Pré-requisito

Instale e abra o Docker Desktop.

### Passos

1. Entre na pasta do projeto:

```bash
cd kanban-api
```

2. Crie o arquivo local de configuração:

```bash
cp .env.example .env
```

No Windows PowerShell, use:

```powershell
Copy-Item .env.example .env
```

3. Inicie a API e o PostgreSQL:

```bash
docker compose up --build
```

4. Aguarde a mensagem de inicialização e acesse:

- Saúde: http://localhost:8080/api/health
- Swagger: http://localhost:8080/swagger-ui
- OpenAPI JSON: http://localhost:8080/api-docs

Para encerrar mantendo os dados, execute `docker compose down`. Para apagar também o banco local, execute `docker compose down -v`.

## Fluxo recomendado de uso

1. Cadastre um responsável em `POST /api/responsibles`.
2. Copie o `id` retornado.
3. Cadastre um projeto em `POST /api/projects`, informando esse identificador em `responsibleIds`.
4. Liste o quadro completo em `GET /api/projects` ou uma coluna com `GET /api/projects?status=DELAYED`.
5. Movimente o cartão com `POST /api/projects/{id}/transitions/{targetStatus}`.

Status aceitos pela API: `NOT_STARTED`, `IN_PROGRESS`, `DELAYED` e `COMPLETED`.

### Exemplo de responsável

```json
{
  "name": "Ana Silva",
  "email": "ana@example.com",
  "jobTitle": "Gerente de Projetos"
}
```

### Exemplo de projeto

```json
{
  "name": "Implantação do portal",
  "responsibleIds": ["UUID_DO_RESPONSAVEL"],
  "plannedStartDate": "2026-09-25",
  "plannedEndDate": "2026-10-25",
  "actualStartDate": null,
  "actualEndDate": null
}
```

## Endpoints

| Método | Caminho | Finalidade |
|---|---|---|
| GET | `/api/health` | Verificar disponibilidade |
| POST | `/api/responsibles` | Cadastrar responsável |
| GET | `/api/responsibles` | Listar com paginação |
| GET | `/api/responsibles/{id}` | Consultar por ID |
| PUT | `/api/responsibles/{id}` | Atualizar |
| DELETE | `/api/responsibles/{id}` | Excluir |
| POST | `/api/projects` | Cadastrar e calcular métricas |
| GET | `/api/projects` | Listar, opcionalmente por status |
| GET | `/api/projects/{id}` | Consultar por ID |
| PUT | `/api/projects/{id}` | Atualizar e recalcular métricas |
| DELETE | `/api/projects/{id}` | Excluir |
| POST | `/api/projects/{id}/transitions/{status}` | Aplicar transição Kanban |

As listagens aceitam `page`, `size` e `sort`. Erros retornam horário, código HTTP, mensagem, caminho e erros por campo quando aplicável.

## Cálculos

- **Status:** recalculado pelas datas previstas e realizadas em toda criação, edição ou transição.
- **Dias de atraso:** diferença entre o término previsto e hoje quando o projeto não foi concluído.
- **Tempo restante:** `((total - dias usados) / total) * 100`, limitado entre 0% e 100% e arredondado para duas casas.
- **Concluído:** sempre apresenta zero dias de atraso e zero tempo restante.

## Executar sem Docker

Requisitos: Java 21, Maven 3.9+ e PostgreSQL 16. Configure `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD`, depois execute:

```bash
mvn spring-boot:run
```

## Testes

```bash
mvn test
```

Os testes unitários não exigem banco. O teste de integração usa um PostgreSQL temporário e é ignorado automaticamente quando o Docker não está disponível.

## Estrutura

```text
src/main/java/br/com/facilit/kanban/
├── api/          configuração geral
├── health/       verificação de saúde
├── project/      projetos, métricas e transições
├── responsible/  responsáveis
└── shared/       erros compartilhados
src/main/resources/db/migration/  evolução do banco
src/test/                         testes unitários, API e integração
```

## Histórico de marcos sugerido

1. `chore: initialize Spring Boot Kanban API`
2. `chore: add Docker development environment`
3. `feat: add responsible person management`
4. `feat: add projects and scheduling metrics`
5. `feat: implement Kanban status transitions`
6. `docs: add OpenAPI documentation and examples`
7. `docs: finalize project delivery documentation`

## Limitações e próximos passos

Os requisitos obrigatórios REST estão implementados. GraphQL, interface visual, autenticação, secretarias, indicadores avançados e observabilidade são diferenciais não incluídos nesta versão para manter o foco na qualidade do núcleo obrigatório. Em produção, também seriam recomendados autenticação/autorização, limite de requisições e monitoramento centralizado.
