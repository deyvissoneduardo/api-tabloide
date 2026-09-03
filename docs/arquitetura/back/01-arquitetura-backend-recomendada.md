# Arquitetura de back-end recomendada para o SGTM

## Resumo executivo

**Decisão recomendada para o MVP:** construir um **monólito modular**, exposto por APIs HTTP, com **um banco de dados relacional transacional**, armazenamento de objetos para imagens/PDFs e processamento assíncrono dentro do mesmo produto apenas onde a resposta ao usuário não deve esperar.

Essa é a menor arquitetura capaz de atender ao SGTM agora. Ela preserva transações entre campanhas, ofertas e lojas; reduz custo operacional; facilita mudanças em um domínio ainda em descoberta; e deixa fronteiras claras para extrair componentes no futuro, caso métricas reais justifiquem isso.

**Não adotar no MVP:** microsserviços, event sourcing, CQRS completo, broker obrigatório, banco por módulo, Kubernetes ou cache distribuído como pré-requisito.

## 1. Diagnóstico

O repositório contém requisitos, decisões de produto e histórias, mas ainda não contém código, contratos de API, modelo de dados, infraestrutura ou ADRs de implementação. Portanto, esta proposta inicia a arquitetura; não substitui uma arquitetura existente.

O produto é um SaaS B2B:

- multiempresa: cada supermercado é um tenant isolado;
- multi-loja: uma conta pode administrar várias lojas;
- com dois planos de acesso distintos: administração autenticada e leitura pública anônima;
- centrado em catálogo, campanhas, ofertas, conteúdo promocional e QR Code permanente;
- orientado a eventos de acesso para produzir analytics agregados;
- com imagens e tabloides em PDF;
- sujeito a auditoria, privacidade e rastreabilidade;
- em fase de MVP, com domínio e volume real ainda não validados.

O maior risco inicial não é escala técnica. É criar complexidade antes de confirmar que consumidores escaneiam o QR Code e que supermercados percebem valor no produto.

## 2. Problemas arquiteturais a resolver

| Problema | Necessidade arquitetural |
|---|---|
| Um supermercado não pode acessar outro | Isolamento de tenant aplicado em todas as operações privadas |
| DONO, OPERADOR e Super Admin têm poderes diferentes | Autenticação separada de autorização e políticas por ação |
| Oferta vigente depende de estado e período | Regras temporais centralizadas e testáveis |
| Uma campanha atende várias lojas | Modelo relacional e fronteiras transacionais explícitas |
| QR físico não pode expirar com a campanha | Identificador estável que resolve o conteúdo vigente no acesso |
| Página pública precisa ser rápida e disponível | Modelo de leitura enxuto, paginação, índices e mídia fora do banco |
| Scans e visualizações não devem atrasar a página | Captura desacoplada e processamento assíncrono evolutivo |
| Alterações críticas precisam de autoria | Auditoria imutável na mesma transação da alteração |
| Imagens e PDFs são grandes | Armazenamento de objetos, metadados no banco e URLs controladas |
| Produto ainda está mudando | Baixo custo de alteração e implantação simples |

## 3. Comparação das arquiteturas candidatas

Escala: 1 = inadequada; 5 = muito adequada ao MVP.

| Critério | Monólito em camadas | **Monólito modular** | Microsserviços | Serverless por função |
|---|---:|---:|---:|---:|
| Simplicidade operacional | 5 | **4** | 1 | 2 |
| Clareza de fronteiras | 2 | **5** | 5 | 3 |
| Transações do domínio | 5 | **5** | 2 | 2 |
| Velocidade de mudança | 4 | **5** | 2 | 3 |
| Escala independente | 1 | **3** | 5 | 5 |
| Observabilidade simples | 4 | **4** | 2 | 2 |
| Adequação ao MVP | 4 | **5** | 1 | 2 |
| Risco de acoplamento futuro | 2 | **4** | 4 | 2 |

### Decisão

Escolher **monólito modular**. Um monólito em camadas simples seria mais rápido nos primeiros dias, mas tende a misturar responsabilidades conforme o backlog cresce. Microsserviços e funções distribuídas aumentariam deploys, contratos, falhas parciais, consistência eventual e custo operacional sem uma necessidade comprovada.

O monólito modular significa:

- um artefato implantável para a API e, inicialmente, um processo worker do mesmo código;
- módulos com responsabilidades e dependências explícitas;
- uma única base relacional, sem acesso indiscriminado às tabelas de outro módulo;
- chamadas locais entre módulos por interfaces/casos de uso públicos;
- eventos internos somente para efeitos secundários, nunca para esconder o fluxo principal;
- possibilidade de escalar API pública e worker separadamente sem separar o domínio.

## 4. Visão de componentes

```text
Painel SGTM ───────┐
Painel Mercado ────┼──> API HTTP ──> Casos de uso ──> Módulos de domínio
Página pública ────┘         │                │                 │
                             │                │                 ├── Banco relacional
                             │                │                 ├── Armazenamento de objetos
                             │                └── Caixa de saída └── E-mail (externo)
                             │                         │
                             └── Coleta de eventos ────┴──> Worker ──> Agregados de analytics
```

No início, API e worker podem compartilhar repositório e artefato. Devem executar como processos distintos apenas quando isso melhorar confiabilidade ou escala.

## 5. Módulos e responsabilidades

| Módulo | Responsabilidade | Não deve possuir |
|---|---|---|
| Identidade e Acesso | login, sessões, recuperação de senha, usuários, perfis e políticas | regras comerciais de campanhas |
| Tenancy e Supermercados | supermercado, estado da conta, dados cadastrais e lojas | autenticação ou métricas |
| Planos e Assinaturas | planos, funcionalidades, cotas e vigência | cobrança externa no MVP |
| Catálogo | categorias, produtos e referências de mídia | preço promocional vigente |
| Campanhas e Ofertas | campanha, oferta, validade, publicação e vínculo com lojas | contagem analítica |
| Publicação | projeção/consulta do conteúdo público atual | dados privados administrativos |
| QR Codes | identificador permanente, destino lógico, estado e atribuição | conteúdo duplicado da campanha |
| Conteúdo e Mídia | banners, avisos, tabloides e metadados dos arquivos | binários dentro do banco relacional |
| Analytics | eventos, agregações, rankings e relatórios | regras transacionais de publicação |
| Auditoria | ator, ação, alvo, instante, tenant e contexto seguro | senhas, tokens ou conteúdo sensível completo |
| Operação | ocorrências e visão consolidada do Super Admin | acesso irrestrito por conveniência |

`TenantId` deve ser obrigatório nos dados pertencentes a supermercado. Entidades globais, como o Super Admin e definições globais de plano, precisam ser explicitamente identificadas como exceções.

## 6. Organização interna de cada módulo

Usar uma combinação pragmática de **vertical slices** com portas e adaptadores:

```text
modulo/
  dominio/          # regras, invariantes e objetos de negócio relevantes
  aplicacao/        # casos de uso, comandos, consultas e transações
  interfaces/       # endpoints, DTOs e consumidores de jobs/eventos
  infraestrutura/   # persistência e integrações externas
```

Não é necessário criar as quatro pastas se um módulo ainda for simples. A dependência deve apontar de interfaces e infraestrutura para aplicação/domínio, e nunca colocar regra de negócio em controller ou ORM.

Evitar `GenericRepository<T>`, serviços genéricos e uma interface para cada classe. Criar portas somente em limites que mudam ou precisam ser substituídos/testados, como e-mail, armazenamento de objetos, relógio e geração de tokens.

## 7. Dados, consistência e multi-tenancy

### Modelo recomendado

- Um banco relacional compartilhado e um schema lógico no MVP.
- Coluna `tenant_id` obrigatória nas tabelas privadas.
- Chaves e índices compostos com `tenant_id` quando a unicidade for por supermercado.
- Restrições do banco para invariantes estruturais: CNPJ e e-mail globais únicos conforme as decisões do MVP; referências válidas; valores monetários não negativos; datas coerentes.
- Filtro de tenant aplicado na camada de aplicação/persistência e, se suportado com segurança pela tecnologia escolhida, defesa adicional no banco.
- Migrações versionadas e compatíveis com rollback de aplicação.

Separar schema ou banco por tenant não se justifica para mercados de 1 a 20 lojas e elevaria o custo de migração e operação. Reavaliar apenas por obrigação regulatória, isolamento contratual ou tenants excepcionalmente grandes.

### Transações

- Publicar campanha, ofertas e vínculos de loja deve ser atômico quando formar uma única mudança de negócio.
- Auditoria crítica deve ser gravada na mesma transação da alteração.
- E-mail, geração de derivados de imagem e analytics devem ocorrer depois do commit.
- Se efeitos assíncronos não puderem ser perdidos, usar **transactional outbox** e consumidor idempotente.
- Não prometer entrega exatamente uma vez; deduplicar por `event_id`.

### Datas e dinheiro

- Armazenar instantes em UTC e converter para o fuso aplicável na borda.
- Definir explicitamente se validade é instante ou data comercial da loja.
- Armazenar dinheiro em tipo decimal, nunca ponto flutuante.
- Calcular “vigente” por estado, início e fim; um job pode atualizar projeções, mas não pode ser a única garantia de expiração.

## 8. APIs e contratos

- REST/JSON é suficiente para painéis e página pública do MVP.
- Separar rotas administrativas da plataforma, administrativas do tenant e públicas.
- Nunca inferir o tenant apenas de um identificador enviado pelo cliente; derivá-lo da identidade autenticada e validar o recurso.
- Usar paginação por cursor em eventos e listas de alto crescimento; paginação por página pode servir para cadastros pequenos.
- Filtros e ordenações devem usar listas permitidas, nunca fragmentos de consulta recebidos do cliente.
- Respostas públicas devem usar DTOs próprios e jamais serializar entidades internas.
- Padronizar erros com código estável, mensagem segura, detalhes de validação e `correlation_id`.
- Aplicar idempotência em criação/publicação suscetível a repetição e em callbacks futuros.
- Versionar contratos somente quando mudança incompatível for necessária; preferir evolução aditiva.

## 9. Fluxos críticos

### Publicação de campanha

1. Autenticar usuário e resolver tenant/permissões.
2. Validar campanha, ofertas, produtos, lojas e limites do plano.
3. Persistir a mudança e a auditoria em uma transação.
4. Registrar evento na outbox quando houver projeções ou tarefas assíncronas.
5. Invalidar/atualizar a visão pública depois do commit.
6. Tornar a publicação observável por logs e métricas.

### Acesso por QR Code

1. Receber o identificador público opaco e verificar se está ativo.
2. Resolver supermercado/loja e destino atual sem alterar o QR físico.
3. Redirecionar ou retornar a página pública vigente.
4. Registrar o evento de scan sem bloquear a leitura por falha de analytics.
5. Deduplicar apenas quando a métrica exigir; não coletar identidade pessoal por padrão.

### Página pública

1. Resolver slug/loja pública.
2. Consultar somente conteúdo publicado e vigente.
3. Retornar DTO enxuto, paginado e sem campos administrativos.
4. Servir imagens por armazenamento de objetos/CDN.
5. Registrar visualizações de forma assíncrona e tolerante a falhas.

## 10. Segurança e privacidade

- Senhas com algoritmo de hash lento e adaptativo; nunca criptografia reversível.
- Sessões revogáveis para cumprir encerramento em troca, recuperação ou desativação de usuário.
- Tokens de recuperação aleatórios, de uso único, armazenados como hash e expirados em 15 minutos.
- Autorização por política/caso de uso, incluindo DONO, OPERADOR e Super Admin; não apenas por rota.
- Rate limit mais restritivo em login, recuperação, QR e coleta de eventos.
- Identificadores públicos opacos e não enumeráveis.
- Upload por URL assinada ou endpoint controlado; validar tipo real, tamanho e nome, e considerar varredura antimalware para PDFs.
- TLS em trânsito, criptografia gerenciada em repouso, segredos fora do código e rotação de credenciais.
- Logs sem senha, token, link de recuperação, dados pessoais desnecessários ou corpo integral de requisições.
- Coleta anônima/agregada de analytics por padrão e política de retenção explícita.
- Operações críticas com confirmação, prevenção de repetição acidental e auditoria.

## 11. Desempenho e disponibilidade

Começar com índices alinhados às consultas reais:

- tenant + status em supermercados, lojas, usuários, campanhas, ofertas e QR Codes;
- loja + janela de vigência + estado para conteúdo público;
- tenant + categoria/nome normalizado para pesquisa de produtos;
- evento + tenant/loja/QR + intervalo de tempo para analytics;
- ator/alvo + data para auditoria.

Não adicionar cache distribuído inicialmente. Primeiro medir latência, throughput e consultas. Para conteúdo público, usar cache HTTP/CDN e invalidação por versão de publicação antes de introduzir um servidor de cache. O sistema deve continuar correto quando qualquer cache estiver indisponível.

Metas iniciais precisam ser definidas pelo Product Owner. Até lá, medir no mínimo p50/p95/p99, taxa de erro, throughput, saturação e tempo entre publicar e aparecer publicamente.

## 12. Observabilidade e operação

- Logs estruturados com `timestamp`, `level`, `service`, `environment`, `correlation_id`, `tenant_id` quando permitido, usuário técnico e código do evento.
- Métricas RED para endpoints e jobs: taxa, erros e duração; métricas de fila/outbox e falhas de e-mail.
- Health checks distintos para processo vivo e pronto para receber tráfego.
- Tracing distribuído não é essencial enquanto houver um único processo, mas propagar `trace_id`/`correlation_id` desde o início facilita evolução.
- Alertas sobre sintomas do usuário, não apenas CPU: página pública indisponível, erro de login anormal, atraso de publicação e backlog crescente.
- Backups automáticos, restauração testada, retenção de auditoria e runbook de incidentes.

## 13. Estratégia de testes

| Nível | Deve provar |
|---|---|
| Unidade | invariantes de validade, preço, estados, permissões e último DONO |
| Integração | queries com isolamento de tenant, constraints, transações, outbox e storage |
| API/contrato | autenticação, autorização, paginação, erros e ausência de campos privados |
| Funcional | publicar campanha, resolver QR permanente, recuperar senha e expirar oferta |
| Segurança | acesso horizontal entre tenants, força bruta, upload malicioso e enumeração |
| Carga | página pública e ingestão de eventos nos volumes definidos |

Priorizar testes de comportamento. Testes end-to-end devem cobrir poucos fluxos críticos; não duplicar toda a suíte nesse nível.

## 14. Implantação incremental

1. Criar esqueleto modular, pipeline, migrações, configuração e observabilidade básica.
2. Implementar Identidade/Acesso e Tenancy com isolamento automatizado.
3. Implementar Catálogo, Lojas, Campanhas/Ofertas e auditoria transacional.
4. Implementar Publicação e QR permanente com contrato público separado.
5. Adicionar mídia em armazenamento de objetos.
6. Capturar analytics inicialmente em tabela de eventos, processando agregações em job.
7. Introduzir outbox/worker quando houver efeitos que não possam ser perdidos ou carga que prejudique a API.
8. Adicionar cache, broker ou extração de serviço somente após medição.

## 15. Gatilhos de evolução

| Evidência observada | Evolução possível |
|---|---|
| Página pública domina CPU/latência | réplica de leitura, CDN, cache versionado ou processo de leitura separado |
| Eventos de analytics degradam o banco transacional | fila/broker e armazenamento analítico próprio |
| Worker afeta disponibilidade da API | deploy e escala independentes do worker |
| Um módulo exige cadência/equipe/escala isolada | avaliar extração daquele módulo, com contrato e ownership de dados |
| Pesquisa deixa de atender SLO com índices adequados | mecanismo de busca dedicado |
| Tenant exige isolamento contratual | schema/banco dedicado para esse perfil |

Microsserviço só deve ser criado quando houver evidência de que seu benefício supera latência, falhas parciais, contratos distribuídos, observabilidade e consistência eventual.

## 16. Riscos e trade-offs

| Decisão | Ganho | Custo/risco | Mitigação |
|---|---|---|---|
| Banco compartilhado | simplicidade e transações | vazamento entre tenants por bug | tenant obrigatório, testes e defesa no banco |
| Monólito modular | velocidade e operação simples | fronteiras podem deteriorar | testes de arquitetura e ownership de módulo |
| Consistência eventual em analytics | página rápida e resiliente | números podem atrasar | indicar atualização e medir lag |
| Sem cache distribuído | menos falhas e operação | menor teto de leitura inicial | índices, CDN e medição antes de cache |
| REST | contratos simples e difundidos | múltiplas consultas em telas complexas | endpoints orientados a casos de uso/read models |

## 17. Mini ADR

### Contexto

O SGTM é um MVP SaaS multi-tenant com regras transacionais, leitura pública e analytics, sem dados reais de escala e sem uma equipe grande definida.

### Decisão

Adotar monólito modular, banco relacional compartilhado com isolamento lógico por tenant, armazenamento de objetos e assincronia incremental.

### Motivos

Menor custo cognitivo e operacional, transações simples, rapidez de aprendizado e fronteiras suficientes para evolução.

### Alternativas consideradas

Monólito em camadas, microsserviços e funções serverless independentes.

### Consequências positivas

Deploy simples, depuração direta, consistência transacional e desenvolvimento rápido.

### Consequências negativas

Escala não é independente por módulo no início; disciplina arquitetural é necessária; analytics poderá demandar extração futura.

## Decisão final

> Para o MVP, implementar um monólito modular com APIs REST, banco relacional compartilhado e protegido por tenant, armazenamento de objetos e worker assíncrono evolutivo. Distribuir componentes somente diante de métricas e necessidades operacionais comprovadas.

