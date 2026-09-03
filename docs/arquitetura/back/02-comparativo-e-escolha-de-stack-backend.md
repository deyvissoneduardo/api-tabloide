# Comparativo e escolha da stack de back-end do SGTM

## Como usar este documento

Este documento transforma a arquitetura definida em [01-arquitetura-backend-recomendada.md](./01-arquitetura-backend-recomendada.md) em opções tecnológicas concretas. A escolha final deve considerar a experiência real da equipe; tecnologia não corrige fronteiras ruins nem isolamento de tenant incompleto.

## Resposta curta

### Recomendação principal

**TypeScript + NestJS + PostgreSQL**, com ORM/mapeador que permita transações, constraints, consultas explícitas e migrações; armazenamento compatível com S3; OpenAPI; contêineres; OpenTelemetry; testes unitários e de integração com dependências reais efêmeras.

Escolha essa opção quando a equipe já trabalha com TypeScript/JavaScript ou quando uma equipe pequena cuidará de front-end e back-end. É a melhor relação entre velocidade do MVP, estrutura modular, disponibilidade de profissionais e custo operacional para este projeto.

### Alternativa conservadora para back-end corporativo

**Java + Spring Boot + PostgreSQL**. Escolha quando a equipe domina Java/Spring, quando robustez corporativa e convenções maduras pesam mais que a velocidade de uma stack compartilhada com o front-end, ou quando integrações corporativas se anteciparem no roadmap.

### Terceira opção válida

**C# + ASP.NET Core + PostgreSQL**. Escolha quando o time domina .NET ou o ambiente do cliente é fortemente Microsoft. Tecnicamente é equivalente para o escopo; sem essa vantagem organizacional, não supera objetivamente as duas opções acima.

**Go não é a recomendação inicial:** é excelente para serviços simples, concorrentes e eficientes, mas o SGTM tem muita regra CRUD/transacional, autorização e validação. Seu benefício de eficiência não compensa automaticamente a menor velocidade de modelagem para uma equipe sem experiência forte em Go.

## 1. Premissas da decisão

- Produto em MVP e domínio ainda evoluindo.
- Equipe presumida pequena; a composição real ainda não está documentada.
- Prioridade em entrega, clareza, segurança multi-tenant e testabilidade.
- Página pública predominantemente de leitura.
- Analytics inicialmente básico e agregável.
- Sem necessidade comprovada de microsserviços, Kubernetes ou banco analítico.
- Necessidade de contratar/manter desenvolvedores no mercado brasileiro.
- Preferência por tecnologias abertas e portáveis entre provedores.

Se a equipe já tiver domínio profundo de uma das três stacks principais, esse fator deve prevalecer sobre diferenças pequenas da matriz.

## 2. Critérios e pesos

| Critério | Peso | Por que importa no SGTM |
|---|---:|---|
| Velocidade e produtividade no MVP | 25% | hipótese de negócio ainda precisa ser validada |
| Adequação ao monólito modular | 15% | fronteiras precisam continuar claras com o backlog grande |
| Segurança e ecossistema web | 15% | autenticação, RBAC, uploads e multi-tenancy são críticos |
| Modelagem relacional/transacional | 15% | campanhas, ofertas e lojas exigem integridade |
| Testabilidade e observabilidade | 10% | auditoria e diagnóstico são requisitos desde o início |
| Disponibilidade de profissionais | 10% | reduz risco de continuidade |
| Eficiência operacional | 5% | custo importa, mas sem otimização prematura |
| Curva de aprendizagem para time full-stack | 5% | relevante se o time compartilhar linguagem com o front-end |

Notas: 1 = fraco; 3 = adequado; 5 = excelente. O resultado é uma orientação, não uma medição universal.

## 3. Matriz comparativa

| Opção | Produtividade | Modularidade | Segurança | Dados/transações | Testes/observ. | Profissionais | Eficiência | Full-stack | Nota ponderada |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| **TypeScript + NestJS** | 5 | 5 | 4 | 4 | 4 | 5 | 3 | 5 | **4,50** |
| **Java + Spring Boot** | 4 | 5 | 5 | 5 | 5 | 5 | 3 | 2 | **4,45** |
| **C# + ASP.NET Core** | 4 | 5 | 5 | 5 | 5 | 4 | 4 | 2 | **4,35** |
| Go + framework leve | 3 | 4 | 4 | 4 | 4 | 3 | 5 | 2 | **3,65** |
| Python + FastAPI/Django | 5 | 3 | 4 | 4 | 4 | 4 | 3 | 2 | **3,95** |

As diferenças entre as três primeiras são pequenas. A recomendação de TypeScript assume equipe pequena/full-stack. Com uma equipe especializada em back-end corporativo, Java passa a ser a primeira escolha. Com domínio prévio de .NET, C# pode obter a maior nota prática.

## 4. Comparação qualitativa

### Opção A — TypeScript + NestJS

**Resolve bem:** APIs administrativas e públicas, módulos explícitos, validação, OpenAPI, jobs, autenticação e compartilhamento de linguagem/tipos com o ecossistema front-end.

**Benefícios:**

- alta velocidade para uma equipe pequena;
- estrutura opinativa compatível com módulos e injeção de dependência;
- bom ecossistema para HTTP, filas, storage e observabilidade;
- contratação ampla e menor troca de contexto em um time full-stack;
- desempenho suficiente para o MVP quando operações de CPU são delegadas.

**Custos e riscos:**

- facilidade de criar módulos apenas nominais e acoplados;
- uso indiscriminado de decorators/magia pode esconder fluxos;
- tipagem estática não elimina validação em runtime;
- processamento pesado de imagens/PDF não deve bloquear o event loop;
- escolha de ORM pode incentivar modelo anêmico ou consultas ineficientes.

**Condição de escolha:** equipe confortável com TypeScript e revisão arquitetural disciplinada.

### Opção B — Java + Spring Boot

**Resolve bem:** domínio transacional, segurança, jobs, integrações futuras e aplicações corporativas de longa vida.

**Benefícios:**

- ecossistema muito maduro para segurança, persistência, migrações e observabilidade;
- convenções fortes e excelente suporte a testes;
- bom equilíbrio entre desempenho e produtividade;
- grande disponibilidade de profissionais de back-end;
- evolução natural para integrações ERP/PDV futuras.

**Custos e riscos:**

- maior cerimônia e consumo de recursos em comparação a opções leves;
- equipe sem experiência pode criar camadas e abstrações excessivas;
- menor reaproveitamento de conhecimento com front-end;
- tempo inicial pode ser maior em um MVP muito pequeno.

**Condição de escolha:** equipe Java experiente ou expectativa concreta de integrações corporativas complexas.

### Opção C — C# + ASP.NET Core

**Resolve bem:** APIs de alto desempenho, domínio transacional, autenticação/autorização e operação corporativa.

**Benefícios:**

- framework integrado, rápido e testável;
- excelente linguagem e ferramentas;
- segurança, observabilidade e persistência maduras;
- boa adequação a módulos e vertical slices.

**Custos e riscos:**

- vantagem diminui quando a equipe não conhece .NET;
- contratação pode variar conforme a região e o perfil da empresa;
- existe risco de acoplamento desnecessário ao ecossistema de um fornecedor, embora a plataforma seja multiplataforma.

**Condição de escolha:** experiência prévia do time ou contexto Microsoft relevante.

### Opção D — Go

**Benefícios:** binários simples, baixo consumo, concorrência e desempenho previsíveis.

**Custos para este produto:** mais código repetitivo para validação/mapeamento, ecossistema menos opinativo para um domínio administrativo grande e menor ganho enquanto o gargalo for banco/rede. Recomendada futuramente para um coletor de eventos de altíssimo volume, não como escolha automática do monólito.

### Opção E — Python

**Benefícios:** prototipação rápida, legibilidade e ecossistema amplo.

**Custos para este produto:** disciplina adicional para contratos e modularidade, menor desempenho bruto e risco de inconsistência se tipagem/validação não forem rigorosas. Django é válido se o admin pronto for vantagem determinante; FastAPI é válido com uma equipe Python experiente.

## 5. Stack completa recomendada

| Capacidade | Escolha para o MVP | Motivo | Quando revisar |
|---|---|---|---|
| Linguagem/framework | **TypeScript + NestJS** | produtividade e módulos explícitos | equipe ou benchmark indicar inadequação |
| API | REST/JSON + OpenAPI | simples, interoperável e documentável | necessidade real de outro protocolo |
| Banco transacional | **PostgreSQL** | integridade, transações, índices e consultas analíticas iniciais | volume analítico prejudicar OLTP |
| Acesso a dados | ORM/mapeador com migrações e SQL explícito disponível | produtividade sem perder controle | queries críticas ficarem opacas |
| Migrações | ferramenta versionada no repositório | mudanças repetíveis e auditáveis | nunca substituir por sync automático em produção |
| Arquivos | storage compatível com S3 | binários fora do banco e portabilidade | requisitos específicos de mídia |
| Entrega de mídia | CDN/HTTP cache | página pública rápida | desde que tráfego justifique/custo permita |
| Processamento assíncrono | tabela de jobs/outbox + worker | confiabilidade com pouca infraestrutura | backlog/throughput exigir broker |
| Cache | nenhum servidor dedicado inicialmente | evitar invalidação e operação prematuras | p95/SLO comprovarem necessidade |
| Busca | PostgreSQL com índices adequados | catálogo inicial não justifica motor separado | relevância/volume não atender SLO |
| Analytics | eventos + agregações no PostgreSQL | menor custo e consistência controlável | ingestão/consultas afetarem transações |
| Autenticação | sessões/tokens revogáveis próprios ou provedor OIDC | requisitos exigem revogação e papéis | decisão build vs buy por custo/risco |
| E-mail | adaptador para provedor transacional | recuperação desacoplada de fornecedor | entregabilidade/custo |
| Observabilidade | OpenTelemetry + logs JSON + métricas | padrão portável | backend de telemetria pode variar |
| Empacotamento | contêiner OCI | execução reproduzível e portável | plataforma escolhida dispensar gestão direta |
| CI | lint, typecheck, testes, migration check e scan | qualidade e segurança mínimas | ampliar conforme riscos |

Não fixar versões neste documento. Versões suportadas devem ficar em manifesto, lockfile e política de atualização do projeto.

## 6. Decisões específicas de implementação

### ORM/mapeador

A escolha deve ser feita por uma prova curta usando os fluxos mais difíceis, não por popularidade. O candidato precisa demonstrar:

- transação explícita incluindo auditoria/outbox;
- constraints e índices compostos com `tenant_id`;
- migrações revisáveis;
- lock otimista ou pessimista quando necessário;
- paginação eficiente;
- query para ofertas vigentes por loja/categoria;
- agregação de eventos por intervalo;
- escape para SQL parametrizado sem contornos perigosos.

Rejeitar ferramentas que dependam de sincronização automática de schema em produção ou escondam consultas críticas sem diagnóstico.

### Autenticação

Há duas escolhas válidas:

| Escolha | Quando usar | Custo principal |
|---|---|---|
| Provedor OIDC gerenciado/autohospedado | equipe pequena quer reduzir risco de autenticação | custo, integração e dependência operacional |
| Implementação no monólito | regras simples e equipe domina segurança | responsabilidade por sessões, recuperação e hardening |

Em ambas, a autorização por tenant e por ação pertence ao SGTM. O provedor não substitui a validação de ownership.

### Fila/broker

Não instalar broker apenas para “ser orientado a eventos”. Começar com outbox/tabela de jobs e polling seguro. Introduzir broker quando houver throughput, múltiplos consumidores, isolamento ou latência que a solução simples não cumpra. A partir daí, exigir idempotência, retry com backoff, DLQ e métricas de lag.

## 7. Estrutura inicial sugerida para TypeScript/NestJS

```text
src/
  modules/
    identity-access/
    tenants-stores/
    plans-subscriptions/
    catalog/
    campaigns-offers/
    publication/
    qr-codes/
    content-media/
    analytics/
    audit/
  shared/
    kernel/           # poucos tipos realmente transversais
    infrastructure/   # configuração e adaptadores compartilhados inevitáveis
  bootstrap/
    api/
    worker/
```

Cada módulo publica sua API interna. É proibido importar diretamente repositórios ou entidades de persistência de outro módulo. `shared` não deve virar depósito de regras de negócio.

## 8. Ambientes e topologia mínima

```text
Internet
   │
CDN / proteção HTTP
   │
API (1+ réplicas) ───── PostgreSQL gerenciado
   │                         │
   ├── Storage de objetos    └── backup + restauração testada
   ├── Provedor de e-mail
   └── Worker (pode iniciar junto e separar depois)
```

Preferir serviços gerenciados para banco, storage e e-mail quando o orçamento permitir. Isso reduz trabalho operacional, mas a aplicação deve depender de capacidades/contratos, não de APIs proprietárias espalhadas pelo domínio.

## 9. Checklist de escolha para o time

Marque a primeira condição verdadeira:

1. **O time domina claramente uma stack candidata?** Use-a se atender segurança, transações, testes e observabilidade. Experiência real vale mais que até 0,5 ponto na matriz.
2. **O time é pequeno e majoritariamente TypeScript/full-stack?** Escolha TypeScript + NestJS.
3. **O time é especializado em Java/back-end corporativo?** Escolha Java + Spring Boot.
4. **O time domina .NET ou opera em ecossistema Microsoft?** Escolha C# + ASP.NET Core.
5. **Não há equipe definida?** Adote provisoriamente a recomendação principal e valide com uma prova técnica antes de iniciar o backlog.

## 10. Prova técnica obrigatória antes da decisão irreversível

Implementar em no máximo poucos dias uma fatia vertical descartável:

1. autenticar DONO;
2. criar produto e oferta vinculados a tenant e loja;
3. publicar campanha em transação com auditoria;
4. consultar página pública sem campos privados;
5. resolver QR permanente e registrar scan assincronamente;
6. executar testes de tentativa de acesso entre tenants;
7. medir consulta da página pública com massa representativa;
8. executar migração e rollback compatível.

Comparar tempo de implementação, clareza, número de contornos, qualidade das queries, diagnóstico e experiência do time. Se a recomendação falhar nessa prova, escolher a alternativa que a complete com menor risco.

## 11. O que não entra na stack inicial

- Kubernetes ou service mesh;
- microsserviços;
- Kafka/RabbitMQ como requisito de partida;
- Redis sem gargalo medido;
- Elasticsearch/OpenSearch para o catálogo inicial;
- banco NoSQL como fonte principal de campanhas/ofertas;
- data warehouse no MVP;
- GraphQL apenas para evitar desenhar endpoints;
- event sourcing;
- CQRS com infraestruturas separadas;
- funções serverless para cada caso de uso.

Essas tecnologias não estão proibidas. Elas apenas precisam de um problema mensurável e uma ADR antes de entrar.

## 12. Gatilhos para reavaliar a stack

- p95 da página pública não atende ao SLO após índices, paginação e cache HTTP;
- analytics compete por recursos com transações;
- processamento de mídia exige CPU/memória isolada;
- integrações ERP/PDV exigem conectores com ciclos independentes;
- custo de execução torna-se material e foi medido;
- contratação/manutenção da stack escolhida torna-se um risco;
- requisitos regulatórios ou contratuais exigem outro isolamento;
- uma limitação concreta do runtime/framework impede um requisito.

## 13. Mini ADR

### Contexto

O SGTM precisa transformar um backlog amplo em um MVP seguro e evolutivo, com equipe e carga ainda não documentadas.

### Decisão

Usar TypeScript + NestJS + PostgreSQL como stack padrão, condicionada à competência da equipe e à prova técnica. Manter Java + Spring Boot como alternativa preferida para uma equipe Java/corporativa e C# + ASP.NET Core para uma equipe .NET.

### Motivos

Maior produtividade esperada para equipe pequena, estrutura suficiente para o monólito modular, ecossistema web maduro e capacidade adequada ao volume inicial.

### Alternativas consideradas

Java/Spring Boot, C#/ASP.NET Core, Go e Python.

### Consequências positivas

Entrega rápida, menor troca de contexto, contratação ampla e infraestrutura portátil.

### Consequências negativas

Exige disciplina para não misturar módulos, validação rigorosa em runtime e delegação de tarefas de CPU ao worker.

## Decisão final

> Na ausência de uma vantagem comprovada da equipe em outra tecnologia, escolher **TypeScript + NestJS + PostgreSQL**. A arquitetura continua sendo monólito modular; trocar linguagem não deve alterar suas fronteiras, regras de tenant, estratégia de dados ou critérios de evolução.

