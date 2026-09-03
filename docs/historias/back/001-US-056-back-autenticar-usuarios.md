# US-056 — Autenticar seus usuários

## Scrum

- **Prioridade:** 001
- **Story Points:** 8
- **Status de refinamento:** Pronta
- **Dependências:** Nenhuma
- **Bloqueia:** Back/US-057, Back/US-058, Back/US-059, Back/US-060, Back/US-061, Back/US-062, Back/US-063, Back/US-064, Back/US-065, Back/US-066, Back/US-067, Back/US-068, Back/US-069, Back/US-070, Back/US-071, Back/US-072, Back/US-073, Back/US-074, Back/US-075, Back/US-076, Back/US-077, Back/US-078, Back/US-079, Back/US-080, Back/US-081, Back/US-082, Back/US-083, Back/US-084, Back/US-085, Back/US-086, Back/US-087, Back/US-088, Back/US-089, Back/US-090, Back/US-091, Back/US-092, Back/US-093, Back/US-094, Back/US-095, Back/US-096, Back/US-097, Back/US-098, Back/US-099, Back/US-100, Back/US-102, Back/US-103, Back/US-104, Back/US-105, Back/US-106, Back/US-107, Back/US-109, Back/US-110, Back/US-111, Back/US-112, Back/US-113, Back/US-114, Back/US-115, Back/US-116, Back/US-117, Front/US-118, Front/US-119, Front/US-120, Front/US-121, Front/US-122, Front/US-123, Front/US-124, Front/US-125, Front/US-126, Front/US-140, Front/US-141, Front/US-142, Front/US-143, Front/US-144, Front/US-145, Front/US-146, Front/US-147, Front/US-148, Front/US-149, Front/US-150, Front/US-151, Front/US-152, Front/US-153, Front/US-154, Front/US-155, Front/US-156, Front/US-157, Front/US-158, Front/US-159, Front/US-160, Front/US-164, Front/US-165, Front/US-166, Front/US-167, Back/US-170, Back/US-173, Back/US-174, Back/US-175, Back/US-176, Back/US-177, Back/US-178, Back/US-185, Back/US-186, Back/US-187, Back/US-188, Back/US-189, Front/US-190, Front/US-191, Front/US-192, Front/US-193, Front/US-194, Front/US-195, Front/US-196, Front/US-197, Front/US-198, Front/US-199, Front/US-200, Front/US-201, Front/US-202, Front/US-203, Front/US-204, Front/US-205, Front/US-206, Front/US-207, Back/US-208, Back/US-209, Back/US-210, Back/US-211, Back/US-212, Front/US-213, Back/US-214, Back/US-215, Back/US-216, Front/US-217, Back/US-218, Front/US-219, Back/US-220, Front/US-221, Back/US-222, Front/US-223, Front/US-224, Front/US-225, Front/US-226
- **Risco:** Alto

### Scrum Poker

- Back end: 8
- QA: 13
- Scrum Master: 5

**Consenso:** 8 SP

A divergência do QA considera cenários de exceção e regressão. O consenso preserva o tamanho relativo após ponderar escopo, risco e dependências.

### Justificativa

Estimativa relativa baseada no escopo descrito, nos cenários de validação e teste, no risco alto e em não possuir dependência funcional obrigatória identificada.

---

## 1. Visão geral

A capacidade deve implementar autenticar seus usuários usando as regras aprovadas de identidade e acesso. O problema resolvido é permitir um comportamento consistente, autorizado, testável e rastreável para usuário autorizado do supermercado. O resultado esperado é a conclusão observável da capacidade sem depender do documento de origem.

---

## 2. História de usuário

**Como** usuário autorizado do supermercado  
**Quero** autenticar seus usuários  
**Para** garantir a qualidade e a regra operacional aprovadas para o MVP

---

## 3. Contexto de negócio

Esta história pertence a **Supermercado**, domínio **Identidade e acesso**, seção “B1. Conta e acesso do supermercado”. O requisito de origem é SUP-RF-002. As decisões de 24/08/2026 têm precedência sobre interpretações anteriores e foram incorporadas abaixo.

---

## 4. Escopo

### 4.1 Incluído

- Executar autenticar seus usuários no escopo autorizado.
- Aplicar as regras de identidade e acesso documentadas nesta história.
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

O gatilho ocorre sempre que um fluxo afetado produz resposta ou altera estado.

---

## 8. Fluxo principal

1. O fluxo afetado é iniciado.
2. O sistema aplica a qualidade “O supermercado deve poder autenticar seus usuários” antes de devolver o resultado.
3. A execução produz evidência mensurável em teste, logs ou métricas.
4. Em falha, preserva dados e apresenta comportamento recuperável.
5. O resultado somente é considerado concluído quando a condição desta história for atendida.

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
Haverá um único usuário da plataforma: **Super Admin do SGTM**, com acesso total.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

### RN-002 — Regra aprovada 2

**Descrição:**  
A conta do Super Admin será criada e recuperada diretamente no banco de dados.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

### RN-003 — Regra aprovada 3

**Descrição:**  
O login utilizará e-mail e senha.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

### RN-004 — Regra aprovada 4

**Descrição:**  
Supermercados terão os perfis **DONO** e **OPERADOR**.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

### RN-005 — Regra aprovada 5

**Descrição:**  
DONO possui acesso integral ao próprio supermercado.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

### RN-006 — Regra aprovada 6

**Descrição:**  
OPERADOR pode somente cadastrar, editar, ativar, desativar e cancelar promoções; pode selecionar lojas, categorias e produtos existentes nesses fluxos.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

### RN-007 — Regra aprovada 7

**Descrição:**  
Somente DONO gerencia usuários, lojas, categorias e produtos.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

### RN-008 — Regra aprovada 8

**Descrição:**  
O Super Admin cria o primeiro DONO com senha provisória; a troca no primeiro acesso não é obrigatória.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

### RN-009 — Regra aprovada 9

**Descrição:**  
Não há limite de usuários por supermercado.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

### RN-010 — Regra aprovada 10

**Descrição:**  
E-mail é único em toda a plataforma.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

### RN-011 — Regra aprovada 11

**Descrição:**  
Senha: mínimo de 6 caracteres, ao menos uma maiúscula, uma minúscula e um número.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

### RN-012 — Regra aprovada 12

**Descrição:**  
Recuperação de DONO e OPERADOR ocorre por link de uso único, enviado por e-mail e válido por 15 minutos.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

### RN-013 — Regra aprovada 13

**Descrição:**  
Alterar ou recuperar senha encerra todas as sessões do usuário.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

### RN-014 — Regra aprovada 14

**Descrição:**  
Desativar usuário encerra suas sessões e bloqueia novos acessos.

**Motivação:**  
Garantir aderência às decisões consolidadas do MVP.

**Aplicação:**  
Nos fluxos de identidade e acesso afetados por esta história.

---

## 12. Requisitos funcionais

- **RF-001:** O sistema deve autenticar seus usuários.
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
  Quando solicitar "autenticar seus usuários"
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

A capacidade é transversal e deve ser validada nos endpoints afetados de `/api/sessoes`. Não cria endpoint exclusivo quando puder ser aplicada como requisito de qualidade.

Todos os contratos usam JSON UTF-8, datas ISO 8601 e identificadores opacos. Datas são persistidas em UTC e apresentadas em `America/Sao_Paulo`.

---

## 16. Dados

- e-mail: obrigatório, único globalmente e normalizado em minúsculas.
- senha: mínimo de 6 caracteres, com maiúscula, minúscula e número.
- perfil: DONO ou OPERADOR.
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

- `autenticar_usuarios_iniciado`: início intencional do fluxo.
- `autenticar_usuarios_concluido`: após confirmação do resultado.
- `autenticar_usuarios_erro`: término sem conclusão, com categoria não sensível.
- Eventos contêm identificador único, contexto autorizado e instante UTC.
- Nenhum evento identifica diretamente o consumidor.

---

## 22. Dependências

- Nenhuma história funcional obrigatória anterior foi identificada.
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
- Item de origem: `SUP-RF-002 — O supermercado deve poder autenticar seus usuários.`
- Decisões aplicáveis: seção “Identidade e acesso” de `docs/historias/decisoes-mvp.md`.
- Sem história anterior obrigatória identificada.

