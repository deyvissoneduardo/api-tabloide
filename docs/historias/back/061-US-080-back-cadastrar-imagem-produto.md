# US-080 — Cadastrar uma imagem do produto

## Scrum

- **Prioridade:** 061
- **Story Points:** 5
- **Status de refinamento:** Pronta
- **Dependências:** Back/US-056, Back/US-078, Back/US-074
- **Bloqueia:** Nenhuma
- **Risco:** Médio

### Scrum Poker

- Back end: 5
- QA: 5
- Scrum Master: 3

**Consenso:** 5 SP

### Justificativa

Estimativa relativa baseada no escopo descrito, nos cenários de validação e teste, no risco médio e em 3 dependência(s) identificada(s).

---

## 1. Visão geral

A capacidade deve implementar cadastrar uma imagem do produto usando as regras aprovadas de produtos. O problema resolvido é permitir um comportamento consistente, autorizado, testável e rastreável para usuário autorizado do supermercado. O resultado esperado é a conclusão observável da capacidade sem depender do documento de origem.

---

## 2. História de usuário

**Como** usuário autorizado do supermercado  
**Quero** cadastrar uma imagem do produto  
**Para** manter a operação do SGTM atualizada e controlada

---

## 3. Contexto de negócio

Esta história pertence a **Supermercado**, domínio **Produtos**, seção “B5. Produtos”. O requisito de origem é SUP-RF-035. As decisões de 24/08/2026 têm precedência sobre interpretações anteriores e foram incorporadas abaixo.

---

## 4. Escopo

### 4.1 Incluído

- Executar cadastrar uma imagem do produto no escopo autorizado.
- Aplicar as regras de produtos documentadas nesta história.
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

O gatilho ocorre quando usuário autorizado do supermercado solicita cadastrar uma imagem do produto.

---

## 8. Fluxo principal

1. O ator inicia cadastrar uma imagem do produto.
2. O sistema apresenta ou recebe os campos aplicáveis listados na seção 16.
3. O sistema normaliza entradas e valida obrigatoriedade, formato, unicidade, vínculos, permissão e estado.
4. Se houver erro, rejeita toda a alteração e identifica cada campo inválido.
5. Com dados válidos, persiste a operação de forma atômica.
6. Registra auditoria quando a alteração for relevante.
7. Retorna o recurso, identificador e estado final sem expor dados de outro supermercado.

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
Cada supermercado mantém catálogo próprio e isolado.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de produtos afetados por esta história.

### RN-002 — Regra aprovada 2

**Descrição:**  
Nome e ao menos uma categoria são obrigatórios.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de produtos afetados por esta história.

### RN-003 — Regra aprovada 3

**Descrição:**  
Imagem, marca, descrição, peso, unidade e volume são opcionais.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de produtos afetados por esta história.

### RN-004 — Regra aprovada 4

**Descrição:**  
Produtos duplicados são permitidos e diferenciados pelo identificador.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de produtos afetados por esta história.

### RN-005 — Regra aprovada 5

**Descrição:**  
Somente DONO cadastra, edita, desativa e reativa.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de produtos afetados por esta história.

### RN-006 — Regra aprovada 6

**Descrição:**  
Produto desativado deixa de aparecer e oculta suas ofertas; o histórico é preservado.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de produtos afetados por esta história.

### RN-007 — Regra aprovada 7

**Descrição:**  
Reativação restaura ofertas ainda vigentes.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de produtos afetados por esta história.

### RN-008 — Regra aprovada 8

**Descrição:**  
Produtos podem ser reutilizados em qualquer campanha futura do mesmo supermercado.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de produtos afetados por esta história.

### RN-009 — Regra aprovada 9

**Descrição:**  
Pesquisa considera nome, marca e descrição, sem diferenciar caixa ou acentos.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de produtos afetados por esta história.

### RN-010 — Regra aprovada 10

**Descrição:**  
Filtro de categoria aceita uma ou mais categorias e pode ser combinado com pesquisa.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de produtos afetados por esta história.

### RN-011 — Regra aprovada 11

**Descrição:**  
Recomenda-se limite de 150 caracteres para nome e marca, 1.000 para descrição e 50 para apresentação; entradas são aparadas, mas o texto original é preservado.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de produtos afetados por esta história.

---

## 12. Requisitos funcionais

- **RF-001:** O sistema deve cadastrar uma imagem do produto.
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
  Quando solicitar "cadastrar uma imagem do produto"
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

Não aplicável — história exclusivamente de backend.

---

## 15. Contratos e integrações

Método: `POST`

Rota: `/api/produtos`

Request: objeto com os campos listados na seção 16.

Sucesso: `201 Created`, contendo identificador, dados persistidos e estado.

Erros aplicáveis: 400/422 para validação, 401, 403, 404 de dependência, 409 de conflito e 500.

Todos os contratos usam JSON UTF-8, datas ISO 8601 e identificadores opacos. Datas são persistidas em UTC e apresentadas em `America/Sao_Paulo`.

---

## 16. Dados

- nome: obrigatório, até 150 caracteres.
- categorias: uma ou mais, obrigatórias.
- imagem, marca, descrição, peso, unidade e volume: opcionais.
- estado: ATIVO ou DESATIVADO.
- Todos os registros pertencem a um supermercado, exceto recursos globais do Super Admin.
- Exclusões com histórico são lógicas; imagens desvinculadas podem ser removidas e liberar cota.
- Auditoria é retida por 24 meses; eventos brutos por 90 dias e agregados por 24 meses.

---

## 17. Estados e transições

ATIVO -> desativação -> DESATIVADO; DESATIVADO -> reativação -> ATIVO.

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

- `cadastrar_imagem_produto_iniciado`: início intencional do fluxo.
- `cadastrar_imagem_produto_concluido`: após confirmação do resultado.
- `cadastrar_imagem_produto_erro`: término sem conclusão, com categoria não sensível.
- Eventos contêm identificador único, contexto autorizado e instante UTC.
- Nenhum evento identifica diretamente o consumidor.

---

## 22. Dependências

- US-056.
- US-078.
- US-074.
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
- Item de origem: `SUP-RF-035 — O supermercado deve poder cadastrar uma imagem do produto.`
- Decisões aplicáveis: seção “Produtos” de `docs/historias/decisoes-mvp.md`.
- Histórias relacionadas: US-056, US-078, US-074.

