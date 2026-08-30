# CLAUDE.md

## Visão do Projeto
`api-tabloid` é o serviço backend do módulo Tabloide do SGTM. Expõe API REST documentada via OpenAPI/Swagger. Projeto em estágio inicial: hoje só existe um endpoint de health-check; a estrutura de domínio ainda vai crescer.

## Stack
- Java 25, Spring Boot 4.1.1, `spring-boot-starter-webmvc`
- springdoc-openapi 3.1.0 (Swagger UI em `/swagger-ui.html`, OpenAPI em `/v3/api-docs`)
- Maven via wrapper (`./mvnw`) — não há Gradle
- NÃO usar Lombok automaticamente — não está no pom.xml; só adicionar se justificar necessidade real
- Ainda não há Spring Data/JPA nem banco configurado — não assuma persistência existente

## Como rodar
- Build: `./mvnw clean package`
- Run: `./mvnw spring-boot:run`
- Testes: `./mvnw test` (comando funciona, mas ainda não há testes escritos nem `spring-boot-starter-test` no pom — ao adicionar o primeiro teste, adicionar a dependência primeiro)

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
- Preferir `record` e imutabilidade quando simplificar.
- Não fazer overengineering: nada de DDD completo, arquitetura hexagonal, CQRS ou event sourcing para funcionalidade simples/CRUD.
- Ver `.claude/agents/agent-especialist-java.md` para o detalhamento completo (SOLID, nomenclatura, exceptions, etc.).

## Onde coisas novas vão
- Pacote raiz: `com.tabloide.api`.
- Organização por módulo de domínio: `modules/<dominio>/interfaces/http/` para controllers REST (ver `modules/operation/interfaces/http/HealthController.java` como exemplar).
- Ainda não existem camadas `application/`, `domain/` nem `infrastructure/` — ao introduzir regra de negócio real, criar essas camadas dentro do módulo em vez de colocar lógica no controller.

<!-- Itere este arquivo: erro que estava neste contrato → fortaleça a regra existente;
     erro que NÃO estava → adicione uma regra nova. O arquivo cresce com o uso, não de uma vez. -->
