# Qualidade, segurança e operação do front-end SGTM

## 1. Atributos de qualidade

| Atributo | Decisão verificável |
|---|---|
| Desempenho | medir scan até conteúdo útil, tamanho de bundle, frames e chamadas críticas |
| Disponibilidade percebida | conteúdo essencial não depende de analytics nem de imagens |
| Acessibilidade | navegação por teclado, semântica, contraste, escala de texto e leitor de tela |
| Segurança | sessão adequada por plataforma, CSP/headers no host e nenhum segredo no bundle |
| Privacidade | vitrine anônima e eventos mínimos/agregados por padrão |
| Manutenibilidade | imports unidirecionais, API pública do Core e ownership por feature |
| Escalabilidade de UI | paginação/virtualização e filtros server-side para listas grandes |
| Observabilidade | falhas técnicas correlacionáveis sem registrar dados sensíveis |

Metas numéricas finais devem ser aprovadas pelo produto após baseline. Sem metas, o pipeline coleta tendência e impede regressões relevantes em relação ao release anterior.

## 2. Pirâmide de testes

### Core

- unidade: preço/desconto, vigência, validações, permissões representadas e mapeamento de falhas;
- casos de uso: sucesso, vazio, paginação, cancelamento e erro;
- infraestrutura: serialização, headers, refresh único concorrente, retry e correlation ID;
- contratos: exemplos OpenAPI e ausência de campos administrativos no modelo público.

### Web

- widget: cards de oferta, filtros, formulários, confirmações e estados de feedback;
- responsividade: viewports pequenos, médios e largos;
- navegação: deep links, redirects, voltar/avançar e rotas negadas;
- browser: storage, compartilhamento, foco, teclado e upload;
- integração: QR -> ofertas, login -> painel, publicar oferta e ação crítica administrativa.

### Mobile

- widget e navegação dos fluxos efetivamente implementados;
- integrações por adapter com fakes, seguidas por testes em Android/iOS;
- deep links, permissões e lifecycle quando entrarem no escopo.

Poucos E2E devem cobrir jornadas que quebrariam receita ou confiança. Não duplicar toda regra de domínio em E2E.

## 3. Cenários BDD prioritários

```gherkin
Funcionalidade: consultar ofertas por QR Code
  Cenário: loja possui ofertas vigentes
    Dado um QR Code ativo vinculado à loja
    Quando o consumidor abrir seu endereço
    Então verá a identidade da loja e somente as ofertas vigentes
    E não precisará autenticar-se

  Cenário: analytics está indisponível
    Dado que a loja possui ofertas vigentes
    E o coletor de eventos está indisponível
    Quando o consumidor abrir a página
    Então continuará vendo as ofertas
```

```gherkin
Funcionalidade: isolamento entre supermercados
  Cenário: usuário altera o identificador da loja na URL/requisição
    Dado que o usuário pertence ao supermercado A
    Quando tentar acessar uma loja do supermercado B
    Então receberá acesso negado
    E nenhum dado do supermercado B será exibido ou preservado em cache
```

```gherkin
Funcionalidade: publicação de oferta
  Cenário: oferta com validade inválida
    Quando o operador informar término anterior ao início
    Então verá uma mensagem clara junto ao campo
    E a oferta não será publicada
```

## 4. Acessibilidade

- Alvo mínimo: WCAG 2.2 nível AA nos principais fluxos.
- Ordem de foco acompanha a ordem visual; modais aprisionam e devolvem foco corretamente.
- Todo controle possui nome, papel, estado e alvo de toque adequados.
- Teclado opera painéis e vitrine; hover nunca é o único meio de revelar ação.
- Preço anterior, promocional, desconto e validade possuem semântica compreensível.
- Erro é associado ao campo e resumido no formulário; não depende apenas de vermelho.
- Zoom/escala de texto não corta informações essenciais.
- Gráficos têm resumo textual/tabela equivalente.
- Imagem promocional possui alternativa útil ou é marcada decorativa.

Automação detecta parte dos problemas; revisão manual com teclado, leitor de tela e zoom integra o critério de aceite.

## 5. Desempenho da vitrine pública

O indicador principal é `QR aberto -> primeira oferta útil visível`. Medir em aparelho intermediário e rede móvel limitada, incluindo cache frio.

Práticas obrigatórias:

- build separado da vitrine/painéis se a medição demonstrar que módulos administrativos aumentam o bootstrap público; inicialmente, usar entry points e carregamento diferido quando suportado de forma confiável;
- read model público enxuto e paginado;
- fontes e assets mínimos, comprimidos e cacheáveis;
- imagens responsivas, dimensões reservadas, lazy loading e fallback;
- skeleton apenas onde reduz mudança visual, sem esconder espera indefinida;
- debounce e cancelamento de pesquisa;
- listas lazy/virtualizadas;
- cache HTTP/CDN alinhado à versão de publicação;
- analytics fora do caminho crítico;
- ausência de trabalho síncrono pesado no isolate principal.

Monitorar tamanho comprimido e não comprimido dos artefatos, tempo de bootstrap, primeira renderização útil, p75/p95 de API, jank e taxa de falha de assets.

## 6. Segurança Web

- TLS e headers do host: CSP compatível com Flutter, HSTS, `X-Content-Type-Options`, política de referrer e permissions policy mínima.
- Definir CORS por origem conhecida; CORS não é autorização.
- Preferir sessão em cookie HttpOnly; se houver cookie, proteger mutações contra CSRF.
- Sanitizar/evitar HTML arbitrário em banners, avisos e descrições.
- Validar extensão, MIME real, tamanho e resultado do upload no servidor; cliente só antecipa feedback.
- Não armazenar secrets, credenciais administrativas, payloads privados ou PII em logs/cache persistente.
- Limpar queries e providers privados ao sair/trocar sessão ou tenant.
- Evitar enumeração em mensagens de recuperação de acesso.
- Dependências passam por atualização programada e análise de vulnerabilidade/licença.

Minificação e ocultação não protegem segredo. Toda regra de permissão e tenant é novamente aplicada pela API.

## 7. Privacidade e analytics

- A página pública funciona sem consentimento para processamento estritamente necessário.
- Analytics de produto usa identificadores opacos e dados agregáveis, sem CPF, e-mail ou fingerprint por padrão.
- Consentimento para WhatsApp ou finalidade futura é específico, informado, revogável e não pré-marcado.
- Não carregar trackers de terceiros antes da decisão de privacidade correspondente.
- Definir retenção e finalidade no back-end; a UI oferece transparência e mecanismo de revogação quando aplicável.

## 8. Observabilidade

Registrar no cliente somente eventos operacionais seguros:

- versão/build, ambiente e plataforma;
- rota normalizada, nunca parâmetros sensíveis;
- tipo/código da falha e correlation ID;
- duração e resultado de chamadas;
- erros Flutter não tratados e falhas de bootstrap.

Não registrar token, senha, corpo integral, URL assinada, endereço, contato ou texto livre do usuário. Source maps/símbolos ficam em armazenamento restrito. Alertas focam falha de login, erro da vitrine, regressão do tempo pós-scan e aumento de crashes.

## 9. CI/CD e regras de mudança

```text
mudança no Core   -> format + analyze + test Core + test/build Web + test/build Mobile
mudança no Web    -> format + analyze + test/build Web
mudança no Mobile -> format + analyze + test/build Mobile
```

Pipeline mínimo:

1. validar manifests, lockfile e direção de dependências;
2. conferir formatação e análise estática com warnings tratados;
3. verificar codegen limpo/reproduzível;
4. executar testes e cobertura orientada a risco;
5. construir artefatos por ambiente;
6. executar smoke test de rotas/deep links;
7. analisar dependências, licenças e artefato Web;
8. publicar artefato imutável com versão e permitir rollback.

Staging usa contrato e topologia próximos de produção, mas dados não sensíveis. Promoção para produção reutiliza o mesmo artefato quando a plataforma permitir.

## 10. Definition of Done arquitetural

- responsabilidade classificada e feature proprietária definida;
- nenhuma dependência proibida ou import de internals;
- estados loading/empty/error/denied e retry apropriado tratados;
- autorização e isolamento validados na API e representados corretamente na UI;
- acessibilidade por teclado/semântica/contraste revisada;
- telemetria sem dados sensíveis e com correlation ID;
- testes proporcionais ao risco passam nos packages afetados;
- fluxo funciona nos viewports e browsers suportados;
- documentação/ADR atualizada quando uma decisão arquitetural muda.

## 11. ADRs iniciais

Criar ADR quando a implementação começar para registrar:

1. Flutter único e condição de saída para a vitrine pública;
2. workspace com três packages e regra de dependência;
3. Provider com ChangeNotifier como estado/DI da apresentação;
4. rotas nomeadas nativas e RouteFactory por aplicação;
5. estratégia de sessão Web e Mobile;
6. geração ou escrita manual do client OpenAPI;
7. política de eventos de analytics e privacidade;
8. estratégia de deployment/entry points da vitrine e painéis.
