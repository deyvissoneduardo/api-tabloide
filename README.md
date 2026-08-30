# api-tabloid

> API do módulo Tabloide do SGTM — serviço backend Java/Spring Boot que expõe endpoints REST para o domínio de tabloides do sistema SGTM.

## Sobre

O `api-tabloid` é o serviço backend responsável pelo módulo Tabloide dentro do SGTM. Ele expõe uma API REST documentada via OpenAPI/Swagger, construída sobre Spring Boot.

O projeto está em estágio inicial: por enquanto expõe apenas um endpoint de health-check, servindo como base para os próximos módulos de domínio.

## Arquitetura

O código é organizado por módulo de domínio, com cada módulo separando a camada de interface HTTP da lógica de aplicação:

```
src/main/java/com/tabloide/api/
├── ApiTabloidApplication.java
└── modules/
    └── operation/
        └── interfaces/http/   # controllers REST do módulo
```

## Pré-requisitos

- Java 25
- Maven (ou o wrapper `mvnw` incluso no repositório, que não exige instalação prévia)

## Como subir

```bash
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

## Endpoints / API

Com a aplicação em execução, a documentação interativa fica disponível em:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Endpoint disponível atualmente:

| Método | Caminho | Descrição |
|--------|---------|-----------|
| `GET` | `/api/v1/health` | Verifica se a API está disponível |

## Testes

```bash
./mvnw test
```

> ⚠️ Ainda não há testes automatizados no repositório (`src/test` está vazio). O comando acima é o ponto de entrada padrão do Maven para quando a suíte for adicionada.

## Contribuindo

1. Suba o serviço localmente (`./mvnw spring-boot:run`).
2. Rode a suíte de testes (`./mvnw test`) antes de abrir um PR.
3. Abra um Pull Request descrevendo a mudança.

> ⚠️ Não há `CONTRIBUTING.md` neste repositório ainda.

## Licença

> ⚠️ Licença ainda não definida. Nenhum arquivo `LICENSE` foi encontrado neste repositório.
