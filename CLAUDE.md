# CLAUDE.md

## Visão do Projeto
`api-tabloid` é o serviço backend do módulo Tabloide do SGTM. Expõe API REST documentada via OpenAPI/Swagger (Swagger UI em `/swagger-ui.html`, OpenAPI em `/v3/api-docs`). Já implementa autenticação/autorização por sessão, cadastro de supermercado, plano/assinatura, ocorrências de suporte e auditoria — não é mais só um health-check.

## Stack
- Java 25, Spring Boot 4.1.1, `spring-boot-starter-webmvc`
- springdoc-openapi 3.1.0
- Spring Data JPA + PostgreSQL + Flyway (migrations em `src/main/resources/db/migration`, `ddl-auto=validate`)
- Sessão autenticada com JWT (`jjwt` 0.12.6) + `spring-security-crypto` para hash de senha
- Maven via wrapper (`./mvnw`) — não há Gradle
- NÃO usar Lombok automaticamente — não está no pom.xml; só adicionar se justificar necessidade real

## Como rodar
- Build: `./mvnw clean package`
- Run: `./mvnw spring-boot:run` (requer Postgres; ver `APP_DATASOURCE_*` em `application.properties`)
- Testes unitários: `./mvnw test` (roda `*Test.java`, sem depender de Docker)
- Testes de integração: `./mvnw verify` (roda `*IT.java` via failsafe, sobe Postgres via Testcontainers — precisa de Docker rodando)

## Regras de comportamento (não negocie)
1. **Incerteza → leia o arquivo ou rode `grep`. Nunca chute.** Se não achou (função, campo, API, arquivo), diga que não achou.
2. **Menor delta possível.** Resolva só o pedido. Oportunidade de melhoria fora do escopo → reporte separado, não execute sem pedir.
3. **Não toque em código fora do escopo sem autorização explícita.** Viu algo errado → sinalize, não conserte por conta própria.
4. **"Funciona" só com evidência.** Ao afirmar que algo funciona, mostre o output do comando/teste. Não rodou → diga "não rodei".
5. **Ações destrutivas exigem confirmação prévia.** Migração de schema, delete em massa, mudança de CI/auth → confirme antes de executar.

## Convenções
- Constructor injection sempre; nunca `@Autowired` em campo.
- Controllers ficam finos: recebem request, chamam caso de uso, devolvem response. Nada de regra de negócio, SQL ou acesso a repository direto no controller.
- Guard clauses / early return. Evitar `if` aninhado e `switch` gigante — quando houver variação real de comportamento por tipo, considerar Strategy.
- Não criar interface sem justificativa concreta (múltiplas implementações, boundary de infra, substituição em teste). Nada de `CrudService<T>` genérico.
- Preferir `record` e imutabilidade quando simplificar. DTOs de response são `record` com factory estática `from(...)`.
- Erros HTTP: um `@RestControllerAdvice` por módulo (`ErroXHandler`), não um handler global único. Exceptions de domínio são `RuntimeException` específicas, uma por caso de erro.
- Autorização é declarativa via `@RequerPerfil(Perfil...)` lido por interceptor — não espalhar `if` de perfil pelo controller.
- Toda consulta (GET) a dado privado administrativo deve ser auditada (`@AuditarConsulta` + interceptor de auditoria de leitura) — vale para qualquer consulta administrativa, não só as do fluxo de suporte/Ocorrência.
- Antes de planejar módulo administrativo novo (o que altera ou expõe dado relevante), ler `docs/arquitetura/back/*.md` no monorepo `SGTM` — define decisões estruturais (ex.: auditoria crítica gravada na mesma transação da alteração) que não estão no código nem neste arquivo.
- Não fazer overengineering: nada de DDD completo, CQRS ou event sourcing para funcionalidade simples/CRUD.
- Ver `.claude/agents/agent-especialist-java.md` para o detalhamento completo (SOLID, nomenclatura, exceptions, etc.).

## Onde coisas novas vão
- Pacote raiz: `com.tabloide.api`.
- Módulos existentes (exemplares): `auditoria`, `autenticacao`, `operation`, `plano`, `supermercado`, cada um em `modules/<dominio>/`.
- Camadas por módulo: `domain/` (entidades, interface do repository, exceptions) · `application/` (uma classe por caso de uso, verbo+substantivo, ex. `AbrirOcorrencia`) · `infrastructure/persistence/` (`XJpaEntity` + `XJpaRepository` + `XRepositoryAdapter` implementando o repository do domain) · `interfaces/http/` (controller + `ErroXHandler` + `dto/`).
- Testes em `src/test/java` espelham essa estrutura: `*Test.java` (unit) ao lado de `application`/`domain`; `*IT.java` + um `XIntegrationTestSupport` abstrato (Testcontainers) em `interfaces/http`.

<!-- Itere este arquivo: erro que estava neste contrato → fortaleça a regra existente;
     erro que NÃO estava → adicione uma regra nova. O arquivo cresce com o uso, não de uma vez. -->
