**Guia UX/UI para criação das telas no Figma**

Tabloide Digital / Ofertas Inteligentes para Supermercados — consolidação de MVP, primeira versão e visão de produto

**Objetivo: permitir que um Analista/Designer Sênior transforme os requisitos em fluxos, wireframes, UI final, protótipo navegável e Design System sem depender de interpretação implícita.**

# Base documental

- MVP — requisitos selecionados: define o recorte que deve orientar a primeira entrega do produto.

- Primeira Versão — requisitos do produto: amplia o MVP com campanhas, personalização, lista, favoritos, WhatsApp, cupons, QR por setor, Retail Media e demais evoluções.

- Mapa Mental — visão estratégica: explica problema, proposta de valor, experiência do cliente, hipótese de uso do QR Code, analytics, posicionamento e roadmap.

# 1. Princípio de interpretação

Este documento separa requisitos explícitos dos arquivos-fonte e recomendações de design necessárias para transformá-los em interface. Quando algo estiver marcado como “recomendação de design”, trata-se de uma solução UX/UI proposta para materializar o requisito; não é um novo requisito de negócio.

- O MVP deve ser desenhado primeiro e identificado no Figma como escopo prioritário.

- As telas da Primeira Versão podem ser desenhadas em uma página/branch separada para não contaminar a validação do MVP.

- A página pública do consumidor deve ser mobile-first e não pode exigir login ou cadastro.

- Os painéis administrativos são produtos de gestão e devem favorecer clareza, busca, filtros, auditoria e prevenção de erros.

- O QR Code é permanente/dinâmico: a interface de gestão não deve sugerir que o usuário precisa gerar um novo QR a cada campanha.

# 2. Atores e contextos de uso

| **Ator**                           | **Contexto**                                          | **Objetivo principal**                                               | **Prioridade de UX**                                         |
|------------------------------------|-------------------------------------------------------|----------------------------------------------------------------------|--------------------------------------------------------------|
| Administrador da plataforma        | Backoffice do dono do SaaS                            | Operar supermercados, planos, limites, suporte, métricas e auditoria | Eficiência, visão consolidada, segurança                     |
| Administrador do supermercado/rede | Painel administrativo B2B                             | Gerenciar lojas, produtos, ofertas, QR Codes, conteúdo e métricas    | Operação simples, reutilização, baixa dependência de suporte |
| Consumidor                         | Celular, muitas vezes dentro da loja após escanear QR | Encontrar rapidamente ofertas relevantes                             | Velocidade, zero cadastro, preço e validade evidentes        |

# 3. Arquitetura do produto no Figma

- 00 — Capa / índice / legenda de escopo.

- 01 — Foundations / Design Tokens.

- 02 — Components / variantes / estados.

- 03 — Admin Plataforma — MVP.

- 04 — Supermercado — MVP.

- 05 — Consumidor — MVP (mobile-first).

- 06 — Protótipos / fluxos do MVP.

- 07 — Primeira Versão — expansões futuras.

- 08 — Especificações / handoff / anotações.

Recomendação de design: usar prefixos consistentes nos frames, por exemplo “ADM-”, “SUP-” e “PUB-”, e um badge visual “MVP” ou “V1” no título de cada frame.

# 4. Navegação macro recomendada

| **Superfície**   | **Navegação sugerida**                                                                                                        | **Justificativa**                                            |
|------------------|-------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------|
| Admin Plataforma | Dashboard; Supermercados; Planos; Auditoria; Ocorrências/Suporte; Conteúdos; Relatórios                                       | Agrupa os requisitos administrativos por tarefa operacional. |
| Supermercado     | Dashboard; Lojas; Produtos; Categorias; Ofertas; QR Codes; Tabloide; Conteúdos; Analytics; Usuários/Permissões; Configurações | Prioriza o fluxo operacional diário e o acesso às métricas.  |
| Página pública   | Home/ofertas; categorias; busca; detalhe; compartilhamento; tabloide                                                          | Mantém a experiência direta, sem exigir navegação complexa.  |

# 5. Fluxos críticos do MVP

1.  Administrador da plataforma: login → dashboard → localizar supermercado → abrir detalhe → alterar status/plano/limite → confirmar ação crítica → visualizar resultado/auditoria.

2.  Supermercado: login → dashboard → criar/selecionar produto → criar oferta → definir preço, validade e lojas → ativar/publicar → verificar oferta na página pública.

3.  Supermercado: QR Codes → gerar/visualizar QR por loja → identificar QR → ativar/desativar → copiar/baixar para material físico → acompanhar scans.

4.  Consumidor: escanear QR → abrir página pública da loja → ver ofertas vigentes → filtrar/pesquisar → abrir detalhe → compartilhar.

5.  Supermercado: dashboard/analytics → selecionar período → consultar scans, acessos, categorias/produtos/QRs → comparar lojas/campanhas quando disponível no escopo.

6.  Supermercado: disponibilizar tabloide existente → informar validade → consumidor visualizar apenas quando vigente.

# 6. Inventário de telas — MVP

| **Código** | **Área**     | **Tela/Frame**                   | **Obrigatória no MVP** |
|------------|--------------|----------------------------------|------------------------|
| ADM-01     | Admin        | Login                            | Sim                    |
| ADM-02     | Admin        | Recuperar acesso                 | Sim                    |
| ADM-03     | Admin        | Dashboard geral                  | Sim                    |
| ADM-04     | Admin        | Lista de supermercados           | Sim                    |
| ADM-05     | Admin        | Detalhe do supermercado          | Sim                    |
| ADM-06     | Admin        | Criar/editar supermercado        | Sim                    |
| ADM-07     | Admin        | Plano, limites e funcionalidades | Sim                    |
| ADM-08     | Admin        | Auditoria                        | Sim                    |
| ADM-09     | Admin        | Ocorrências / suporte            | Sim                    |
| ADM-10     | Admin        | Relatórios gerais                | Sim                    |
| SUP-01     | Supermercado | Login                            | Sim                    |
| SUP-02     | Supermercado | Recuperar acesso                 | Sim                    |
| SUP-03     | Supermercado | Dashboard                        | Sim                    |
| SUP-04     | Supermercado | Perfil/dados do supermercado     | Sim                    |
| SUP-05     | Supermercado | Usuários e permissões            | Sim                    |
| SUP-06     | Supermercado | Lista de lojas                   | Sim                    |
| SUP-07     | Supermercado | Criar/editar loja                | Sim                    |
| SUP-08     | Supermercado | Categorias                       | Sim                    |
| SUP-09     | Supermercado | Produtos                         | Sim                    |
| SUP-10     | Supermercado | Criar/editar produto             | Sim                    |
| SUP-11     | Supermercado | Ofertas                          | Sim                    |
| SUP-12     | Supermercado | Criar/editar oferta              | Sim                    |
| SUP-13     | Supermercado | QR Codes                         | Sim                    |
| SUP-14     | Supermercado | Detalhe/geração de QR Code       | Sim                    |
| SUP-15     | Supermercado | Analytics                        | Sim                    |
| SUP-16     | Supermercado | Tabloide existente               | Sim                    |
| SUP-17     | Supermercado | Conteúdos promocionais           | Sim                    |
| PUB-01     | Consumidor   | Página pública / ofertas         | Sim                    |
| PUB-02     | Consumidor   | Resultado de busca/filtro        | Sim                    |
| PUB-03     | Consumidor   | Detalhe da oferta                | Sim                    |
| PUB-04     | Consumidor   | Tabloide vigente                 | Sim                    |
| PUB-05     | Consumidor   | Sem ofertas vigentes             | Sim                    |

# 7. Especificação das telas — Admin da plataforma

ADM-01 — Login

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador da plataforma</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Permitir acesso seguro à área administrativa.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Marca/nome da plataforma<br />
• E-mail/usuário<br />
• Senha<br />
• Ação de entrar<br />
• Link de recuperação de acesso</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Entrar<br />
• Ir para recuperação de acesso</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Inicial<br />
• Campos preenchidos<br />
• Validação de campo<br />
• Credenciais inválidas<br />
• Carregando<br />
• Erro técnico</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• Não expor informações de supermercados antes da autenticação.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>ADM-RF-001, ADM-RF-002, ADM-RF-004, ADM-RNF-001</td>
</tr>
</tbody>
</table>

ADM-03 — Dashboard geral

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador da plataforma</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Fornecer visão consolidada e compreensível da operação.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Total de supermercados<br />
• Ativos<br />
• Inativos<br />
• Total de lojas<br />
• Campanhas<br />
• Ofertas<br />
• Acessos públicos<br />
• Scans de QR<br />
• Filtro por período<br />
• Indicadores de maior/menor utilização</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Alterar período<br />
• Abrir lista filtrada<br />
• Acessar detalhes/relatórios</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Carregando/skeleton<br />
• Com dados<br />
• Sem dados no período<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• Evitar jargão técnico; cards devem ter título, valor e contexto. Gráficos precisam de legenda e não podem depender apenas de cor.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>ADM-RF-020..031, ADM-RNF-007, ADM-RNF-008</td>
</tr>
</tbody>
</table>

ADM-04 — Lista de supermercados

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador da plataforma</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Localizar e operar contas de supermercados com rapidez.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Busca<br />
• Filtros de status/plano<br />
• Tabela/lista<br />
• Nome<br />
• Lojas<br />
• Plano<br />
• Status<br />
• Uso/atividade<br />
• Ações</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Buscar<br />
• Filtrar<br />
• Abrir detalhe<br />
• Cadastrar supermercado<br />
• Ativar/desativar/bloquear conforme permissão</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Lista populada<br />
• Busca sem resultado<br />
• Sem supermercados<br />
• Carregando<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• A busca deve ser dominante. Ações destrutivas não devem ficar expostas como clique primário.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>ADM-RF-005, ADM-RF-007..015, ADM-RNF-004, ADM-RNF-005, ADM-RNF-009</td>
</tr>
</tbody>
</table>

ADM-05 — Detalhe do supermercado

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador da plataforma</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Concentrar dados, lojas, usuários, status, assinatura, limites, suporte e histórico.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Dados cadastrais<br />
• Status<br />
• Plano/assinatura<br />
• Limites<br />
• Lojas<br />
• Usuários administrativos<br />
• Métricas resumidas<br />
• Auditoria<br />
• Informações de suporte</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Editar<br />
• Alterar plano<br />
• Ativar/desativar<br />
• Bloquear/reativar<br />
• Abrir auditoria</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Ativo<br />
• Inativo<br />
• Bloqueado<br />
• Carregando<br />
• Erro<br />
• Sem lojas/usuários</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• Usar confirmação explícita para bloquear/desativar e explicar consequência antes da confirmação.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>ADM-RF-006, ADM-RF-011..019, ADM-RF-032..034, ADM-RNF-002, ADM-RNF-003, ADM-RNF-009</td>
</tr>
</tbody>
</table>

ADM-07 — Planos, limites e funcionalidades

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador da plataforma</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Administrar o que cada plano permite e os limites de uso.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Lista de planos<br />
• Funcionalidades por plano<br />
• Limites<br />
• Supermercados com exceções/funcionalidades liberadas</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Criar/editar configuração visual do plano<br />
• Associar/alterar plano de supermercado<br />
• Liberar funcionalidade específica</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Com dados<br />
• Sem configuração<br />
• Edição<br />
• Confirmação<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• Mostrar claramente impacto da alteração e, quando aplicável, quantidade de contas afetadas.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>ADM-RF-016, ADM-RF-017, ADM-RF-036..038</td>
</tr>
</tbody>
</table>

ADM-08 — Auditoria

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador da plataforma</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Permitir rastrear alterações administrativas relevantes.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Data/hora<br />
• Responsável<br />
• Supermercado<br />
• Ação<br />
• Objeto alterado<br />
• Resumo da alteração<br />
• Filtros</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Filtrar<br />
• Pesquisar<br />
• Abrir detalhe do registro</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Com registros<br />
• Sem registros<br />
• Carregando<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• Priorizar legibilidade e rastreabilidade; histórico não deve sugerir edição.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>ADM-RF-032, ADM-RF-033, ADM-RNF-002, ADM-RNF-003, ADM-RNF-013</td>
</tr>
</tbody>
</table>

# 8. Especificação das telas — Supermercado

SUP-03 — Dashboard do supermercado

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador do supermercado/rede</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Apresentar visão operacional resumida da performance.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Acessos<br />
• Scans<br />
• Ofertas mais vistas<br />
• Categorias mais vistas<br />
• QR Codes mais utilizados<br />
• Filtro por período<br />
• Comparação de períodos<br />
• Métricas por loja</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Alterar período<br />
• Comparar período<br />
• Abrir analytics<br />
• Trocar loja/visão consolidada</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Com dados<br />
• Sem dados<br />
• Carregando<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• Se a conta possuir várias lojas, deixar explícito se os números são consolidados ou de uma loja específica.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>SUP-RF-117..125, SUP-RF-171, SUP-RF-175, SUP-RF-176</td>
</tr>
</tbody>
</table>

SUP-06 — Lojas

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador do supermercado/rede</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Visualizar, cadastrar e administrar lojas.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Lista de lojas<br />
• Identificação<br />
• Endereço<br />
• Status<br />
• Métricas resumidas quando disponíveis</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Criar loja<br />
• Editar<br />
• Ativar/desativar<br />
• Abrir métricas da loja</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Com lojas<br />
• Sem lojas<br />
• Ativa<br />
• Inativa<br />
• Carregando<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• Para redes, oferecer seletor de loja consistente em todo o painel.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>SUP-RF-019..026, SUP-RF-170, SUP-RF-171</td>
</tr>
</tbody>
</table>

SUP-08 — Categorias

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador do supermercado</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Gerenciar a taxonomia usada para organizar produtos e ofertas públicas.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Nome da categoria<br />
• Status<br />
• Quantidade de produtos</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Criar<br />
• Editar<br />
• Desativar<br />
• Pesquisar/ordenar lista</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Com categorias<br />
• Sem categorias<br />
• Categoria inativa<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• A estrutura precisa ser simples porque categorias são um eixo principal de navegação do consumidor.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>SUP-RF-027..031</td>
</tr>
</tbody>
</table>

SUP-09 — Produtos

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador do supermercado</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Manter catálogo reutilizável para criação rápida de ofertas.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Busca<br />
• Filtro por categoria<br />
• Imagem<br />
• Nome<br />
• Marca<br />
• Categoria<br />
• Peso/unidade/volume<br />
• Status</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Cadastrar produto<br />
• Editar<br />
• Desativar<br />
• Abrir detalhe<br />
• Usar produto em oferta</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Com produtos<br />
• Sem produtos<br />
• Busca vazia<br />
• Carregando<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• O catálogo não deve misturar preço promocional como atributo permanente do produto; preço pertence à oferta.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>SUP-RF-033..044, SUP-RNF-037</td>
</tr>
</tbody>
</table>

SUP-12 — Criar/editar oferta

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador do supermercado</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Permitir cadastrar uma promoção de forma simples e com baixa chance de erro.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Produto<br />
• Preço normal<br />
• Preço promocional<br />
• Percentual de desconto calculado/apresentado<br />
• Data inicial<br />
• Data final<br />
• Condições/observações<br />
• Lojas<br />
• Status</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Selecionar produto<br />
• Salvar<br />
• Ativar/desativar<br />
• Cancelar<br />
• Copiar oferta</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Vazia<br />
• Preenchida<br />
• Validações<br />
• Preço inválido<br />
• Datas inválidas<br />
• Salvando<br />
• Sucesso<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• Recomendação: formulário em uma única página ou passos curtos, com resumo/preview do card público antes de salvar. Não ocultar validade.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>SUP-RF-045..060, SUP-RNF-009, SUP-RNF-010, SUP-RNF-018</td>
</tr>
</tbody>
</table>

SUP-13 — QR Codes

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador do supermercado</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Gerenciar QR Codes permanentes por loja e acompanhar origem dos acessos.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Identificação do QR<br />
• Loja<br />
• Status<br />
• Destino/página pública<br />
• Scans<br />
• Data de criação/último uso quando disponível</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Gerar/obter QR<br />
• Abrir detalhe<br />
• Ativar/desativar<br />
• Copiar identificação/link</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Com QRs<br />
• Sem QRs<br />
• Ativo<br />
• Inativo<br />
• Carregando<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• A interface deve reforçar: o QR continua válido mesmo quando campanhas/ofertas mudam.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>SUP-RF-088..097, SUP-RNF-017, SUP-RNF-039</td>
</tr>
</tbody>
</table>

SUP-15 — Analytics

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador do supermercado/rede</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Transformar scans e navegação em informação compreensível para decisão.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Filtro de período<br />
• Scans<br />
• Acessos por data/hora<br />
• Categorias mais acessadas<br />
• Produtos mais acessados<br />
• QR Codes mais utilizados<br />
• Métricas por loja/produto<br />
• Comparação de campanhas/lojas<br />
• Ranking de ofertas</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Selecionar período<br />
• Trocar loja<br />
• Comparar<br />
• Ordenar ranking</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Com dados<br />
• Sem dados<br />
• Período sem eventos<br />
• Carregando<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• Usar gráficos somente quando facilitarem comparação. Rankings podem ser tabelas/barras. Sempre exibir valor numérico.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>SUP-RF-104..116, SUP-RNF-020, SUP-RNF-031</td>
</tr>
</tbody>
</table>

SUP-16 — Tabloide existente

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Administrador do supermercado</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Permitir publicar um tabloide já existente e controlar sua validade.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Arquivo/imagem/PDF representando o tabloide<br />
• Validade<br />
• Status atual/expirado</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Adicionar/substituir tabloide<br />
• Definir validade<br />
• Remover/desativar</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Sem tabloide<br />
• Vigente<br />
• Expirado<br />
• Upload/carregando<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• Os documentos-fonte não especificam formato de upload. O design deve deixar o componente de upload genérico até definição técnica.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>SUP-RF-160, SUP-RF-162, SUP-RF-164, SUP-RF-163</td>
</tr>
</tbody>
</table>

# 9. Especificação das telas — Consumidor

PUB-01 — Página pública de ofertas

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Consumidor</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Exibir ofertas vigentes imediatamente após o acesso por QR/link, sem login e sem cadastro.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Identificação clara do supermercado/loja<br />
• Busca<br />
• Categorias<br />
• Ofertas vigentes<br />
• Imagem quando disponível<br />
• Preço normal<br />
• Preço promocional em destaque<br />
• Desconto quando aplicável<br />
• Validade<br />
• Condições essenciais<br />
• Acesso ao tabloide vigente quando houver</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Pesquisar<br />
• Filtrar categoria<br />
• Abrir detalhe<br />
• Compartilhar página/oferta<br />
• Abrir tabloide</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Carregando rápido/skeleton<br />
• Com ofertas<br />
• Sem ofertas<br />
• Busca sem resultado<br />
• Imagem indisponível<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• Mobile-first. Informação essencial deve aparecer mesmo se imagem falhar. Não exigir onboarding. Não pedir dados pessoais para visualizar ofertas.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>SUP-RF-073..087, SUP-RF-138..141, SUP-RF-163, SUP-RNF-001..007, SUP-RNF-011..013, SUP-RNF-026</td>
</tr>
</tbody>
</table>

PUB-03 — Detalhe da oferta

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Consumidor</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Apresentar informações completas de uma promoção e permitir compartilhamento.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Imagem<br />
• Produto<br />
• Marca/peso quando disponível<br />
• Preço normal<br />
• Preço promocional<br />
• Percentual de desconto<br />
• Validade<br />
• Condições/observações<br />
• Supermercado/loja</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Voltar<br />
• Compartilhar</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Oferta vigente<br />
• Oferta indisponível/expirada<br />
• Imagem indisponível<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• Preço promocional deve ser o elemento visual dominante; validade precisa estar próxima do preço/CTA informativo.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>SUP-RF-049, SUP-RF-082..086, SUP-RF-138, SUP-RF-141</td>
</tr>
</tbody>
</table>

PUB-05 — Sem ofertas vigentes

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Campo</strong></th>
<th><strong>Especificação</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Ator</td>
<td>Consumidor</td>
</tr>
<tr class="even">
<td>Objetivo da tela</td>
<td>Explicar claramente quando a loja não possui ofertas atuais.</td>
</tr>
<tr class="odd">
<td>Conteúdo obrigatório</td>
<td>• Identificação da loja<br />
• Mensagem de ausência de ofertas<br />
• Possível acesso ao tabloide vigente se existir</td>
</tr>
<tr class="even">
<td>Ações</td>
<td>• Voltar/limpar filtros<br />
• Abrir tabloide quando disponível</td>
</tr>
<tr class="odd">
<td>Estados que o Figma deve desenhar</td>
<td>• Sem ofertas gerais<br />
• Filtro sem resultado<br />
• Erro</td>
</tr>
<tr class="even">
<td>Observações UX/UI</td>
<td>• Não usar tela vazia genérica; diferenciar “não há ofertas vigentes” de “sua busca não encontrou resultados”.</td>
</tr>
<tr class="odd">
<td>Rastreabilidade</td>
<td>SUP-RF-087, SUP-RNF-018</td>
</tr>
</tbody>
</table>

# 10. Primeira Versão — telas adicionais a preparar fora do MVP

| **Módulo**               | **Telas/estados adicionais**                                                              | **Origem**                              |
|--------------------------|-------------------------------------------------------------------------------------------|-----------------------------------------|
| Campanhas                | Lista; criar/editar; associar ofertas/lojas; agendar; publicar; retirar; duplicar; prévia | SUP-RF-061..072                         |
| Personalização           | Identidade visual e informações públicas do supermercado                                  | SUP-RF-017, SUP-RF-018                  |
| QR por setor             | Criar QR de setor; destino categoria/campanha; comparação de desempenho                   | SUP-RF-091, SUP-RF-093, SUP-RF-098..102 |
| Lista de compras         | Adicionar/remover; lista; quantidade; estimativa; limpar; compartilhar                    | SUP-RF-126..133                         |
| Favoritos                | Favoritar; consultar; remover; mensuração                                                 | SUP-RF-134..137                         |
| WhatsApp                 | Opt-in; explicação do conteúdo; consentimento; cancelamento                               | SUP-RF-142..148                         |
| Cupons                   | Administração; validade; condições; consulta do consumidor; expirado                      | SUP-RF-149..155                         |
| Oferta digital exclusiva | Criação, identificação visual e métricas                                                  | SUP-RF-156..159                         |
| Retail Media             | Conteúdo patrocinado; identificação; período; métricas e desempenho                       | SUP-RF-177..182                         |

# 11. Design System mínimo exigido

## Foundations

- Cores: brand, neutros, superfície, borda, texto, success, warning, error, info e estados disabled.

- Cores semânticas: preço promocional/destaque não deve depender apenas da cor para transmitir significado.

- Tipografia: escala para display, títulos, subtítulos, body, label e caption; verificar leitura em celular.

- Spacing: escala consistente (ex.: múltiplos de 4 ou 8) aplicada a layout e componentes.

- Raios, bordas, elevação/sombra, iconografia e grid responsivo.

- Breakpoints documentados para mobile, tablet e desktop; o painel administrativo deve ser responsivo, mas a página pública deve nascer no mobile.

## Componentes

- Button: primary, secondary, tertiary/text, destructive; tamanhos; loading; disabled.

- Text field, password, search, textarea, date/date range, currency/price, select, multiselect.

- Checkbox, radio, switch/toggle, chip/filter chip, badge/status.

- Navigation: sidebar desktop, header/topbar, breadcrumb quando necessário, seletor de loja.

- Table/data grid com paginação, busca, filtros, ordenação, row actions e empty state.

- Cards de métricas e cards de oferta/produto.

- Tabs, accordion, modal/dialog, drawer, tooltip, dropdown/menu.

- Toast/snackbar/feedback, alert/banner, confirmação de ação crítica.

- Upload/image placeholder.

- Chart wrappers com título, legenda, valor, tooltip e estado sem dados.

- Skeleton/loading, empty state, error state.

- QR card: preview, identificação, loja, status e ações.

# 12. Regras visuais específicas do produto

- Preço promocional é a informação de maior peso visual no card da oferta.

- Preço normal, quando presente, deve ter hierarquia secundária e não competir com o promocional.

- Validade deve estar sempre visível sem exigir abertura do detalhe quando o espaço permitir.

- Desconto percentual pode ser badge, mas não pode substituir a exibição dos preços.

- Cards de oferta devem continuar compreensíveis sem imagem.

- Supermercado/loja deve estar claramente identificado na página pública.

- Status administrativos devem usar texto + elemento visual, não somente cor.

- Ações como bloquear, desativar, cancelar ou retirar publicação exigem confirmação contextual.

- Tabelas extensas devem permitir busca/filtros e permanecer utilizáveis com grande volume de dados.

# 13. Responsividade

| **Contexto**                    | **Comportamento esperado**                                                                                                                     |
|---------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| Página pública — mobile         | Prioridade máxima. Uma coluna ou grid compacto; busca e categorias acessíveis; preço legível; toque confortável; evitar menus complexos.       |
| Página pública — tablet/desktop | Aproveitar largura com grid maior sem transformar a experiência em um dashboard.                                                               |
| Painéis — desktop               | Sidebar + área de conteúdo; tabelas e dashboards com boa densidade de informação.                                                              |
| Painéis — tablet                | Sidebar recolhível; filtros adaptados; tabelas com prioridades de coluna.                                                                      |
| Painéis — mobile                | Permitir tarefas essenciais; tabelas podem virar cards/listas. Não é a superfície prioritária para operação intensiva, salvo definição futura. |

# 14. Acessibilidade e usabilidade

- Contraste adequado em texto, preço, badges, gráficos e controles.

- Informações importantes com texto/ícone além de cor.

- Tamanhos de fonte e line-height legíveis em telas pequenas.

- Estados de foco visíveis para navegação por teclado nos painéis web.

- Alvos de toque adequados na página pública mobile.

- Labels persistentes para formulários; placeholder não deve substituir label.

- Mensagens de erro próximas ao campo, em linguagem clara e orientada à correção.

- Hierarquia semântica consistente de títulos, seções e controles.

# 15. Estados obrigatórios a desenhar

| **Categoria** | **Estados**                                                     |
|---------------|-----------------------------------------------------------------|
| Dados         | loading/skeleton; sucesso; vazio; erro; atualização             |
| Busca/filtro  | sem filtro; filtro ativo; sem resultado; limpar filtros         |
| Formulário    | vazio; preenchido; inválido; salvando; sucesso; falha           |
| Entidades     | ativa; inativa; bloqueada; expirada; vigente, conforme domínio  |
| Ação crítica  | antes da ação; modal de confirmação; processando; sucesso; erro |
| Imagem        | carregando; disponível; indisponível/falha                      |
| Analytics     | com dados; sem eventos no período; erro; período comparado      |

# 16. Conteúdo e microcopy

- Usar linguagem do varejo e evitar termos técnicos internos.

- Botões devem indicar a ação: “Criar oferta”, “Salvar alterações”, “Desativar loja”, “Gerar QR Code”.

- Confirmações críticas devem dizer o objeto e a consequência: exemplo conceitual “Desativar esta loja? Ela deixará de operar normalmente na plataforma.”

- Empty states devem explicar causa e próxima ação possível.

- Na página pública, evitar textos longos: foco em produto, preço, validade e condição.

# 17. Protótipos navegáveis mínimos

7.  Fluxo Admin: login → dashboard → supermercado → alteração de status/plano → confirmação → auditoria.

8.  Fluxo Supermercado: login → produtos → novo produto → ofertas → nova oferta → ativar → página pública.

9.  Fluxo QR: QR Codes → gerar/visualizar QR por loja → ativar/desativar → analytics do QR.

10. Fluxo Consumidor: página pública → categoria → oferta → detalhe → compartilhar.

11. Fluxo Busca: página pública → buscar produto → resultado → sem resultado → limpar busca.

12. Fluxo Analytics: dashboard → período → analytics → troca de loja/visão consolidada.

# 18. Dados fictícios para os mocks

Recomendação de design: o Figma deve usar dados realistas e variados para validar hierarquia e casos extremos. Os nomes abaixo são apenas placeholders de design, não requisitos de negócio.

- Supermercados com nomes curtos e longos; contas ativas, inativas e bloqueadas.

- Rede com 1 loja e rede com várias lojas.

- Produtos com e sem imagem, nomes extensos, marcas, pesos e volumes diferentes.

- Ofertas com desconto baixo e alto, validade curta e longa, condições extensas.

- Dashboard com valores baixos, altos e períodos sem dados.

- QR Codes ativos e inativos em diferentes lojas.

# 19. Critérios de aceite do trabalho no Figma

- Todas as telas MVP do inventário estão desenhadas.

- Todos os fluxos críticos possuem protótipo navegável.

- Cada tela possui estados de loading, vazio, erro e sucesso quando aplicável.

- Ações críticas possuem confirmação.

- Página pública possui versão mobile prioritária e estados sem oferta/sem resultado.

- Design System contém foundations, componentes, variantes e estados usados nas telas.

- Componentes usados nas telas são instâncias do Design System, não cópias desconectadas.

- Frames estão identificados por ator, código e escopo MVP/V1.

- Filtros, tabelas, gráficos e formulários possuem comportamento anotado.

- Responsividade está demonstrada em pelo menos mobile e desktop nas superfícies relevantes.

- Acessibilidade básica está documentada: contraste, foco, tamanho de toque, informação não dependente de cor.

- Há rastreabilidade entre tela/fluxo e IDs de requisitos.

# 20. Pontos que os documentos não definem e não devem ser inventados silenciosamente

- Branding definitivo da plataforma: nome, logo, paleta, tipografia e identidade.

- Formato/tamanho máximo dos arquivos do tabloide e imagens de produto.

- Regras exatas de senha, sessão e recuperação de acesso.

- Campos cadastrais completos de endereço/contato e validações específicas.

- Modelo exato de planos, nomes, preços e limites comerciais.

- Quais permissões administrativas existem e sua matriz detalhada.

- Definição exata das métricas, fórmulas, eventos e unidades de comparação.

- Política final de retenção de auditoria e requisitos jurídicos específicos.

- Breakpoints oficiais e biblioteca tecnológica do front-end.

- Canal/mecanismo técnico de compartilhamento.

- Formato de download/exportação do QR Code.

O analista pode criar propostas de UX para esses pontos, mas deve marcá-las como hipótese/decisão de design pendente de validação, em vez de tratá-las como requisito aprovado.

# 21. Direção estratégica que deve influenciar o design

- O produto não deve parecer apenas um visualizador de PDF; a visão é uma plataforma de ofertas inteligentes.

- O benefício para o consumidor precisa ser evidente: encontrar promoções rapidamente.

- A hipótese central é se clientes realmente escanearão o QR Code; portanto, a experiência pós-scan precisa ter fricção mínima.

- Analytics é parte relevante da proposta de valor para o supermercado.

- A arquitetura visual deve comportar evolução para campanhas, lista de compras, favoritos, WhatsApp, cupons, multi-loja e Retail Media sem exigir refazer o produto inteiro.

# 22. Checklist final para o Analista/Designer Sênior

- Ler os três documentos-fonte e este guia antes de iniciar wireframes.

- Criar mapa de navegação por ator.

- Validar o inventário de telas do MVP.

- Criar wireframes low-fi dos fluxos críticos.

- Definir foundations e componentes essenciais.

- Aplicar UI high-fi ao MVP.

- Desenhar estados e edge cases.

- Montar protótipo navegável.

- Anotar regras, comportamento e responsividade.

- Fazer revisão de acessibilidade.

- Fazer revisão de consistência de componentes.

- Entregar handoff com rastreabilidade dos requisitos.

# Apêndice A — Matriz rápida de rastreabilidade por domínio

| **Domínio**                 | **IDs principais**                 |
|-----------------------------|------------------------------------|
| Admin / autenticação        | ADM-RF-001..004                    |
| Admin / supermercados       | ADM-RF-005..019                    |
| Admin / métricas            | ADM-RF-020..031, ADM-RF-040        |
| Admin / auditoria e suporte | ADM-RF-032..035                    |
| Admin / planos e conteúdo   | ADM-RF-036..039                    |
| Supermercado / acesso       | SUP-RF-001..010                    |
| Cadastro e lojas            | SUP-RF-011..026                    |
| Categorias e produtos       | SUP-RF-027..044                    |
| Ofertas                     | SUP-RF-045..060                    |
| Campanhas — V1              | SUP-RF-061..072                    |
| Página pública              | SUP-RF-073..087                    |
| QR Code                     | SUP-RF-088..102                    |
| Analytics e dashboard       | SUP-RF-103..125                    |
| Engajamento — V1            | SUP-RF-126..159                    |
| Tabloide/conteúdo           | SUP-RF-160..169                    |
| Multi-loja                  | SUP-RF-170..176                    |
| Retail Media — V1           | SUP-RF-177..182                    |
| RNF de interface            | SUP-RNF-001..014, SUP-RNF-032..035 |
