# Primeira Versão — Requisitos do Produto

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
- [ ] SUP-RF-017 — O supermercado deve poder definir quais informações serão exibidas ao consumidor.
- [ ] SUP-RF-018 — O supermercado deve poder personalizar sua identidade visual.
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
- [ ] SUP-RF-030 — O supermercado deve poder ordenar categorias.
- [ ] SUP-RF-031 — O supermercado deve poder associar produtos a categorias.
- [ ] SUP-RF-032 — O supermercado deve poder definir identificação visual para categorias.
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
- [ ] SUP-RF-055 — O supermercado deve poder destacar uma oferta.
- [ ] SUP-RF-056 — O supermercado deve poder definir a ordem das ofertas.
- [ ] SUP-RF-057 — O supermercado deve poder informar condições ou observações da oferta.
- [ ] SUP-RF-058 — O supermercado deve poder associar uma oferta a uma ou mais lojas.
- [ ] SUP-RF-059 — O supermercado deve poder copiar uma oferta existente.
- [ ] SUP-RF-060 — Ofertas vencidas devem deixar de ser apresentadas como vigentes.
### B7. Campanhas

- [ ] SUP-RF-061 — O supermercado deve poder criar campanhas.
- [ ] SUP-RF-062 — O supermercado deve poder definir o nome da campanha.
- [ ] SUP-RF-063 — O supermercado deve poder cadastrar uma descrição.
- [ ] SUP-RF-064 — O supermercado deve poder definir início e fim da campanha.
- [ ] SUP-RF-065 — O supermercado deve poder associar várias ofertas a uma campanha.
- [ ] SUP-RF-066 — O supermercado deve poder associar uma campanha a uma ou mais lojas.
- [ ] SUP-RF-067 — O supermercado deve poder editar uma campanha.
- [ ] SUP-RF-068 — O supermercado deve poder publicar uma campanha.
- [ ] SUP-RF-069 — O supermercado deve poder retirar uma campanha de publicação.
- [ ] SUP-RF-070 — O supermercado deve poder agendar uma campanha.
- [ ] SUP-RF-071 — O supermercado deve poder duplicar uma campanha anterior.
### B8. Página pública do supermercado

- [ ] SUP-RF-078 — A página deve organizar ofertas por categoria.
- [ ] SUP-RF-081 — A página deve destacar ofertas escolhidas pelo supermercado.
### B9. QR Code

- [ ] SUP-RF-089 — O QR Code deve continuar válido após a troca de campanhas.
- [ ] SUP-RF-090 — O supermercado deve poder gerar QR Codes diferentes por loja.
- [ ] SUP-RF-091 — O supermercado deve poder gerar QR Codes para setores específicos.
- [ ] SUP-RF-092 — O supermercado deve poder identificar cada QR Code.
- [ ] SUP-RF-093 — O supermercado deve poder informar onde cada QR Code está instalado.
- [ ] SUP-RF-094 — O supermercado deve poder ativar ou desativar QR Codes.
- [ ] SUP-RF-096 — O sistema deve contabilizar acessos originados por QR Code.
- [ ] SUP-RF-097 — O supermercado deve poder identificar qual QR Code originou os acessos.
### B10. QR Code por setor

- [ ] SUP-RF-098 — O supermercado deve poder criar QR Codes para setores da loja.
- [ ] SUP-RF-099 — Cada QR Code de setor deve possuir identificação própria.
- [ ] SUP-RF-100 — Um QR Code deve poder direcionar o consumidor para uma categoria específica.
- [ ] SUP-RF-101 — Um QR Code deve poder direcionar para uma campanha específica.
### B13. Lista de compras

- [ ] SUP-RF-126 — O consumidor deve poder adicionar ofertas à sua lista de compras.
- [ ] SUP-RF-127 — O consumidor deve poder remover produtos da lista.
- [ ] SUP-RF-129 — O consumidor deve poder limpar a lista.
- [ ] SUP-RF-133 — O supermercado deve poder saber quantas vezes uma oferta foi adicionada a listas.
### B14. Favoritos

- [ ] SUP-RF-135 — O consumidor deve poder remover favoritos.
- [ ] SUP-RF-137 — O supermercado deve poder mensurar interesse por favoritos.
### B15. Compartilhamento

- [ ] SUP-RF-141 — O compartilhamento deve levar diretamente ao conteúdo correspondente.
### B16. WhatsApp

- [ ] SUP-RF-142 — O consumidor deve poder solicitar o recebimento de ofertas pelo WhatsApp.
- [ ] SUP-RF-143 — O consumidor deve ser informado sobre o tipo de comunicação que receberá.
- [ ] SUP-RF-144 — O consumidor deve fornecer consentimento antes de receber mensagens.
- [ ] SUP-RF-145 — O consumidor deve poder cancelar o recebimento.
- [ ] SUP-RF-146 — O supermercado deve poder utilizar campanhas como base para comunicações.
- [ ] SUP-RF-147 — O sistema deve registrar a autorização do consumidor.
- [ ] SUP-RF-148 — O sistema deve registrar a revogação da autorização.
### B17. Cupons

- [ ] SUP-RF-149 — O supermercado deve poder criar cupons.
- [ ] SUP-RF-150 — O supermercado deve poder definir validade do cupom.
- [ ] SUP-RF-151 — O supermercado deve poder associar cupom a um produto.
- [ ] SUP-RF-152 — O supermercado deve poder associar cupom a uma campanha.
- [ ] SUP-RF-153 — O supermercado deve poder definir condições de utilização.
- [ ] SUP-RF-155 — Cupons vencidos devem ser identificados como expirados.
### B18. Ofertas exclusivas digitais

- [ ] SUP-RF-156 — O supermercado deve poder criar uma oferta exclusiva para o canal digital.
- [ ] SUP-RF-157 — O supermercado deve poder criar oferta exclusiva para determinado QR Code.
- [ ] SUP-RF-158 — O consumidor deve ser informado quando uma oferta for exclusiva.
- [ ] SUP-RF-159 — O supermercado deve poder mensurar o interesse nessas ofertas.
### B19. Tabloide existente

- [ ] SUP-RF-160 — O supermercado deve poder disponibilizar um tabloide já existente.
- [ ] SUP-RF-161 — O supermercado deve poder associar o tabloide a uma campanha.
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
- [ ] SUP-RF-172 — A rede deve poder criar campanhas para todas as lojas.
- [ ] SUP-RF-173 — A rede deve poder criar campanhas para lojas específicas.
- [ ] SUP-RF-174 — A rede deve poder replicar campanhas.
### B22. Retail Media

- [ ] SUP-RF-177 — O supermercado deve poder exibir conteúdo patrocinado.
- [ ] SUP-RF-178 — Campanhas patrocinadas devem possuir período de exibição.
- [ ] SUP-RF-179 — O sistema deve contabilizar visualizações de conteúdo patrocinado.
- [ ] SUP-RF-180 — O sistema deve contabilizar interações com conteúdo patrocinado.

## Requisitos Funcionais Front end

### B1. Conta e acesso do supermercado

- [ ] SUP-RF-001 — O supermercado deve poder acessar sua área administrativa.
- [ ] SUP-RF-003 — O usuário deve poder encerrar sua sessão.
- [ ] SUP-RF-004 — O usuário deve poder recuperar seu acesso.
- [ ] SUP-RF-010 — O supermercado deve poder consultar quem realizou alterações importantes.
### B3. Gestão de lojas

- [ ] SUP-RF-023 — O supermercado deve poder visualizar suas lojas.
- [ ] SUP-RF-026 — O supermercado deve poder visualizar métricas por loja.
### B5. Produtos

- [ ] SUP-RF-043 — O supermercado deve poder pesquisar produtos.
- [ ] SUP-RF-044 — O supermercado deve poder filtrar produtos por categoria.
### B6. Ofertas

- [ ] SUP-RF-049 — O sistema deve apresentar o percentual de desconto quando aplicável.
### B7. Campanhas

- [ ] SUP-RF-072 — O supermercado deve poder visualizar uma prévia antes da publicação.
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
### B10. QR Code por setor

- [ ] SUP-RF-102 — O supermercado deve poder comparar o desempenho dos diferentes QR Codes.
### B11. Analytics

- [ ] SUP-RF-103 — O supermercado deve poder visualizar a quantidade de acessos.
- [ ] SUP-RF-104 — O supermercado deve poder visualizar a quantidade de scans de QR Code.
- [ ] SUP-RF-105 — O supermercado deve poder visualizar acessos por data e horário.
- [ ] SUP-RF-106 — O supermercado deve poder visualizar quantidade de visualizações de produtos.
- [ ] SUP-RF-107 — O supermercado deve poder visualizar categorias mais acessadas.
- [ ] SUP-RF-108 — O supermercado deve poder visualizar produtos mais acessados.
- [ ] SUP-RF-109 — O supermercado deve poder visualizar QR Codes mais utilizados.
- [ ] SUP-RF-110 — O supermercado deve poder selecionar um período de análise.
- [ ] SUP-RF-111 — O supermercado deve poder visualizar métricas por loja.
- [ ] SUP-RF-112 — O supermercado deve poder visualizar métricas por campanha.
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
- [ ] SUP-RF-123 — O painel deve apresentar desempenho de campanhas.
- [ ] SUP-RF-124 — O painel deve permitir escolher período.
- [ ] SUP-RF-125 — O painel deve permitir comparar períodos.
### B13. Lista de compras

- [ ] SUP-RF-128 — O consumidor deve poder visualizar sua lista.
- [ ] SUP-RF-130 — O consumidor deve poder informar quantidade dos itens.
- [ ] SUP-RF-131 — O sistema deve poder apresentar uma estimativa de valor da lista.
- [ ] SUP-RF-132 — O consumidor deve poder compartilhar a lista.
### B14. Favoritos

- [ ] SUP-RF-134 — O consumidor deve poder favoritar ofertas ou produtos.
- [ ] SUP-RF-136 — O consumidor deve poder consultar seus favoritos.
### B15. Compartilhamento

- [ ] SUP-RF-138 — O consumidor deve poder compartilhar uma oferta.
- [ ] SUP-RF-139 — O consumidor deve poder compartilhar uma campanha.
- [ ] SUP-RF-140 — O consumidor deve poder compartilhar a página do supermercado.
### B17. Cupons

- [ ] SUP-RF-154 — O consumidor deve poder consultar seus cupons.
### B19. Tabloide existente

- [ ] SUP-RF-163 — O consumidor deve poder visualizar o tabloide vigente.
### B21. Multi-loja / rede

- [ ] SUP-RF-171 — A rede deve poder visualizar todas as lojas.
- [ ] SUP-RF-175 — A rede deve poder visualizar métricas consolidadas.
- [ ] SUP-RF-176 — A rede deve poder visualizar métricas individuais de cada loja.
### B22. Retail Media

- [ ] SUP-RF-181 — O supermercado deve poder visualizar desempenho das campanhas patrocinadas.
- [ ] SUP-RF-182 — O consumidor deve identificar claramente o conteúdo patrocinado.

## Requisitos Não Funcionais Back end

### B22. Retail Media

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

### B22. Retail Media

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
