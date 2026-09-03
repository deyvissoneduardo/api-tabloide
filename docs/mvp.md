# MVP — Requisitos Selecionados

# Grupo A — Dono do sistema / Administrador da plataforma

## Requisitos Funcionais Back end

- [ ] ADM-RF-002 — O administrador deve poder autenticar-se no sistema.
- [ ] ADM-RF-007 — O administrador deve poder cadastrar um supermercado manualmente.
- [ ] ADM-RF-008 — O administrador deve poder editar os dados de um supermercado.
- [ ] ADM-RF-009 — O administrador deve poder ativar um supermercado.
- [ ] ADM-RF-010 — O administrador deve poder desativar um supermercado.
- [ ] ADM-RF-013 — O administrador deve poder bloquear o acesso de um supermercado à plataforma.
- [ ] ADM-RF-014 — O administrador deve poder reativar o acesso de um supermercado.
- [ ] ADM-RF-016 — O administrador deve poder associar um plano comercial a um supermercado.
- [ ] ADM-RF-017 — O administrador deve poder alterar o plano de um supermercado.
- [ ] ADM-RF-030 — O administrador deve poder identificar supermercados com maior utilização da plataforma.
- [ ] ADM-RF-031 — O administrador deve poder identificar supermercados inativos ou com baixa utilização.
- [ ] ADM-RF-033 — O administrador deve poder identificar qual usuário realizou determinada alteração administrativa.
- [ ] ADM-RF-035 — O administrador deve poder acompanhar problemas ou ocorrências registradas na plataforma.
- [ ] ADM-RF-036 — O administrador deve poder definir funcionalidades disponíveis por plano.
- [ ] ADM-RF-037 — O administrador deve poder definir limites de utilização por plano.
- [ ] ADM-RF-038 — O administrador deve poder disponibilizar funcionalidades para determinados supermercados.
- [ ] ADM-RF-039 — O administrador deve poder administrar conteúdos gerais da plataforma quando necessário.

## Requisitos Funcionais Front end

- [ ] ADM-RF-001 — O administrador da plataforma deve poder acessar uma área administrativa própria.
- [ ] ADM-RF-003 — O administrador deve poder encerrar sua sessão.
- [ ] ADM-RF-004 — O administrador deve poder recuperar seu acesso.
- [ ] ADM-RF-005 — O administrador deve poder visualizar todos os supermercados cadastrados.
- [ ] ADM-RF-006 — O administrador deve poder consultar os dados de um supermercado.
- [ ] ADM-RF-011 — O administrador deve poder visualizar as lojas vinculadas a cada supermercado.
- [ ] ADM-RF-012 — O administrador deve poder visualizar os usuários administrativos vinculados a cada supermercado.
- [ ] ADM-RF-015 — O administrador deve poder consultar o status de uma conta.
- [ ] ADM-RF-018 — O administrador deve poder consultar os limites do plano de cada supermercado.
- [ ] ADM-RF-019 — O administrador deve poder consultar o estado da assinatura de cada supermercado.
- [ ] ADM-RF-020 — O administrador deve poder visualizar a quantidade total de supermercados cadastrados.
- [ ] ADM-RF-021 — O administrador deve poder visualizar a quantidade de supermercados ativos.
- [ ] ADM-RF-022 — O administrador deve poder visualizar a quantidade de supermercados inativos.
- [ ] ADM-RF-023 — O administrador deve poder visualizar a quantidade total de lojas cadastradas.
- [ ] ADM-RF-024 — O administrador deve poder visualizar a quantidade total de campanhas criadas.
- [ ] ADM-RF-025 — O administrador deve poder visualizar a quantidade total de ofertas cadastradas.
- [ ] ADM-RF-026 — O administrador deve poder visualizar a quantidade total de acessos às páginas públicas.
- [ ] ADM-RF-027 — O administrador deve poder visualizar a quantidade total de leituras de QR Code.
- [ ] ADM-RF-028 — O administrador deve poder visualizar métricas gerais da plataforma.
- [ ] ADM-RF-029 — O administrador deve poder filtrar métricas por período.
- [ ] ADM-RF-032 — O administrador deve poder consultar registros de alterações relevantes.
- [ ] ADM-RF-034 — O administrador deve poder consultar informações necessárias para suporte ao supermercado.
- [ ] ADM-RF-040 — O administrador deve poder consultar relatórios gerais de utilização.

## Requisitos Não Funcionais Back end

- [ ] ADM-RNF-001 — Apenas usuários autorizados devem acessar a administração geral da plataforma.
- [ ] ADM-RNF-002 — As ações administrativas relevantes devem possuir rastreabilidade.
- [ ] ADM-RNF-003 — Alterações de planos, contas e permissões devem registrar responsável e data.
- [ ] ADM-RNF-005 — O painel administrativo deve continuar utilizável com grande quantidade de supermercados cadastrados.
- [ ] ADM-RNF-006 — Dados privados de supermercados não devem ser expostos publicamente.
- [ ] ADM-RNF-007 — O administrador deve possuir visão consolidada da operação.
- [ ] ADM-RNF-009 — Operações críticas devem reduzir o risco de execução acidental.
- [ ] ADM-RNF-010 — O sistema deve proteger contas de supermercados contra acesso indevido.
- [ ] ADM-RNF-011 — Informações de diferentes supermercados devem permanecer logicamente separadas.
- [ ] ADM-RNF-012 — O sistema deve permitir crescimento do número de supermercados sem alterar o modelo de administração.
- [ ] ADM-RNF-013 — Registros de auditoria devem ser preservados pelo período definido pela operação.
- [ ] ADM-RNF-014 — O acesso administrativo deve respeitar as regras de privacidade aplicáveis.
- [ ] ADM-RNF-015 — O sistema deve permitir suporte operacional sem comprometer a privacidade dos clientes dos supermercados.

## Requisitos Não Funcionais Front end

- [ ] ADM-RNF-004 — O administrador deve conseguir localizar um supermercado de forma simples.
- [ ] ADM-RNF-008 — As métricas gerais devem ser compreensíveis sem necessidade de conhecimento técnico.

---

# Grupo B — Supermercado

## Requisitos Funcionais Back end

### B1. Conta e acesso do supermercado

- [ ] SUP-RF-002 — O supermercado deve poder autenticar seus usuários.
- [ ] SUP-RF-005 — O supermercado deve poder alterar seus dados cadastrais.
- [ ] SUP-RF-006 — O supermercado deve poder alterar suas credenciais de acesso.
- [ ] SUP-RF-007 — O supermercado deve poder cadastrar mais de um usuário administrativo.
- [ ] SUP-RF-008 — O supermercado deve poder definir permissões diferentes para seus usuários.
- [ ] SUP-RF-009 — O supermercado deve poder ativar ou desativar seus usuários.
### B2. Cadastro do supermercado

- [ ] SUP-RF-011 — O supermercado deve poder cadastrar seu nome comercial.
- [ ] SUP-RF-012 — O supermercado deve poder cadastrar sua razão social quando necessária.
- [ ] SUP-RF-013 — O supermercado deve poder cadastrar sua logomarca.
- [ ] SUP-RF-014 — O supermercado deve poder cadastrar seus dados de contato.
- [ ] SUP-RF-015 — O supermercado deve poder cadastrar seu endereço.
- [ ] SUP-RF-016 — O supermercado deve poder editar seus dados.
### B3. Gestão de lojas

- [ ] SUP-RF-019 — O supermercado deve poder cadastrar uma ou mais lojas.
- [ ] SUP-RF-020 — O supermercado deve poder identificar cada loja individualmente.
- [ ] SUP-RF-021 — O supermercado deve poder cadastrar o endereço de cada loja.
- [ ] SUP-RF-022 — O supermercado deve poder ativar ou desativar uma loja.
- [ ] SUP-RF-024 — O supermercado deve poder administrar ofertas separadamente por loja.
- [ ] SUP-RF-025 — O supermercado deve poder compartilhar uma mesma campanha entre várias lojas.
### B4. Categorias

- [ ] SUP-RF-027 — O supermercado deve poder cadastrar categorias.
- [ ] SUP-RF-028 — O supermercado deve poder editar categorias.
- [ ] SUP-RF-029 — O supermercado deve poder desativar categorias.
- [ ] SUP-RF-031 — O supermercado deve poder associar produtos a categorias.
### B5. Produtos

- [ ] SUP-RF-033 — O supermercado deve poder cadastrar produtos.
- [ ] SUP-RF-034 — O supermercado deve poder informar o nome do produto.
- [ ] SUP-RF-035 — O supermercado deve poder cadastrar uma imagem do produto.
- [ ] SUP-RF-036 — O supermercado deve poder associar o produto a uma categoria.
- [ ] SUP-RF-037 — O supermercado deve poder informar a marca.
- [ ] SUP-RF-038 — O supermercado deve poder informar uma descrição.
- [ ] SUP-RF-039 — O supermercado deve poder informar peso, unidade ou volume.
- [ ] SUP-RF-040 — O supermercado deve poder editar produtos.
- [ ] SUP-RF-041 — O supermercado deve poder desativar produtos.
- [ ] SUP-RF-042 — O supermercado deve poder reutilizar produtos em futuras campanhas.
### B6. Ofertas

- [ ] SUP-RF-045 — O supermercado deve poder cadastrar uma oferta.
- [ ] SUP-RF-046 — O supermercado deve poder vincular um produto a uma oferta.
- [ ] SUP-RF-047 — O supermercado deve poder informar o preço normal.
- [ ] SUP-RF-048 — O supermercado deve poder informar o preço promocional.
- [ ] SUP-RF-050 — O supermercado deve poder informar a data inicial da oferta.
- [ ] SUP-RF-051 — O supermercado deve poder informar a data final da oferta.
- [ ] SUP-RF-052 — O supermercado deve poder editar uma oferta.
- [ ] SUP-RF-053 — O supermercado deve poder cancelar uma oferta.
- [ ] SUP-RF-054 — O supermercado deve poder ativar ou desativar uma oferta.
- [ ] SUP-RF-057 — O supermercado deve poder informar condições ou observações da oferta.
- [ ] SUP-RF-058 — O supermercado deve poder associar uma oferta a uma ou mais lojas.
- [ ] SUP-RF-059 — O supermercado deve poder copiar uma oferta existente.
- [ ] SUP-RF-060 — Ofertas vencidas devem deixar de ser apresentadas como vigentes.
### B8. Página pública do supermercado

- [ ] SUP-RF-078 — A página deve organizar ofertas por categoria.
### B9. QR Code

- [ ] SUP-RF-089 — O QR Code deve continuar válido após a troca de campanhas.
- [ ] SUP-RF-090 — O supermercado deve poder gerar QR Codes diferentes por loja.
- [ ] SUP-RF-092 — O supermercado deve poder identificar cada QR Code.
- [ ] SUP-RF-094 — O supermercado deve poder ativar ou desativar QR Codes.
- [ ] SUP-RF-096 — O sistema deve contabilizar acessos originados por QR Code.
- [ ] SUP-RF-097 — O supermercado deve poder identificar qual QR Code originou os acessos.
### B15. Compartilhamento

- [ ] SUP-RF-141 — O compartilhamento deve levar diretamente ao conteúdo correspondente.
### B19. Tabloide existente

- [ ] SUP-RF-160 — O supermercado deve poder disponibilizar um tabloide já existente.
- [ ] SUP-RF-162 — O supermercado deve poder informar a validade do tabloide.
- [ ] SUP-RF-164 — Tabloides expirados não devem aparecer como atuais.
### B20. Conteúdos promocionais

- [ ] SUP-RF-165 — O supermercado deve poder cadastrar mensagens promocionais.
- [ ] SUP-RF-166 — O supermercado deve poder publicar avisos.
- [ ] SUP-RF-167 — O supermercado deve poder cadastrar banners.
- [ ] SUP-RF-168 — O supermercado deve poder definir a validade dos conteúdos.
- [ ] SUP-RF-169 — O supermercado deve poder ordenar conteúdos promocionais.
### B21. Multi-loja / rede

- [ ] SUP-RF-170 — Uma rede deve poder administrar várias lojas em uma conta.

## Requisitos Funcionais Front end

### B1. Conta e acesso do supermercado

- [ ] SUP-RF-001 — O supermercado deve poder acessar sua área administrativa.
- [ ] SUP-RF-003 — O usuário deve poder encerrar sua sessão.
- [ ] SUP-RF-004 — O usuário deve poder recuperar seu acesso.
- [ ] SUP-RF-010 — O supermercado deve poder consultar quem realizou alterações importantes no seu supermercado.
### B3. Gestão de lojas

- [ ] SUP-RF-023 — O supermercado deve poder visualizar suas lojas.
- [ ] SUP-RF-026 — O supermercado deve poder visualizar métricas por loja.
### B5. Produtos

- [ ] SUP-RF-043 — O supermercado deve poder pesquisar produtos.
- [ ] SUP-RF-044 — O supermercado deve poder filtrar produtos por categoria.
### B6. Ofertas

- [ ] SUP-RF-049 — O sistema deve apresentar o percentual de desconto quando aplicável.
### B8. Página pública do supermercado

- [ ] SUP-RF-073 — Cada supermercado ou loja deve possuir uma página pública de ofertas.
- [ ] SUP-RF-074 — O consumidor deve conseguir acessar as ofertas sem login.
- [ ] SUP-RF-075 — O consumidor deve conseguir acessar as ofertas sem cadastro.
- [ ] SUP-RF-076 — A página deve identificar claramente o supermercado.
- [ ] SUP-RF-077 — A página deve apresentar as ofertas vigentes.
- [ ] SUP-RF-079 — O consumidor deve poder filtrar ofertas por categoria.
- [ ] SUP-RF-080 — O consumidor deve poder pesquisar produtos ou ofertas.
- [ ] SUP-RF-082 — A página deve apresentar preços.
- [ ] SUP-RF-083 — A página deve apresentar validade da promoção.
- [ ] SUP-RF-084 — A página deve apresentar imagem do produto quando disponível.
- [ ] SUP-RF-085 — A página deve apresentar condições da oferta.
- [ ] SUP-RF-086 — O consumidor deve poder visualizar detalhes da oferta.
- [ ] SUP-RF-087 — A página deve informar quando não houver ofertas vigentes.
### B9. QR Code

- [ ] SUP-RF-088 — O supermercado deve poder obter um QR Code para sua página pública.
- [ ] SUP-RF-095 — O supermercado deve poder utilizar o QR Code em materiais físicos e digitais.
### B11. Analytics

- [ ] SUP-RF-104 — O supermercado deve poder visualizar a quantidade de scans de QR Code.
- [ ] SUP-RF-105 — O supermercado deve poder visualizar acessos por data e horário.
- [ ] SUP-RF-107 — O supermercado deve poder visualizar categorias mais acessadas.
- [ ] SUP-RF-108 — O supermercado deve poder visualizar produtos mais acessados.
- [ ] SUP-RF-109 — O supermercado deve poder visualizar QR Codes mais utilizados.
- [ ] SUP-RF-110 — O supermercado deve poder selecionar um período de análise.
- [ ] SUP-RF-111 — O supermercado deve poder visualizar métricas por loja.
- [ ] SUP-RF-113 — O supermercado deve poder visualizar métricas por produto.
- [ ] SUP-RF-114 — O supermercado deve poder comparar campanhas.
- [ ] SUP-RF-115 — O supermercado deve poder comparar lojas.
- [ ] SUP-RF-116 — O supermercado deve poder visualizar ranking de ofertas.
### B12. Dashboard

- [ ] SUP-RF-117 — O supermercado deve possuir um painel resumido.
- [ ] SUP-RF-118 — O painel deve apresentar acessos.
- [ ] SUP-RF-119 — O painel deve apresentar scans.
- [ ] SUP-RF-120 — O painel deve apresentar ofertas mais visualizadas.
- [ ] SUP-RF-121 — O painel deve apresentar categorias mais visualizadas.
- [ ] SUP-RF-122 — O painel deve apresentar QR Codes mais utilizados.
- [ ] SUP-RF-124 — O painel deve permitir escolher período.
- [ ] SUP-RF-125 — O painel deve permitir comparar períodos.
### B15. Compartilhamento

- [ ] SUP-RF-138 — O consumidor deve poder compartilhar uma oferta.
- [ ] SUP-RF-139 — O consumidor deve poder compartilhar uma campanha.
- [ ] SUP-RF-140 — O consumidor deve poder compartilhar a página do supermercado.
### B19. Tabloide existente

- [ ] SUP-RF-163 — O consumidor deve poder visualizar o tabloide vigente.
### B21. Multi-loja / rede

- [ ] SUP-RF-171 — A rede deve poder visualizar todas as lojas.
- [ ] SUP-RF-175 — A rede deve poder visualizar métricas consolidadas.
- [ ] SUP-RF-176 — A rede deve poder visualizar métricas individuais de cada loja.

## Requisitos Não Funcionais Back end

### B21. Multi-loja / rede

- [ ] SUP-RNF-015 — A página pública deve estar disponível durante o funcionamento do supermercado.
- [ ] SUP-RNF-016 — Ofertas publicadas devem permanecer disponíveis durante sua validade.
- [ ] SUP-RNF-017 — O QR Code físico deve continuar válido após mudanças de campanhas.
- [ ] SUP-RNF-018 — Apenas ofertas vigentes devem ser apresentadas como atuais.
- [ ] SUP-RNF-019 — Alterações publicadas devem refletir corretamente para o consumidor.
- [ ] SUP-RNF-020 — As métricas devem representar os eventos registrados corretamente.
- [ ] SUP-RNF-021 — Somente usuários autorizados devem acessar a administração do supermercado.
- [ ] SUP-RNF-022 — Um supermercado não deve acessar os dados privados de outro.
- [ ] SUP-RNF-023 — Usuários devem respeitar suas permissões.
- [ ] SUP-RNF-024 — A página pública não deve expor informações administrativas.
- [ ] SUP-RNF-025 — Alterações importantes devem possuir rastreabilidade.
- [ ] SUP-RNF-026 — O consumidor deve poder consultar ofertas sem fornecer dados pessoais.
- [ ] SUP-RNF-027 — Dados pessoais devem ser coletados somente quando necessários.
- [ ] SUP-RNF-028 — A finalidade da coleta deve ser informada.
- [ ] SUP-RNF-029 — Consentimentos devem ser registrados quando necessários.
- [ ] SUP-RNF-030 — O consumidor deve poder revogar consentimentos.
- [ ] SUP-RNF-031 — Dados de analytics devem ser preferencialmente agregados quando identificação pessoal não for necessária.
- [ ] SUP-RNF-036 — O supermercado deve conseguir administrar suas ofertas sem depender constantemente do dono da plataforma.
- [ ] SUP-RNF-037 — Produtos cadastrados devem poder ser reutilizados.
- [ ] SUP-RNF-038 — Campanhas anteriores devem poder ser reaproveitadas.
- [ ] SUP-RNF-039 — Alterações de campanhas não devem exigir troca do QR Code.
- [ ] SUP-RNF-040 — Uma rede deve conseguir crescer em quantidade de lojas sem alterar o fluxo básico de operação.

## Requisitos Não Funcionais Front end

### B21. Multi-loja / rede

- [ ] SUP-RNF-001 — O consumidor deve chegar às ofertas com o mínimo possível de etapas.
- [ ] SUP-RNF-002 — O consumidor não deve precisar de treinamento.
- [ ] SUP-RNF-003 — O consumidor não deve precisar criar conta para visualizar ofertas.
- [ ] SUP-RNF-004 — O acesso deve ser prioritariamente adequado ao uso pelo celular.
- [ ] SUP-RNF-005 — Os preços promocionais devem possuir destaque visual.
- [ ] SUP-RNF-006 — A validade das promoções deve ser facilmente identificável.
- [ ] SUP-RNF-007 — A navegação entre categorias deve ser simples.
- [ ] SUP-RNF-008 — A administração do supermercado deve utilizar linguagem compreensível para usuários do varejo.
- [ ] SUP-RNF-009 — O cadastro de ofertas deve ser simples.
- [ ] SUP-RNF-010 — Mensagens de erro devem ser claras.
- [ ] SUP-RNF-011 — A página de ofertas deve aparecer rapidamente após a leitura do QR Code.
- [ ] SUP-RNF-012 — A navegação entre ofertas deve possuir resposta rápida.
- [ ] SUP-RNF-013 — Imagens não devem impedir a visualização das informações essenciais.
- [ ] SUP-RNF-014 — O painel deve permanecer utilizável mesmo com grande volume de ofertas.
- [ ] SUP-RNF-032 — O conteúdo deve possuir contraste adequado.
- [ ] SUP-RNF-033 — Informações importantes não devem depender somente de cores.
- [ ] SUP-RNF-034 — Textos devem ser legíveis em dispositivos móveis.
- [ ] SUP-RNF-035 — Os principais fluxos devem considerar acessibilidade digital.

---
