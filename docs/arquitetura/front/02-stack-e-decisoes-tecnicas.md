# Stack e decisões técnicas do front-end SGTM

## Resposta curta

| Capacidade | Decisão inicial |
|---|---|
| UI | Flutter + Dart estáveis, mesma versão em todo o workspace |
| Workspace | Dart workspace + Melos para scripts coordenados |
| Estado e DI | `provider` com `ChangeNotifier`, com escopo próximo da feature |
| Navegação | rotas nomeadas nativas do Flutter, configuradas separadamente em Web e Mobile |
| HTTP | Dio encapsulado por um REST client no Core |
| Modelos/JSON | modelos imutáveis e geração com `freezed`/`json_serializable` onde reduzir erro real |
| Contrato | OpenAPI; geração avaliada por prova técnica |
| Persistência de sessão | contrato no Core e implementação por plataforma |
| Internacionalização | `flutter_localizations` + ARB, iniciando em `pt_BR` |
| Gráficos | biblioteca escolhida por prova de acessibilidade, Web e bundle |
| Testes | `flutter_test`, mocks/fakes mínimos e `integration_test` para fluxos críticos |

Não fixar versões nesta documentação. SDK, bibliotecas e hashes pertencem aos manifestos e lockfile.

## 1. Flutter para Web e Mobile

A escolha é mandatória e favorece consistência, uma equipe e reaproveitamento. Para o SGTM, ela funciona especialmente bem nos painéis administrativos e permite reaproveitar domínio/design system em uma aplicação nativa futura.

O ponto sensível é a vitrine pública: Flutter Web possui custo de bootstrap maior que HTML renderizado no servidor e metadados/SEO exigem cuidado. O MVP depende de acesso direto por QR, não de busca orgânica, portanto a decisão é aceitável se houver orçamento de desempenho e medição em celulares intermediários. A escolha deve ser reavaliada se prejudicar a hipótese principal de scan -> consulta.

## 2. Workspace e Melos

Usar workspace Dart para resolução local e Melos como camada de automação, pois há três packages com análise, testes e builds coordenados. Scripts mínimos:

```text
bootstrap
format
format:check
analyze
test
test:core
test:web
test:mobile
build:web
build:mobile
codegen
```

Melos não controla arquitetura. O CI deve verificar imports proibidos e dependências nos `pubspec.yaml`.

## 3. Provider com ChangeNotifier

`provider` será usado para injeção na árvore de widgets e `ChangeNotifier` para o estado da apresentação. Casos de uso, entidades e repositories permanecem classes Dart independentes, sem `BuildContext` ou dependência de Provider.

Diretrizes:

- dependências estáveis são fornecidas no composition root com `Provider`;
- cada página ou subárvore cria seus `ChangeNotifierProvider`s próximos da feature;
- `MultiProvider` organiza a composição, mas não vira um registro global de toda a aplicação;
- `ChangeNotifier` coordena casos de uso e expõe estado imutável da tela; não contém HTTP nem regra de negócio;
- separar notifiers quando estados possuírem ciclos de vida ou frequências de atualização diferentes;
- widgets usam `context.select`, `Selector` ou `Consumer` no menor trecho necessário para limitar rebuilds;
- operações assíncronas representam explicitamente estado inicial, carregando, sucesso, vazio e falha;
- `notifyListeners()` ocorre somente após uma mudança observável e nunca dentro de `build`;
- recursos e listeners pertencentes ao notifier são liberados em `dispose`;
- testes instanciam o notifier diretamente com fakes dos casos de uso/repositories;
- não usar `Provider.of`/`context.read` como service locator dentro do domínio ou da infraestrutura.

Notifiers globais ficam restritos a sessão, configuração, tema e contexto autorizado de tenant/loja. Estado de formulário e de interação deve permanecer no menor escopo possível.

## 4. Navegação com rotas nomeadas nativas

Cada aplicação declara seus próprios nomes de rota e sua própria `RouteFactory`, usando `MaterialApp`, `Navigator`, `Navigator.pushNamed`, `routes`, `onGenerateRoute` e `onUnknownRoute`. O Core não conhece `Navigator`, `BuildContext`, `RouteSettings` nem URLs.

Rotas estáticas simples podem usar a tabela `routes`. Rotas com argumentos, segmentos dinâmicos ou validação devem passar por `onGenerateRoute`, que converte `RouteSettings` em uma página tipada. Argumentos recebidos são validados antes de construir a tela; argumento ausente ou inválido leva a uma rota de erro segura.

No Web, exigir:

- configuração do host para devolver o entry point Flutter em acessos diretos;
- nomes públicos compatíveis com URLs estáveis e compartilháveis;
- tratamento de rota inicial obtida do browser, inclusive parâmetros de QR, loja e oferta;
- guarda centralizada antes de construir uma rota protegida, preservando o destino pretendido;
- prevenção de loops entre login, recuperação e rota protegida;
- comportamento previsível dos botões voltar/avançar e restauração da rota;
- `onUnknownRoute` para endereço inexistente e estados distintos para conteúdo expirado e acesso negado;
- teste de acesso direto, refresh e compartilhamento de toda rota pública.

Não espalhar strings de rota pelas telas. Cada aplicação mantém um catálogo próprio de constantes e funções tipadas para montar nomes/argumentos. O uso de rotas nomeadas nativas deve ser reavaliado por ADR apenas se limitações comprovadas de deep link ou histórico do browser impedirem requisitos do produto.

## 5. HTTP com Dio

O Core encapsula Dio atrás de `RestClient`; features não dependem diretamente da biblioteca. Interceptors autorizados:

- base URL/headers de ambiente;
- credencial/sessão;
- correlation ID;
- refresh coordenado, evitando múltiplas renovações simultâneas;
- telemetria sanitizada;
- retry limitado para operações idempotentes.

Não registrar corpos, tokens, dados pessoais ou URLs assinadas. Cancelamento deve acompanhar descarte de pesquisa/página quando útil.

## 6. Modelagem e geração de código

Separar:

- DTO: formato de transporte, tolera evolução aditiva do contrato;
- modelo de domínio: invariantes e tipos seguros;
- view model: forma específica de apresentação.

Usar geração para igualdade, unions de estado e serialização repetitiva. Evitar gerar camadas inteiras difíceis de depurar. Toda geração deve ser determinística e verificada no CI; definir no início se arquivos gerados são versionados.

Dinheiro deve usar representação decimal/inteira coerente com a API. Datas de validade comercial precisam de tipo e timezone explícitos. Slugs e IDs opacos não são intercambiáveis.

## 7. Design system

O Core oferece API pública por componentes e tokens semânticos. Componentes devem aceitar conteúdo e intenção, não expor todos os detalhes de Material. A aplicação pode usar Material internamente sem acoplar o domínio a ele.

Antes de adicionar Storybook-like tooling, provar o valor. No MVP, uma gallery interna com estados, tamanhos, contraste e navegação por teclado é suficiente. Golden tests cobrem componentes estáveis e críticos, sem transformar cada pixel em contrato frágil.

## 8. Analytics de produto

Eventos são contratos explícitos e versionáveis, por exemplo:

```text
qr_resolved
public_page_viewed
category_selected
offer_viewed
share_requested
```

O Core define nome e payload permitido; Web/Mobile fornecem contexto técnico. Nenhum evento deve conter dado pessoal por conveniência. Envio é assíncrono, tolerante a falha e não bloqueia navegação. Métricas do painel vêm da API agregada, não do SDK analítico no cliente.

## 9. Configuração e ambientes

Ambientes: development, staging e production. Valores públicos de build podem usar `--dart-define`; segredos nunca fazem parte do bundle Flutter. Validar no bootstrap URL da API, ambiente e flags essenciais. Produção não aceita endpoints locais ou configuração de debug.

Feature flags devem ter owner, valor padrão seguro e data/critério de remoção. Plano comercial não é uma feature flag local: capacidades vêm do servidor.

## 10. Dependências evitadas inicialmente

- múltiplos frameworks de estado/DI;
- banco offline e sincronização complexa;
- micro-frontends;
- package separado por feature;
- service locator global;
- biblioteca de responsive design que apenas esconde breakpoints;
- wrapper genérico de repository/CRUD;
- dependência direta de browser ou plugin nativo no Core;
- SDK analítico que colete identidade automaticamente.

## 11. Prova técnica antes do primeiro release

Construir uma fatia descartável ou evolutiva que prove:

1. acesso direto por URL de QR em browser mobile;
2. primeira lista com imagem progressiva e estado vazio/erro;
3. login, refresh concorrente e logout revogável;
4. navegação protegida e tentativa de trocar tenant manualmente;
5. publicação de oferta com erro padronizado e correlation ID;
6. execução dos testes dos três packages pelo workspace;
7. build Web, deep link no host e medição em rede/celular representativos.
