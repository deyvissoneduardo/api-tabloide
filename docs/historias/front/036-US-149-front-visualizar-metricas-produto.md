# US-149 — Visualizar métricas por produto

## Scrum

- **Prioridade:** 036
- **Story Points:** 5
- **Status de refinamento:** Pronta
- **Dependências:** Back/US-056, Back/US-106
- **Bloqueia:** Nenhuma
- **Risco:** Médio

### Scrum Poker

- Front end: 5
- QA: 5
- Scrum Master: 3

**Consenso:** 5 SP

### Justificativa

Estimativa relativa baseada no escopo descrito, nos cenários de validação e teste, no risco médio e em 2 dependência(s) identificada(s).

---

## 1. Visão geral

A capacidade deve implementar visualizar métricas por produto usando as regras aprovadas de analytics, dashboard e relatórios. O problema resolvido é permitir um comportamento consistente, autorizado, testável e rastreável para responsável pelo produto. O resultado esperado é a conclusão observável da capacidade sem depender do documento de origem.

---

## 2. História de usuário

**Como** responsável pelo produto  
**Quero** visualizar métricas por produto  
**Para** obter informação confiável para executar sua tarefa ou tomar decisão

---

## 3. Contexto de negócio

Esta história pertence a **Supermercado**, domínio **Analytics, dashboard e relatórios**, seção “B11. Analytics”. O requisito de origem é SUP-RF-113. As decisões de 24/08/2026 têm precedência sobre interpretações anteriores e foram incorporadas abaixo.

---

## 4. Escopo

### 4.1 Incluído

- Executar visualizar métricas por produto no escopo autorizado.
- Aplicar as regras de analytics, dashboard e relatórios documentadas nesta história.
- Validar entradas, vínculos, estados e concorrência antes de qualquer efeito.
- Fornecer resposta inequívoca e registrar observabilidade.
- Preservar isolamento entre supermercados.

### 4.2 Não incluído

- Comportamentos de outros domínios que possuem história própria.
- Cobrança ou pagamento dentro do SGTM.
- Coleta de dados pessoais do consumidor.
- Alteração física de registros com histórico.
- Funcionalidades não selecionadas nem aprovadas para o MVP.

---

## 5. Atores e permissões

- DONO pode executar a capacidade dentro do próprio supermercado.
- OPERADOR não pode executar esta capacidade, salvo seleção de recursos existentes dentro do fluxo de promoções.
- Super Admin pode consultar ou administrar para suporte conforme sua permissão global.
- Toda autorização é validada no backend; ocultar controle na interface não substitui autorização.

---

## 6. Pré-condições

- O ator e o contexto existem e estão no escopo correto.
- O ator está autenticado, exceto em acesso público.
- Recursos relacionados existem e não foram removidos logicamente.
- O estado atual permite a ação solicitada.
- O supermercado não está DESATIVADO; se BLOQUEADO, somente consultas são permitidas.

---

## 7. Gatilho

O gatilho ocorre quando responsável pelo produto solicita visualizar métricas por produto.

---

## 8. Fluxo principal

1. O ator acessa a consulta de visualizar métricas por produto.
2. O sistema valida escopo e filtros informados.
3. O sistema consulta somente dados autorizados, aplicando paginação quando houver coleção.
4. O sistema apresenta resultado, período e critérios utilizados.
5. Se não houver dados, apresenta estado vazio sem fabricar valores.

---

## 9. Fluxos alternativos

- **Lista ou resultado vazio:** devolver coleção vazia e mensagem informativa, nunca erro ou dado inventado.
- **Recurso opcional ausente:** continuar o fluxo quando a ausência for permitida e usar representação neutra.
- **Supermercado BLOQUEADO:** permitir somente leitura e impedir qualquer mutação.
- **Reutilização:** quando o domínio permitir, selecionar cadastro existente sem criar duplicata técnica.

---

## 10. Fluxos de exceção

- **401:** sessão ausente, inválida ou expirada; nenhuma operação é executada.
- **403:** perfil ou supermercado sem permissão; nenhum dado restrito é revelado.
- **404:** recurso não existe no escopo autorizado.
- **409:** duplicidade, versão desatualizada ou estado incompatível.
- **422:** campos inválidos, com erros associados aos campos.
- **Timeout/500:** não confirmar sucesso; operação atômica é revertida ou pode ser consultada por identificador idempotente.
- **Dependência indisponível:** preservar o estado anterior e registrar correlação para diagnóstico.

---

## 11. Regras de negócio

### RN-001 — Regra aprovada 1

**Descrição:**  
Eventos: acesso à página, scan de QR Code, visualização de oferta, categoria acessada e compartilhamento.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

### RN-002 — Regra aprovada 2

**Descrição:**  
Cada evento contém identificador único, supermercado, loja quando aplicável, recurso, data/hora UTC e origem; exibição usa horário de Brasília.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

### RN-003 — Regra aprovada 3

**Descrição:**  
Métricas não identificam o consumidor e são agregadas sempre que possível.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

### RN-004 — Regra aprovada 4

**Descrição:**  
Acesso conta quando a página pública carrega o conteúdo essencial; atualização da mesma página após 30 minutos conta novo acesso.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

### RN-005 — Regra aprovada 5

**Descrição:**  
Visualização de oferta conta ao abrir o detalhe; categoria acessada conta ao selecionar filtro/categoria.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

### RN-006 — Regra aprovada 6

**Descrição:**  
Período inclui início às 00:00:00 e fim às 23:59:59 em Brasília.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

### RN-007 — Regra aprovada 7

**Descrição:**  
Período padrão: últimos 30 dias; máximo recomendado por consulta: 24 meses.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

### RN-008 — Regra aprovada 8

**Descrição:**  
Atualização recomendada: até 15 minutos após o evento.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

### RN-009 — Regra aprovada 9

**Descrição:**  
Rankings ordenam por contagem decrescente, depois por nome e identificador.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

### RN-010 — Regra aprovada 10

**Descrição:**  
Comparações exibem valor absoluto e variação percentual; quando a base é zero, mostram “não calculável”.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

### RN-011 — Regra aprovada 11

**Descrição:**  
Dashboard resume acessos, scans, ofertas, categorias e QR Codes mais utilizados, com período e comparação.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

### RN-012 — Regra aprovada 12

**Descrição:**  
Super Admin vê consolidação de toda a plataforma e identifica alta, baixa ou ausência de uso.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

### RN-013 — Regra aprovada 13

**Descrição:**  
Baixa utilização recomendada: valor abaixo de 20% da mediana de supermercados ativos no mesmo período; inativo: zero eventos no período. O critério deve ser exibido na interface.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

### RN-014 — Regra aprovada 14

**Descrição:**  
Relatórios podem ser exportados em CSV, respeitando filtros e escopo.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de analytics, dashboard e relatórios afetados por esta história.

---

## 12. Requisitos funcionais

- **RF-001:** O sistema deve visualizar métricas por produto.
- **RF-002:** O backend deve validar perfil, supermercado e estado em toda solicitação.
- **RF-003:** O sistema deve aplicar os campos e regras da seção 16.
- **RF-004:** Nenhuma validação malsucedida pode produzir alteração parcial.
- **RF-005:** Alterações relevantes devem gerar auditoria com ator, instante, entidade, antes, depois e resultado.
- **RF-006:** Respostas devem distinguir sucesso, vazio, validação, permissão, conflito e indisponibilidade.

---

## 13. Critérios de aceite

### CA-001 — Execução autorizada

```gherkin
Cenário: Executar com dados e estado válidos
  Dado que o ator está no escopo autorizado
  E o recurso atende às regras desta história
  Quando solicitar "visualizar métricas por produto"
  Então o sistema deve concluir a operação de forma atômica
  E não deve afetar dados de outro supermercado
```

### CA-002 — Validação

```gherkin
Cenário: Rejeitar entrada inválida
  Dado que um campo obrigatório ou vínculo viola uma regra documentada
  Quando a solicitação for enviada
  Então o sistema deve rejeitar toda a operação
  E deve identificar a regra violada
```

### CA-003 — Permissão

```gherkin
Cenário: Impedir ator sem permissão
  Dado que o ator não possui o perfil exigido
  Quando tentar executar a capacidade
  Então o sistema deve responder acesso negado
  E não deve revelar nem alterar o recurso
```

### CA-004 — Concorrência ou repetição

```gherkin
Cenário: Repetir a mesma solicitação crítica
  Dado que a primeira solicitação já foi processada
  Quando a mesma chave idempotente for reenviada
  Então o sistema deve devolver o resultado original
  E não deve duplicar efeito, registro ou métrica
```

---

## 14. Experiência do usuário

### 14.1 Comportamento da interface

A tela deve apresentar visualizar métricas por produto com título, contexto atual, controles autorizados e linguagem de varejo. Listas usam pesquisa, filtros e paginação; formulários preservam valores após erro. A ação principal fica desabilitada durante envio.

### 14.2 Estados da interface

- Carregamento com indicação acessível.
- Conteúdo com filtros e ações autorizadas.
- Vazio com explicação e próxima ação possível.
- Erro recuperável com tentativa novamente.
- Sem permissão sem exposição de conteúdo.
- Somente leitura quando o supermercado estiver BLOQUEADO.
- Indisponível quando estiver DESATIVADO.

### 14.3 Validação de campos

- período inicial e final em America/Sao_Paulo.
- dimensão e filtros autorizados.
- contagens agregadas e variação comparativa.

### 14.4 Feedback ao usuário

Sucesso identifica a operação concluída. Erro de campo informa regra violada. Conflito solicita atualização dos dados. Ação crítica descreve seu impacto antes da confirmação.

### 14.5 Responsividade

Desktop, tablet e mobile mantêm conteúdo essencial e ações alcançáveis. Página pública é mobile-first; tabelas administrativas viram cartões ou permitem rolagem identificada.

### 14.6 Acessibilidade

Navegação por teclado, foco visível, labels programáticos, anúncios de erro/sucesso, contraste WCAG AA e informação não dependente apenas de cor.

---

## 15. Contratos e integrações

A interface consome a API `/api/metricas` usando o método correspondente à ação. Deve tratar 200/201, 400/422, 401, 403, 404, 409 e 500 quando aplicáveis, sem inferir sucesso antes da resposta.

Todos os contratos usam JSON UTF-8, datas ISO 8601 e identificadores opacos. Datas são persistidas em UTC e apresentadas em `America/Sao_Paulo`.

---

## 16. Dados

- período inicial e final em America/Sao_Paulo.
- dimensão e filtros autorizados.
- contagens agregadas e variação comparativa.
- Todos os registros pertencem a um supermercado, exceto recursos globais do Super Admin.
- Exclusões com histórico são lógicas; imagens desvinculadas podem ser removidas e liberar cota.
- Auditoria é retida por 24 meses; eventos brutos por 90 dias e agregados por 24 meses.

---

## 17. Estados e transições

Não há ciclo de vida próprio nesta história; ela respeita os estados do recurso relacionado.

Transições fora das listadas devem retornar conflito sem alterar o estado.

---

## 18. Segurança

- Autenticação por e-mail e senha nos fluxos administrativos.
- Autorização por perfil e escopo em toda requisição.
- Identificadores recebidos nunca determinam autorização por si só.
- Senhas, tokens e dados pessoais desnecessários não aparecem em logs.
- Entradas e arquivos são validados por conteúdo.
- Consumidor público não é identificado nem submetido a fingerprinting.

---

## 19. Requisitos não funcionais

- APIs: p95 recomendado de até 2 segundos.
- Página pública: p95 de até 3 segundos em 4G e disponibilidade mensal de 99,5%.
- Listas administrativas: paginação no servidor com 25 itens por padrão e opções 25, 50 e 100.
- Compatibilidade: duas versões estáveis mais recentes de Chrome, Edge, Firefox e Safari.
- Acessibilidade: WCAG AA nos fluxos frontend.
- Datas: persistência UTC e apresentação em horário de Brasília.

---

## 20. Observabilidade

- Log estruturado de início, resultado, duração, ator técnico, entidade e correlação.
- Métricas de quantidade, sucesso, erro e latência.
- Alerta para aumento sustentado de erros ou violação das metas aplicáveis.
- Auditoria separada de log técnico e sem segredos.
- Reprocessamento não pode duplicar efeitos ou eventos.

---

## 21. Analytics e eventos de produto

- `visualizar_metricas_produto_iniciado`: início intencional do fluxo.
- `visualizar_metricas_produto_concluido`: após confirmação do resultado.
- `visualizar_metricas_produto_erro`: término sem conclusão, com categoria não sensível.
- Eventos contêm identificador único, contexto autorizado e instante UTC.
- Nenhum evento identifica diretamente o consumidor.

---

## 22. Dependências

- US-056.
- US-106.
- Serviço de autenticação/autorização quando o fluxo for administrativo.
- Auditoria e observabilidade para alterações relevantes.
- Decisões consolidadas do MVP, já reproduzidas nesta história.

---

## 23. Riscos e impactos

- Autorização incorreta pode causar acesso cruzado entre supermercados.
- Concorrência pode sobrescrever atualização recente sem controle de versão.
- Automação repetida pode duplicar estado ou métrica sem idempotência.
- Alteração de estado pode afetar página pública e recursos vinculados.
- Contrato divergente das regras desta história pode gerar regressão entre frontend e backend.

---

## 24. Casos de borda

- Sessão expira entre abertura e confirmação.
- Recurso é alterado ou desativado por outro usuário.
- Solicitação é enviada duas vezes.
- Texto contém espaços externos, acentos ou caracteres especiais.
- Lista está vazia ou contém mais de uma página.
- Resposta chega após timeout do cliente.
- Supermercado muda para BLOQUEADO durante uma edição.
- Recurso relacionado expira durante o processamento.

---

## 25. Cenários de teste sugeridos

### Positivos

- Executar com cada perfil autorizado e dados mínimos válidos.
- Confirmar persistência, resposta, auditoria e reflexos dependentes.

### Negativos

- Validar campos obrigatórios, formatos, limites, duplicidade e estado incompatível.
- Simular timeout e falha interna sem alteração parcial.

### Permissão

- Tentar com OPERADOR, DONO de outro supermercado e sessão ausente.
- Confirmar comportamento somente leitura para supermercado BLOQUEADO.

### Integração

- Validar códigos, schema, datas, paginação e idempotência.
- Confirmar correlação entre resposta, estado persistido, logs e eventos.

### Edge cases

- Executar concorrentemente, reenviar e alterar recurso relacionado durante o fluxo.
- Testar valores mínimo/máximo e mudança de data no fuso de Brasília.

---

## 26. Definition of Ready

- [ ] Objetivo da história está claro
- [ ] Escopo está definido
- [ ] Regras de negócio estão documentadas
- [ ] Critérios de aceite estão testáveis
- [ ] Dependências foram identificadas
- [ ] A única pendência aplicável, limite de lojas por plano, está explicitamente registrada
- [ ] Contrato proposto está consistente com frontend e backend

---

## 27. Definition of Done

- [ ] Implementação concluída
- [ ] Critérios de aceite atendidos
- [ ] Testes automatizados implementados
- [ ] Testes funcionais executados
- [ ] Tratamento de erros implementado
- [ ] Permissões e isolamento validados
- [ ] Logs, métricas e auditoria implementados quando aplicável
- [ ] Acessibilidade validada quando aplicável
- [ ] Documentação atualizada
- [ ] Sem regressões conhecidas
- [ ] Code review realizado
- [ ] Deploy validado no ambiente do projeto

---

## 28. Questões em aberto

Não há questões funcionais em aberto para esta história. Decisões técnicas podem ajustar implementação sem alterar as regras e critérios aprovados.

---

## 29. Referências

- Documento de origem: `docs/mvp.md` para requisitos ADM/SUP; `docs/historias/decisoes-mvp.md` para requisitos DEC.
- Item de origem: `SUP-RF-113 — O supermercado deve poder visualizar métricas por produto.`
- Decisões aplicáveis: seção “Analytics, dashboard e relatórios” de `docs/historias/decisoes-mvp.md`.
- Histórias relacionadas: US-056, US-106.

