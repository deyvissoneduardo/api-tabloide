# US-115 — Definir a validade dos conteúdos

## Scrum

- **Prioridade:** 087
- **Story Points:** 3
- **Status de refinamento:** Pronta
- **Dependências:** Back/US-056
- **Bloqueia:** Nenhuma
- **Risco:** Baixo

### Scrum Poker

- Back end: 3
- QA: 3
- Scrum Master: 3

**Consenso:** 3 SP

### Justificativa

Estimativa relativa baseada no escopo descrito, nos cenários de validação e teste, no risco baixo e em 1 dependência(s) identificada(s).

---

## 1. Visão geral

A capacidade deve implementar definir a validade dos conteúdos usando as regras aprovadas de conteúdos promocionais. O problema resolvido é permitir um comportamento consistente, autorizado, testável e rastreável para responsável pelo produto. O resultado esperado é a conclusão observável da capacidade sem depender do documento de origem.

---

## 2. História de usuário

**Como** responsável pelo produto  
**Quero** definir a validade dos conteúdos  
**Para** manter a operação do SGTM atualizada e controlada

---

## 3. Contexto de negócio

Esta história pertence a **Supermercado**, domínio **Conteúdos promocionais**, seção “B20. Conteúdos promocionais”. O requisito de origem é SUP-RF-168. As decisões de 24/08/2026 têm precedência sobre interpretações anteriores e foram incorporadas abaixo.

---

## 4. Escopo

### 4.1 Incluído

- Executar definir a validade dos conteúdos no escopo autorizado.
- Aplicar as regras de conteúdos promocionais documentadas nesta história.
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

O gatilho ocorre quando responsável pelo produto solicita definir a validade dos conteúdos.

---

## 8. Fluxo principal

1. O ator inicia definir a validade dos conteúdos.
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
DONO gerencia mensagens promocionais, avisos e banners; OPERADOR não.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de conteúdos promocionais afetados por esta história.

### RN-002 — Regra aprovada 2

**Descrição:**  
Mensagem: título e texto obrigatórios.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de conteúdos promocionais afetados por esta história.

### RN-003 — Regra aprovada 3

**Descrição:**  
Aviso: título, texto e nível informativo obrigatório.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de conteúdos promocionais afetados por esta história.

### RN-004 — Regra aprovada 4

**Descrição:**  
Banner: título, imagem e destino interno ou URL HTTPS opcional.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de conteúdos promocionais afetados por esta história.

### RN-005 — Regra aprovada 5

**Descrição:**  
Todos possuem início e fim, lojas aplicáveis, estado e ordem.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de conteúdos promocionais afetados por esta história.

### RN-006 — Regra aprovada 6

**Descrição:**  
Estados: RASCUNHO, AGENDADO, VIGENTE, DESATIVADO e EXPIRADO.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de conteúdos promocionais afetados por esta história.

### RN-007 — Regra aprovada 7

**Descrição:**  
Ordenação usa posição inteira, com reordenação automática para evitar posições duplicadas.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de conteúdos promocionais afetados por esta história.

### RN-008 — Regra aprovada 8

**Descrição:**  
Conteúdo só aparece quando vigente e quando supermercado e loja estão disponíveis.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de conteúdos promocionais afetados por esta história.

### RN-009 — Regra aprovada 9

**Descrição:**  
Links externos abrem com proteção contra acesso ao contexto da página.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de conteúdos promocionais afetados por esta história.

---

## 12. Requisitos funcionais

- **RF-001:** O sistema deve definir a validade dos conteúdos.
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
  Quando solicitar "definir a validade dos conteúdos"
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

Método: `PATCH`

Rota: `/api/conteudos-promocionais/{id}`

Request: campos alteráveis e versão conhecida do recurso.

Sucesso: `200 OK`, com estado atualizado.

Erros aplicáveis: 400/422, 401, 403, 404, 409 e 500.

Todos os contratos usam JSON UTF-8, datas ISO 8601 e identificadores opacos. Datas são persistidas em UTC e apresentadas em `America/Sao_Paulo`.

---

## 16. Dados

- tipo: mensagem, aviso ou banner.
- título e conteúdo/imagem conforme tipo.
- início, fim, lojas e posição: obrigatórios.
- Todos os registros pertencem a um supermercado, exceto recursos globais do Super Admin.
- Exclusões com histórico são lógicas; imagens desvinculadas podem ser removidas e liberar cota.
- Auditoria é retida por 24 meses; eventos brutos por 90 dias e agregados por 24 meses.

---

## 17. Estados e transições

RASCUNHO -> publicação futura -> AGENDADO; AGENDADO -> início -> VIGENTE; VIGENTE -> fim -> EXPIRADO; VIGENTE -> desativação -> DESATIVADO.

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

- `definir_validade_conteudos_iniciado`: início intencional do fluxo.
- `definir_validade_conteudos_concluido`: após confirmação do resultado.
- `definir_validade_conteudos_erro`: término sem conclusão, com categoria não sensível.
- Eventos contêm identificador único, contexto autorizado e instante UTC.
- Nenhum evento identifica diretamente o consumidor.

---

## 22. Dependências

- US-056.
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
- Item de origem: `SUP-RF-168 — O supermercado deve poder definir a validade dos conteúdos.`
- Decisões aplicáveis: seção “Conteúdos promocionais” de `docs/historias/decisoes-mvp.md`.
- Histórias relacionadas: US-056.

