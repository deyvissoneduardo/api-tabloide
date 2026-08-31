# api-tabloid

> API do módulo Tabloide do SGTM — serviço backend Java/Spring Boot que expõe endpoints REST para o domínio de tabloides do sistema SGTM.

## Sobre

O `api-tabloid` é o serviço backend responsável pelo módulo Tabloide dentro do SGTM. Ele expõe uma API REST documentada via OpenAPI/Swagger, construída sobre Spring Boot.

Hoje o serviço já cobre autenticação/autorização por sessão, cadastro e ciclo de vida de supermercado, plano/assinatura, ocorrências de suporte e auditoria de leitura e escrita de dado privado.

## Arquitetura

O código é organizado por módulo de domínio. Cada módulo separa interface HTTP, casos de uso, regra de domínio e persistência — por exemplo, o módulo `supermercado`:

```
src/main/java/com/tabloide/api/
├── ApiTabloidApplication.java
└── modules/
    └── supermercado/
        ├── domain/                    # entidades, contrato do repository, exceptions
        ├── application/               # um caso de uso por classe (CadastrarSupermercado, ...)
        ├── infrastructure/persistence/ # entidade JPA + repository adapter
        └── interfaces/http/           # controller REST + tratamento de erro + dto/
```

Os demais módulos (`auditoria`, `autenticacao`, `operation`, `plano`) seguem o mesmo padrão de camadas.

## Pré-requisitos

- Java 25
- Maven (ou o wrapper `mvnw` incluso no repositório, que não exige instalação prévia)
- Docker (para subir o Postgres local via `docker-compose.yml` e para os testes de integração, que usam Testcontainers)

## Como subir

```bash
docker compose up -d   # sobe o Postgres local (porta 5432)
./mvnw spring-boot:run
```

A API sobe por padrão em `http://localhost:8080`.

## Configuração

Propriedades definidas em `src/main/resources/application.properties`:

| Propriedade | Obrigatória | Default | Descrição |
|-------------|-------------|---------|-----------|
| `spring.application.name` | Não | `api-tabloid` | Nome da aplicação Spring Boot |
| `springdoc.api-docs.path` | Não | `/v3/api-docs` | Caminho do documento OpenAPI gerado |
| `springdoc.swagger-ui.path` | Não | `/swagger-ui.html` | Caminho da UI do Swagger |
| `APP_DATASOURCE_URL` | Não | `jdbc:postgresql://localhost:5432/api_tabloid` | URL JDBC do Postgres |
| `APP_DATASOURCE_USERNAME` | Não | `api_tabloid` | Usuário do banco |
| `APP_DATASOURCE_PASSWORD` | Não | `api_tabloid` | Senha do banco |
| `APP_AUTH_JWT_SECRET` | **Sim em produção** | segredo de dev (não usar fora de local) | Chave usada para assinar o token de sessão |
| `app.auth.sessao.duracao-maxima` | Não | `PT8H` | Duração máxima de uma sessão autenticada |
| `app.auth.sessao.tempo-inatividade` | Não | `PT30M` | Tempo de inatividade até expirar a sessão |
| `app.auth.login.max-tentativas` | Não | `5` | Tentativas de login antes do bloqueio temporário |
| `app.auth.login.bloqueio-temporario` | Não | `PT15M` | Duração do bloqueio após exceder as tentativas |
| `app.auth.redefinicao-senha.validade` | Não | `PT15M` | Validade do token de redefinição de senha |

Migrations do banco ficam em `src/main/resources/db/migration` (Flyway).

## Endpoints / API

Com a aplicação em execução, a documentação interativa fica disponível em:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Grupos de recursos disponíveis hoje (ver Swagger para o detalhe de cada rota):

| Base | Descrição |
|------|-----------|
| `GET /api/v1/health` | Verifica se a API está disponível |
| `/api/sessoes` | Login e gestão de sessão autenticada |
| `/api/redefinicoes-senha` | Fluxo de redefinição de senha |
| `/api/supermercados` | Cadastro e ciclo de vida do supermercado |
| `/api/supermercados/{supermercadoId}/usuarios` | Usuários de um supermercado |
| `/api/supermercados/{supermercadoId}/plano` | Plano/assinatura vigente do supermercado |
| `/api/ocorrencias` | Ocorrências de suporte |
| `/api/auditoria` | Consulta a registros de auditoria |

## Testes

```bash
./mvnw test     # testes unitários (*Test.java) — não precisa de Docker
./mvnw verify   # + testes de integração (*IT.java) — sobe Postgres via Testcontainers, precisa de Docker rodando
```

## Contribuindo

1. Suba o serviço localmente (`docker compose up -d` + `./mvnw spring-boot:run`).
2. Rode a suíte de testes (`./mvnw test` e, se mexeu em algo que toca HTTP/persistência, `./mvnw verify`) antes de abrir um PR.
3. Abra um Pull Request descrevendo a mudança.

> ⚠️ Não há `CONTRIBUTING.md` neste repositório ainda.

## Licença

> ⚠️ Licença ainda não definida. Nenhum arquivo `LICENSE` foi encontrado neste repositório.
