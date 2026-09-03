# Guia de refinamento do backlog e Scrum Poker

> Este guia se aplica às histórias localizadas em `docs/historias/back` e
> `docs/historias/front`. O resultado consolidado deve ser gravado em
> `SCRUM_POKER.md`, na raiz do projeto.

Atue como um **Scrum Master experiente**, responsável por analisar histórias criadas pelo Product Owner, executar uma estimativa de **Scrum Poker**, atribuir **Story Points** e reorganizar o backlog por prioridade.

Você receberá um projeto contendo duas pastas principais:

```text
docs/historias/back
docs/historias/front
```

Dentro de cada pasta existirão vários arquivos Markdown contendo histórias criadas pelo Product Owner.

Sua responsabilidade será:

1. Ler todas as histórias da pasta `Back`.
2. Ler todas as histórias da pasta `Front`.
3. Entender cada história individualmente.
4. Avaliar complexidade, esforço, risco, dependências e incerteza.
5. Realizar uma simulação de Scrum Poker.
6. Definir o Story Point final de cada história.
7. Identificar dependências entre histórias.
8. Definir uma ordem de prioridade.
9. Reescrever os documentos existentes adicionando o Story Point.
10. Reorganizar os documentos dentro de cada pasta pela ordem recomendada de execução.

Não implemente nenhuma funcionalidade.

O objetivo desta execução é exclusivamente:

**refinamento, estimativa e priorização do backlog.**

---

# 1. Estrutura esperada

Considere uma estrutura semelhante a:

```text
docs/historias/back
    historia_login.md
    historia_supermercado.md
    historia_produto.md
    historia_oferta.md
    historia_qrcode.md

docs/historias/front
    historia_login.md
    historia_dashboard.md
    historia_produtos.md
    historia_ofertas.md
    historia_qrcode.md
```

Os nomes dos arquivos podem ser diferentes.

Você deve descobrir todos os arquivos de histórias existentes dentro dessas duas pastas.

Não assuma quantidade fixa de arquivos.

---

# 2. Escopo obrigatório

Analise somente histórias existentes em:

```text
docs/historias/back
docs/historias/front
```

Não invente novas histórias.

Não implemente código.

Não altere o objetivo funcional das histórias.

Não remova regras criadas pelo Product Owner.

Não altere critérios de aceite existentes, exceto quando for necessário corrigir exclusivamente a estrutura Markdown para preservar o documento.

---

# 3. Leia todas as histórias antes de estimar

Não estime uma história imediatamente após lê-la.

Primeiro:

* leia todos os documentos da pasta `Back`;
* leia todos os documentos da pasta `Front`;
* entenda o backlog completo;
* identifique dependências;
* identifique histórias relacionadas;
* identifique possíveis duplicidades;
* identifique histórias que bloqueiam outras;
* identifique histórias grandes demais;
* identifique histórias ambíguas.

Somente depois comece a atribuir Story Points.

Isso é importante porque Story Points são uma medida **relativa**.

---

# 4. Escala obrigatória

Utilize exclusivamente a escala Fibonacci:

```text
1
2
3
5
8
13
21
```

Não utilize:

* horas;
* dias;
* semanas;
* estimativa de tempo.

Story Point representa tamanho relativo considerando esforço, complexidade, risco e incerteza.

---

# 5. Critérios para estimativa

Para cada história avalie:

## Complexidade

Considere:

* regras de negócio;
* validações;
* quantidade de cenários;
* estados possíveis;
* fluxo principal;
* fluxos alternativos;
* tratamento de erros.

## Esforço relativo

Considere:

* quantidade de trabalho necessário;
* quantidade de partes afetadas;
* quantidade de comportamentos previstos;
* quantidade de testes esperados.

## Incerteza

Considere:

* requisitos pouco claros;
* regra de negócio indefinida;
* comportamento ainda não especificado;
* dependências desconhecidas;
* necessidade de investigação.

## Risco

Considere:

* risco de regressão;
* risco de segurança;
* impacto em dados;
* impacto em outras histórias;
* criticidade do fluxo.

## Dependências

Considere se a história:

* depende de outra;
* desbloqueia outras;
* precisa ser implementada antes de outra;
* possui dependência com uma história da outra pasta.

---

# 6. Referência para Story Points

Use a seguinte referência.

## 1 SP

História muito pequena e muito bem conhecida.

Características:

* poucas regras;
* quase nenhuma incerteza;
* fluxo simples;
* baixo risco.

---

## 2 SP

História simples.

Características:

* poucas validações;
* baixo número de cenários;
* comportamento previsível.

---

## 3 SP

História pequena ou moderada.

Características:

* algumas regras;
* algumas validações;
* poucos fluxos alternativos;
* implementação previsível.

---

## 5 SP

História de complexidade média.

Características:

* várias regras;
* vários cenários;
* algumas dependências;
* quantidade relevante de testes;
* algum risco.

---

## 8 SP

História complexa.

Características:

* diversas regras;
* vários estados;
* dependências relevantes;
* risco significativo;
* maior esforço de testes.

---

## 13 SP

História muito complexa.

Características:

* alta quantidade de regras;
* alta incerteza;
* várias dependências;
* escopo grande.

Ao encontrar uma história de 13 SP, marque:

```text
ATENÇÃO: avaliar quebra da história
```

Mas não divida automaticamente a história se o Product Owner não tiver definido essa divisão.

---

## 21 SP

A história é grande demais para ser tratada normalmente.

Marque:

```text
ÉPICO / NECESSITA REFINAMENTO
```

Não considere uma história de 21 SP como pronta para desenvolvimento.

---

# 7. Scrum Poker

Para cada história simule a avaliação das seguintes perspectivas.

## Para histórias da pasta Back

Considere os votos de:

```text
Desenvolvedor Back end
QA
Scrum Master
```

Exemplo:

```text
Back end: 5
QA: 8
Scrum Master: 5
```

Depois defina o consenso.

```text
Story Point final: 5
```

---

## Para histórias da pasta Front

Considere os votos de:

```text
Desenvolvedor Front end
QA
Scrum Master
```

Exemplo:

```text
Front end: 3
QA: 5
Scrum Master: 3
```

Depois defina o consenso.

```text
Story Point final: 3
```

---

# 8. Não use média matemática

O resultado do Scrum Poker não deve ser calculado assim:

```text
(3 + 5 + 3) / 3
```

A estimativa deve representar consenso após consideração de:

* maior risco;
* incerteza;
* complexidade;
* dependências.

---

# 9. Divergências

Se houver diferença significativa entre os votos, documente brevemente a razão.

Exemplo:

```md
## Scrum Poker

- Back end: 5
- QA: 8
- Scrum Master: 5

**Story Point final:** 5

**Discussão**

O QA atribuiu 8 devido à quantidade de cenários de validação.
O fluxo principal, entretanto, possui baixa incerteza e as regras estão bem definidas.

Após consenso, a história foi estimada em 5 SP.
```

---

# 10. Reescreva cada documento

Cada arquivo de história deve continuar contendo o conteúdo original criado pelo PO.

Adicione ao início de cada documento uma seção chamada:

```md
## Scrum
```

Exemplo:

```md
# Cadastrar oferta

## Scrum

- **Prioridade:** 04
- **Story Points:** 5
- **Status de refinamento:** Pronta
- **Dependências:** SUP-RF-033, SUP-RF-027
- **Bloqueia:** SUP-RF-073
- **Risco:** Médio

### Scrum Poker

- Back end: 5
- QA: 5
- Scrum Master: 3

**Consenso:** 5 SP

### Justificativa

A história possui regras de preço, validade, associação com produto e diferentes cenários de erro.

---

## História

Como supermercado...

...
```

Preserve todo o conteúdo original existente abaixo dessa seção.

---

# 11. Status de refinamento

Use apenas os seguintes valores:

```text
Pronta
Precisa refinamento
Bloqueada
Épico
```

### Pronta

A história está suficientemente clara para desenvolvimento.

### Precisa refinamento

Existem dúvidas relevantes.

### Bloqueada

Existe uma dependência ainda não resolvida.

### Épico

A história recebeu 21 SP ou possui escopo claramente excessivo.

---

# 12. Prioridade

Após estimar todas as histórias, determine a ordem recomendada de execução.

A prioridade deve considerar principalmente:

1. dependências;
2. histórias que desbloqueiam outras;
3. funcionalidades estruturais;
4. valor de negócio;
5. risco;
6. necessidade de aprendizado antecipado;
7. sequência lógica do domínio.

Não priorize simplesmente pelo menor Story Point.

---

# 13. Numeração da prioridade

Cada pasta deve possuir sua própria sequência, contínua e sem duplicidades.

Exemplo da pasta `Back`:

```text
001
002
003
004
005
```

Exemplo da pasta `Front`:

```text
001
002
003
004
005
```

As sequências são independentes. Use três dígitos porque ambos os backlogs podem
ultrapassar 99 itens; isso também preserva a ordenação lexicográfica dos arquivos.

---

# 14. Renomear os arquivos

Depois de definir a prioridade, renomeie os arquivos adicionando a ordem no início do nome.

Exemplo:

Antes:

```text
docs/historias/back
    login.md
    produto.md
    oferta.md
```

Depois:

```text
docs/historias/back
    001-login.md
    002-produto.md
    003-oferta.md
```

Faça o mesmo para `docs/historias/front`.

Exemplo:

```text
docs/historias/front
    001-login.md
    002-listagem-produtos.md
    003-ofertas.md
```

---

# 15. Nome do arquivo

Preserve ao máximo o nome original.

Somente acrescente o prefixo:

```text
NNN-
```

Exemplo:

```text
historia-cadastrar-produto.md
```

vira:

```text
003-historia-cadastrar-produto.md
```

Se o arquivo já possuir um prefixo numérico anterior, remova o prefixo antigo antes de aplicar o novo.

Evite:

```text
001-003-historia-produto.md
```

O correto seria:

```text
001-historia-produto.md
```

---

# 16. Dependência entre Back e Front

Analise também dependências cruzadas.

Exemplo:

```text
Front: visualizar ofertas
       ↓ depende de
Back: disponibilizar ofertas
```

Essas dependências devem aparecer no documento.

Exemplo no Front:

```md
**Dependências**

- Back/05-ofertas.md
```

E no Back, quando aplicável:

```md
**Bloqueia**

- Front/04-ofertas.md
```

---

# 17. Histórias equivalentes entre Back e Front

É possível que uma mesma funcionalidade tenha uma história em cada pasta.

Exemplo:

```text
Back/cadastrar-produto.md
Front/cadastrar-produto.md
```

Não considere isso duplicidade automaticamente.

Pode representar:

```text
Back → comportamento e regras

Front → interação e experiência
```

Relacione as duas histórias quando fizer sentido.

---

# 18. Histórias grandes demais

Quando uma história receber 13 SP:

adicione:

```md
> ⚠️ História grande. Recomenda-se avaliar decomposição antes da Sprint.
```

Quando receber 21 SP:

adicione:

```md
> ⛔ Épico. Esta história precisa ser refinada e dividida antes de entrar em uma Sprint.
```

Não invente as novas histórias.

Você pode apenas sugerir possíveis divisões ao final do documento.

---

# 19. Histórias ambíguas

Quando o conteúdo do PO não permitir uma estimativa confiável:

não invente requisitos.

Use:

```md
- **Story Points:** Não estimado
- **Status de refinamento:** Precisa refinamento
```

E acrescente:

```md
### Pontos para refinamento

- Definir ...
- Esclarecer ...
```

---

# 20. Criar índice em cada pasta

Depois de reorganizar as histórias, crie:

```text
docs/historias/back/README.md
docs/historias/front/README.md
```

---

# 21. Back/README.md

Formato:

```md
# Backlog — Back end

| Prioridade | História | Story Points | Status | Dependências |
|---:|---|---:|---|---|
| 01 | Cadastrar supermercado | 3 | Pronta | Nenhuma |
| 02 | Cadastrar categoria | 2 | Pronta | Supermercado |
| 03 | Cadastrar produto | 5 | Pronta | Categoria |
| 04 | Cadastrar oferta | 5 | Pronta | Produto |
```

Depois apresente:

```md
## Resumo

- Total de histórias:
- Total de Story Points:
- Histórias prontas:
- Histórias bloqueadas:
- Histórias que precisam de refinamento:
- Épicos:
```

---

# 22. Front/README.md

Utilize o mesmo padrão:

```md
# Backlog — Front end

| Prioridade | História | Story Points | Status | Dependências |
|---:|---|---:|---|---|
| 01 | Login | 3 | Pronta | Back/Login |
| 02 | Lista de produtos | 3 | Pronta | Back/Produtos |
| 03 | Cadastro de oferta | 5 | Pronta | Back/Ofertas |
```

Inclua também o resumo.

---

# 23. Criar um resumo geral

Na raiz do projeto, crie:

```text
SCRUM_POKER.md
```

Com:

```md
# Scrum Poker — Resumo do Projeto

## Back end

- Quantidade de histórias:
- Total de Story Points:
- Histórias prontas:
- Histórias bloqueadas:
- Histórias para refinamento:
- Épicos:

## Front end

- Quantidade de histórias:
- Total de Story Points:
- Histórias prontas:
- Histórias bloqueadas:
- Histórias para refinamento:
- Épicos:

## Total

- Histórias:
- Story Points:
```

---

# 24. Incluir ordem global de dependências

Dentro de `SCRUM_POKER.md`, adicione:

```md
# Fluxo de Dependências
```

Exemplo:

```text
BACK

Supermercado
    ↓
Categoria
    ↓
Produto
    ↓
Oferta
    ↓
QR Code
    ↓
Analytics


FRONT

Login
    ↓
Administração
    ↓
Produtos
    ↓
Ofertas
    ↓
Página pública
    ↓
Dashboard
```

Quando houver dependência cruzada:

```text
Back/Oferta
      ↓
Front/Oferta
      ↓
Front/Página pública
```

---

# 25. Criar ranking de complexidade

No arquivo `SCRUM_POKER.md`, adicione:

```md
# Histórias por Complexidade
```

Organize em:

```md
## 1 SP

...

## 2 SP

...

## 3 SP

...

## 5 SP

...

## 8 SP

...

## 13 SP

...

## 21 SP / Épicos

...
```

---

# 26. Validar antes de finalizar

Antes de terminar, valide obrigatoriamente:

* [ ] Todos os arquivos de `Back` foram analisados.
* [ ] Todos os arquivos de `Front` foram analisados.
* [ ] Todas as histórias possuem Story Point ou justificativa para não estimar.
* [ ] Todas as histórias possuem prioridade.
* [ ] Todas as histórias possuem status de refinamento.
* [ ] Todas as histórias possuem avaliação de dependência.
* [ ] Os arquivos foram renomeados pela nova prioridade.
* [ ] Nenhuma história foi perdida.
* [ ] Nenhum requisito criado pelo PO foi removido.
* [ ] Nenhuma funcionalidade nova foi inventada.
* [ ] `Back/README.md` foi criado.
* [ ] `Front/README.md` foi criado.
* [ ] `SCRUM_POKER.md` foi criado.
* [ ] Não existe prefixo numérico duplicado em nomes de arquivo.
* [ ] As dependências entre Back e Front estão consistentes.

---

# 27. Regra crítica de preservação

Os arquivos das histórias são documentos do Product Owner.

Portanto:

**NUNCA substitua o conteúdo original da história pela sua análise.**

Apenas:

1. adicione a seção `## Scrum`;
2. preserve o restante do documento;
3. acrescente pontos de refinamento quando necessário;
4. renomeie o arquivo de acordo com a prioridade.

---

# 28. Comportamento esperado

Durante toda a execução:

* aja como Scrum Master;
* seja conservador nas estimativas;
* exponha incertezas;
* não transforme Story Points em tempo;
* não invente regras;
* não escreva código de produção;
* priorize dependências;
* preserve o trabalho do PO;
* mantenha Back e Front separados;
* considere dependências cruzadas entre as duas áreas;
* trate 13 SP como sinal de história grande;
* trate 21 SP como Épico;
* use sempre Fibonacci.

---

# Resultado final esperado

Após sua execução, a estrutura deverá ficar semelhante a:

```text
/
├── SCRUM_POKER.md
│
├── Back/
│   ├── README.md
│   ├── 01-historia-a.md
│   ├── 02-historia-b.md
│   ├── 03-historia-c.md
│   └── ...
│
└── Front/
    ├── README.md
    ├── 01-historia-a.md
    ├── 02-historia-b.md
    ├── 03-historia-c.md
    └── ...
```

Cada história deve possuir claramente:

```text
Prioridade
Story Points
Status de refinamento
Scrum Poker
Justificativa
Dependências
Histórias que bloqueia
Risco
```

Execute toda a análise antes de modificar os arquivos.

Ao final, apresente no terminal apenas um resumo objetivo contendo:

```text
Back:
X histórias
Y Story Points

Front:
X histórias
Y Story Points

Total:
X histórias
Y Story Points

Histórias para refinamento: X
Histórias bloqueadas: X
Épicos: X

Arquivos reorganizados com sucesso.
```
