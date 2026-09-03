# Rastreabilidade do MVP na arquitetura de front-end

## Como ler

Este documento agrupa os requisitos de `docs/mvp.md` por experiência e feature arquitetural. Ele não substitui a matriz de aceite nem altera a numeração original. Nas faixas numéricas, considere somente os IDs efetivamente selecionados em `docs/mvp.md`; números ausentes continuam fora do recorte. Requisitos de back-end aparecem quando condicionam contratos, estados ou segurança do front-end.

## 1. Administrador da plataforma — Web

| Feature | Requisitos principais | Componentes de apresentação |
|---|---|---|
| Autenticação | ADM-RF-001 a 004 | login, recuperação, logout, guardas `/admin` |
| Supermercados | ADM-RF-005 a 015 | lista pesquisável, detalhe, lojas, usuários, status e ações críticas |
| Planos | ADM-RF-016 a 019, 036 a 038 | plano, limites, capacidades e exceções por tenant |
| Dashboard/analytics | ADM-RF-020 a 031, 040 | KPIs, período, rankings e baixa utilização |
| Auditoria/suporte | ADM-RF-032 a 035 | timeline de alterações e contexto seguro de suporte |
| Conteúdo/operação | ADM-RF-039 | conteúdo geral conforme contrato futuro |

Qualidades dirigidas por ADM-RNF: área isolada, rastreabilidade, separação entre tenants, confirmação de operações críticas, busca simples, métricas compreensíveis e listas paginadas para grande volume.

## 2. Administração do supermercado — Web

| Feature Core | Requisitos principais | Apresentação Web |
|---|---|---|
| Authentication | SUP-RF-001 a 010 | login, recuperação, sessão, usuários/permissões e auditoria |
| Supermarkets | SUP-RF-011 a 016 | dados comerciais, logo, contato e endereço |
| Stores | SUP-RF-019 a 026, 170, 171 | lista/seletor de lojas, estado e escopo multi-loja |
| Catalog | SUP-RF-027 a 044 | categorias, produtos, upload, pesquisa e filtros |
| Campaigns/Offers | SUP-RF-045 a 060 | editor, preço/desconto, vigência, lojas, cópia e cancelamento |
| QR Codes | SUP-RF-088 a 097 | geração/download, identificação, estado, loja e métricas de origem |
| Analytics | SUP-RF-104 a 125, 175, 176 | KPIs, períodos, comparações e rankings |
| Content/Media | SUP-RF-160 a 169 | tabloide, validade, banners, mensagens, avisos e ordenação |

O editor deve usar linguagem de varejo, reduzir etapas, preservar rascunho local apenas quando seguro e exigir revisão em publicação ou ações destrutivas.

## 3. Vitrine pública — Web mobile-first

| Feature | Requisitos | Decisão de UX/arquitetura |
|---|---|---|
| Entrada anônima | SUP-RF-073 a 076 | URL direta, nenhum login/cadastro, identidade imediata |
| Ofertas | SUP-RF-077 a 087 | somente vigentes, categoria, pesquisa, preço, validade, imagem, condição e detalhe |
| QR permanente | SUP-RF-089, 090, 096, 097 | URL opaca estável e atribuição sem bloquear renderização |
| Compartilhamento | SUP-RF-138 a 141 | deep links para oferta, campanha ou loja |
| Tabloide | SUP-RF-160, 162 a 164 | visualização do conteúdo vigente e estado expirado |
| Conteúdo promocional | SUP-RF-165 a 169 | banners/avisos válidos, ordenados e acessíveis |

Qualidades SUP-RNF-001 a 014 e 032 a 035 são critérios de aceite da vitrine: poucos passos, mobile-first, preços e validade evidentes, imagens não bloqueantes, resposta rápida, mensagens claras, contraste, semântica e legibilidade.

## 4. Contratos transversais exigidos do Core

| Contrato | Requisitos que protege |
|---|---|
| Session/AuthRepository/SessionStore | ADM-RNF-001, SUP-RNF-021 e 023 |
| TenantContext e autorização por capability | ADM-RNF-011, SUP-RNF-022 e 024 |
| OfferValidity e PublicationStatus | SUP-RF-060, 164; SUP-RNF-016, 018 e 019 |
| Money/Discount | SUP-RF-047 a 049; SUP-RNF-005 |
| ApiFailure + correlation ID | SUP-RNF-010, rastreabilidade e suporte |
| Public read models separados | ADM-RNF-006, SUP-RNF-024 e 026 |
| AnalyticsEvent mínimo | SUP-RNF-020, 026 a 031 |
| Audit read model | ADM-RF-032/033, SUP-RF-010, SUP-RNF-025 |
| Capability/PlanLimit | ADM-RF-018, 036 a 038 |

## 5. Escopo Mobile

O mapa mental declara “sem instalar aplicativo” e o MVP exclui aplicativo. Portanto, nenhum requisito selecionado exige publicação Android/iOS agora. O package `mobile` existe pela arquitetura obrigatória e recebe apenas bootstrap/testes mínimos até surgir uma jornada nativa aprovada.

Não duplicar a vitrine Web no Mobile por antecipação. Quando o aplicativo entrar no roadmap, reutilizar Core e criar apresentação/navegação próprias, começando por um caso que dependa de capacidade nativa ou retenção comprovada.

## 6. Fora do MVP atual

Não orientar a arquitetura inicial por IA, ERP/PDV, fidelidade avançada, pagamentos, automação complexa, Retail Media, cupons, favoritos ou WhatsApp recorrente. Os limites atuais permitem evolução, mas abstrações só surgem quando uma dessas capacidades entrar no backlog aprovado.

## 7. Matriz de cobertura por teste de jornada

| Jornada | Cobertura mínima |
|---|---|
| QR -> ofertas vigentes -> detalhe | Web integration + contrato público + desempenho |
| Login tenant -> cadastrar produto/oferta -> publicar | Web integration + Core unit/application + API contract |
| Recuperar acesso -> nova sessão | Web integration + segurança |
| Trocar contexto de loja -> métricas individuais/consolidadas | Core application + Web widget/integration |
| Super Admin -> localizar tenant -> bloquear/reativar | Web integration + confirmação + autorização/auditoria |
| Tabloide expirado | Core vigência + Web empty/expired state |
| Falha de analytics durante acesso público | integração garantindo conteúdo disponível |
| Tentativa de acesso cruzado entre tenants | contrato/API e limpeza de estado privado no cliente |
