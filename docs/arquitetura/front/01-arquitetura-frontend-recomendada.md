# Arquitetura de front-end recomendada para o SGTM

## Resumo executivo

O SGTM deve iniciar como um workspace Flutter com exatamente três packages principais:

```text
frontend/
  packages/
    core/
    mobile/
    web/
```

`core` é uma biblioteca Flutter compartilhada; `web` e `mobile` são aplicações independentes e seus únicos composition roots. A dependência permitida é:

```text
mobile ──> core <── web
```

No MVP, `web` entrega a página pública mobile-first, o painel do supermercado e o painel do administrador da plataforma. O aplicativo instalável não faz parte do MVP; `mobile` deve existir como package principal e poderá receber a experiência nativa quando houver hipótese validada, sem obrigar o produto a manter uma aplicação vazia publicada.

A organização é por feature, com camadas criadas apenas quando têm responsabilidade real. Domínio, casos de uso, DTOs, repositórios, estado de dados e componentes genuinamente equivalentes ficam em `core`. Rotas, shells, telas, composição de dependências e integrações de plataforma ficam em `web` ou `mobile`.

## 1. Diagnóstico e forças arquiteturais

O repositório ainda não contém implementação Flutter ou contrato OpenAPI. A arquitetura começa a partir de quatro forças:

- o consumidor chega pelo QR Code e precisa ver ofertas sem login, cadastro ou instalação;
- supermercados operam catálogo, lojas, ofertas, QR Codes, conteúdo e métricas em área autenticada;
- o administrador da plataforma opera tenants, planos, suporte, auditoria e métricas consolidadas;
- o produto é multi-tenant, multi-loja e sujeito a privacidade, autorização e rastreabilidade.

O maior risco de experiência é o tempo entre o scan e a primeira oferta útil. O maior risco estrutural é misturar os três contextos de acesso em telas, rotas e estados globais indistintos.

## 2. Contextos de experiência

| Experiência | Usuário | Autenticação | Entrega no MVP | Prioridade de layout |
|---|---|---|---|---|
| Vitrine pública | consumidor | anônima | Web | celular e rede instável |
| Operação do supermercado | DONO/OPERADOR | obrigatória | Web | desktop/tablet responsivo |
| Operação da plataforma | Super Admin | obrigatória e isolada | Web | desktop |
| Aplicativo nativo | futuro consumidor/operador | conforme caso futuro | Mobile, após validação | Android/iOS |

Os painéis podem compartilhar primitives e casos de uso, mas devem possuir shells, árvores de rota e guardas próprios. A vitrine pública não carrega módulos administrativos, tokens de sessão ou dados privados.

## 3. Princípios e limites

1. Classificar toda responsabilidade como `CORE`, `WEB` ou `MOBILE` antes de implementar.
2. Compartilhar comportamento equivalente, não apenas código parecido.
3. Manter regras de negócio fora de widgets e chamadas HTTP fora de telas.
4. Fazer `core` independente de navegador, Android, iOS e rotas.
5. Compor dependências somente nos entry points das aplicações.
6. Expor APIs públicas estreitas; é proibido importar `package:core/src/...`.
7. Modelar explicitamente carregando, sucesso, vazio, erro recuperável e acesso negado.
8. Fazer segurança e isolamento no servidor; a UI apenas aplica defesa em profundidade e boa experiência.
9. Medir antes de introduzir cache complexo, offline-first ou novos packages.

## 4. Visão de componentes

```text
                    API REST / OpenAPI
                           │
                adapters e REST client
                           │
┌──────────────────────── CORE ────────────────────────┐
│ domínio | casos de uso | repositories | data state  │
│ auth contracts | erros | DTOs | design system       │
└───────────────────────┬──────────────────────────────┘
                        │
           ┌────────────┴────────────┐
           │                         │
┌──────── WEB ────────┐    ┌────── MOBILE ──────┐
│ rotas e guards       │    │ rotas e deep links │
│ vitrine pública      │    │ telas nativas      │
│ painéis e shells     │    │ plugins Android/iOS│
│ browser/storage      │    │ secure storage     │
│ composition root     │    │ composition root   │
└──────────────────────┘    └────────────────────┘
```

## 5. Estrutura física alvo

```text
frontend/
  README.md
  pubspec.yaml
  pubspec.lock
  melos.yaml
  analysis_options.yaml
  packages/
    core/
      pubspec.yaml
      lib/
        core.dart
        src/
          config/
          design_system/
            tokens/
            theme/
            components/
          network/
            rest_client/
            interceptors/
            errors/
          auth/
          errors/
          shared/
          features/
            authentication/
            supermarkets/
            stores/
            plans/
            catalog/
            campaigns_offers/
            publication/
            qr_codes/
            content_media/
            analytics/
            audit/
      test/
    web/
      pubspec.yaml
      lib/
        main.dart
        src/
          app/
          composition_root/
          navigation/
          platform/
          shared/
          features/
            public_offers/
            supermarket_admin/
            platform_admin/
      test/
      integration_test/
      web/
    mobile/
      pubspec.yaml
      lib/
        main.dart
        src/
          app/
          composition_root/
          navigation/
          platform/
          shared/
          features/
      test/
      integration_test/
      android/
      ios/
```

Todos os packages seguem `app/shared/features` conceitualmente, sem criar diretórios vazios ou replicar camadas sem uso. O workspace raiz coordena bootstrap, análise, testes e builds; não se cria um quarto package no MVP.

## 6. Estrutura interna de uma feature

Use a menor estrutura que preserve a dependência correta:

```text
feature/
  domain/          # entidades, value objects e contratos relevantes
  application/     # casos de uso e estados de dados
  infrastructure/  # API, DTOs e implementação de repository
```

Em `web` e `mobile`, a mesma feature normalmente contém:

```text
feature/
  presentation/
    pages/
    controllers/
    widgets/
```

Uma consulta simples pode ter apenas repository + controller + page. As camadas completas são justificadas quando existem invariantes, múltiplas fontes de dados ou fluxos testáveis fora da UI.

## 7. Matriz de propriedade

| Responsabilidade | Package | Motivo |
|---|---|---|
| Entidades, value objects e regras de validade/preço | Core | iguais nas duas plataformas |
| Casos de uso e contratos de repository | Core | comportamento compartilhável |
| REST client, autenticação HTTP, erros e correlation ID | Core | mesma API |
| Tokens, tema base e componentes equivalentes | Core | consistência visual |
| Estado remoto/cache de consulta | Core | representa dados e regras |
| Router, redirects e shells | Web/Mobile | semântica de plataforma |
| Sidebar, tabelas densas, hover e atalhos | Web | interação de browser/desktop |
| QR scan por câmera, push, biometria e permissões | Mobile | plugins nativos |
| Sessão persistida | contrato no Core; implementação por app | storage e risco diferem |
| Compartilhamento/deep link | intenção no Core; adaptador por app | mecanismo de plataforma |
| Upload de imagem/PDF | caso de uso no Core; seletor por app | seleção depende da plataforma |

## 8. Features e bounded contexts do front-end

- `authentication`: login, recuperação, sessão e logout.
- `supermarkets`: conta do tenant e operação de supermercados pelo Super Admin.
- `stores`: lojas, seletor de contexto e visão multi-loja.
- `plans`: plano, limites, funcionalidades e assinatura.
- `catalog`: categorias, produtos, pesquisa e filtros.
- `campaigns_offers`: oferta, campanha, vigência, cópia, publicação e cancelamento.
- `publication`: read model público, banners, avisos e tabloide vigente.
- `qr_codes`: geração, identificação, estado e atribuição de origem.
- `content_media`: upload e referências de imagens/PDF.
- `analytics`: períodos, indicadores, rankings e comparações.
- `audit`: alterações relevantes e autoria.

`public_offers`, no Web, orquestra os dados de `publication` e eventos de analytics; ele não reutiliza as telas administrativas de ofertas.

## 9. Navegação Web

Rotas conceituais, sujeitas ao contrato final de URLs:

```text
/ofertas/:supermarketSlug/:storeSlug    # pública
/ofertas/.../oferta/:offerId            # deep link compartilhável
/tabloide/:publicationId                # conteúdo vigente, se necessário
/entrar                                  # autenticação de tenant
/recuperar-acesso
/app/...                                 # painel do supermercado
/admin/entrar                            # autenticação administrativa isolada
/admin/...                               # painel da plataforma
```

Regras:

- URLs públicas são estáveis, compartilháveis e preservam origem do QR por parâmetro opaco validado no servidor.
- Redirect de autenticação preserva destino seguro.
- Guardas verificam estado de sessão e capacidade, mas o servidor autoriza cada operação.
- Rotas públicas, tenant e Super Admin usam shells e árvores separadas.
- Query parameters representam filtros compartilháveis; segredos e tokens não permanecem na URL.
- Página inexistente, conteúdo expirado e acesso negado têm estados distintos.

## 10. Estado e fluxo de dados

Adotar fluxo unidirecional:

```text
interação -> controller/notifier -> caso de uso -> repository -> REST client
     ^                                                   │
     └──────────── estado imutável / resultado ──────────┘
```

- Modelos de estado e regras compartilháveis vivem no Core: sessão, ofertas carregadas, paginação, filtros de domínio e resultados analíticos. `ChangeNotifier`s compartilhados só ficam no Core quando Web e Mobile executarem exatamente o mesmo fluxo; os demais pertencem à apresentação de cada aplicação.
- Estado visual vive na plataforma: sidebar, aba, hover, modal, bottom navigation e dimensões.
- Provider com `ChangeNotifier` é o padrão de estado da apresentação. Estado global fica restrito a sessão, configuração, tema e contexto de tenant/loja autorizado.
- Estado de formulário é local até ser convertido em comando validado.
- Escritas usam atualização otimista somente quando reversão é segura; publicar, bloquear, trocar plano e desativar exigem confirmação do servidor.
- Invalidação é orientada pela feature e pelas chaves afetadas, não por limpeza global.

## 11. Contrato com a API

- REST/JSON conforme a arquitetura de back-end.
- Gerar DTOs/clientes a partir de OpenAPI somente se a geração produzir código revisável e estável; domínio não depende diretamente dos DTOs gerados.
- Separar endpoints públicos, tenant e plataforma também no client.
- Padronizar `ApiFailure` com código, mensagem segura, campos inválidos, status e `correlationId`.
- Propagar correlation ID e exibi-lo em erros de suporte, nunca detalhes internos.
- Aplicar paginação e debounce/cancelamento em pesquisa.
- Uploads usam endpoint controlado ou URL assinada; progresso e falha são estados explícitos.
- Retry automático apenas em leitura idempotente e falhas transitórias, com limite e jitter. Nunca repetir mutações sem idempotency key.
- Datas trafegam com offset/UTC; valores monetários não usam `double` no domínio.

## 12. Autenticação, autorização e tenancy

O Core define `Session`, `Actor`, `Role`, `Capability`, `AuthRepository` e `SessionStore`. Web e Mobile fornecem armazenamento adequado e composição.

- Web prefere cookie `HttpOnly`, `Secure` e `SameSite` quando o back-end suportar sessão/BFF; evitar tokens persistentes em `localStorage`.
- Se token em memória for inevitável, refresh e proteção CSRF/CORS devem ser decididos junto ao back-end.
- Mobile usa armazenamento seguro do sistema para credenciais renováveis.
- Logout limpa estado local e revoga a sessão no servidor.
- Troca de tenant/loja apenas escolhe contexto entre opções autorizadas; IDs enviados pela UI nunca concedem acesso.
- Feature flags e limites de plano controlam descoberta e feedback da UI, não substituem autorização server-side.
- A aplicação de Super Admin não oferece “atalho” irrestrito aos dados privados do consumidor/tenant.

## 13. Design system e responsividade

O Core contém tokens semânticos (`color.action.primary`, `spacing.md`, `radius.card`), tipografia, temas claro/escuro se necessários e componentes base acessíveis. Não usar cores ou dimensões mágicas nas features.

Componentes candidatos ao Core: botão, input, feedback, preço, badge de validade, imagem com fallback, empty state e skeleton. Sidebar, tabela administrativa, card com hover e bottom sheet permanecem na plataforma quando o comportamento divergir.

Breakpoints devem representar mudança de composição, não modelos de dispositivo. A vitrine pública começa pelo menor viewport; painéis adotam navegação compacta quando necessário. Preço promocional, validade e ação principal não podem depender de imagem, hover ou apenas cor.

## 14. Fluxos críticos

### Scan até ofertas

1. Browser abre URL curta do QR e o servidor resolve o destino estável.
2. Web carrega shell público mínimo e solicita o read model vigente da loja.
3. Identidade do mercado, preços e validade aparecem antes de recursos secundários.
4. Imagens carregam progressivamente com dimensões reservadas e fallback.
5. Evento de scan/visualização é enviado sem bloquear o conteúdo; falha analítica não falha a página.

### Publicar oferta/campanha

1. Usuário seleciona tenant/lojas dentro das permissões.
2. Formulário valida preço, vigência, produto e condições localmente e no servidor.
3. Revisão resume impacto e lojas antes da confirmação.
4. Mutação idempotente retorna estado publicado ou erro de negócio.
5. Queries afetadas são invalidadas e a UI informa quando a visão pública estiver atualizada.

### Operação crítica do Super Admin

1. Página mostra estado, plano, limites e impacto.
2. Ação de bloquear/desativar/trocar plano exige confirmação explícita contextual.
3. Botão fica protegido contra envio repetido.
4. Resultado inclui novo estado e referência de auditoria/correlation ID quando aplicável.

## 15. Estratégia de entrega

1. Criar workspace, regras de dependência, análise estática e pipeline.
2. Construir Core transversal mínimo: configuração, erros, REST, sessão e tokens visuais.
3. Entregar fatia vertical pública: QR -> ofertas vigentes -> detalhe -> analytics tolerante a falha.
4. Entregar autenticação e shell do supermercado.
5. Adicionar lojas, catálogo, campanhas/ofertas, QR e mídia por fatias completas.
6. Adicionar dashboard/analytics e auditoria.
7. Entregar shell e features do Super Admin reutilizando somente o Core.
8. Implementar experiência Mobile apenas quando o roadmap validar um caso nativo.

## 16. Critérios para evolução

| Evidência | Evolução possível |
|---|---|
| Core grande com fronteira estável e consumidores independentes | avaliar quarto package por ADR |
| Primeira oferta útil não cumpre meta mesmo após otimização | avaliar renderer Web específico para a vitrine pública |
| Aplicativo nativo entra no roadmap | ativar features no Mobile sem mover UI Web ao Core |
| Contratos divergem frequentemente da API | automatizar geração e contract tests no CI |
| Cache de estado causa bugs de invalidação | reduzir escopo e revisar chaves/ownership |

Um renderer alternativo para a página pública seria exceção à estratégia Flutter única e exige decisão de produto/arquitetura baseada em métricas, não preferência técnica.
