# Scrum Poker — Resumo do Projeto

## Back end

- Quantidade de histórias: 125
- Total de Story Points: 671
- Histórias prontas: 105
- Histórias bloqueadas: 0
- Histórias para refinamento: 20
- Épicos: 0

## Front end

- Quantidade de histórias: 101
- Total de Story Points: 358
- Histórias prontas: 88
- Histórias bloqueadas: 0
- Histórias para refinamento: 13
- Épicos: 0

## Total

- Histórias: 226
- Story Points: 1029

# Fluxo de Dependências

```text
BACK
Identidade e acesso → Supermercados e planos → Lojas → Categorias → Produtos → Campanhas e ofertas → QR Code → Analytics e relatórios

FRONT
Autenticação → Administração → Cadastros → Campanhas e ofertas → Página pública → Analytics e dashboards

CRUZADO
Serviços Back → Interfaces Front → Experiência pública e painéis
```

As relações específicas estão registradas na seção Scrum de cada história e nos índices de Back e Front.

# Histórias por Complexidade

## 1 SP

- Nenhuma.

## 2 SP

- Back/US-084 — Informar peso, unidade ou volume
- Front/US-136 — Apresentar imagem do produto quando disponível
- Front/US-135 — Apresentar validade da promoção
- Front/US-137 — Apresentar condições da oferta
- Front/US-134 — Apresentar preços

## 3 SP

- Back/US-041 — Restringir usuários autorizados devem acessar a administração geral da plataforma
- Back/US-174 — Restringir usuários autorizados devem acessar a administração do supermercado
- Back/US-003 — Editar os dados de um supermercado
- Back/US-004 — Ativar um supermercado
- Back/US-005 — Desativar um supermercado
- Back/US-012 — Identificar qual usuário realizou determinada alteração administrativa
- Back/US-016 — Disponibilizar funcionalidades para determinados supermercados
- Back/US-050 — Permitir crescimento do número de supermercados sem alterar o modelo de administração
- Back/US-058 — Alterar suas credenciais de acesso
- Back/US-061 — Ativar ou desativar seus usuários
- Back/US-175 — Um supermercado não deve acessar os dados privados de outro
- Back/US-057 — Alterar seus dados cadastrais
- Back/US-069 — Identificar cada loja individualmente
- Back/US-071 — Ativar ou desativar uma loja
- Back/US-075 — Editar categorias
- Back/US-076 — Desativar categorias
- Back/US-101 — Organizar ofertas por categoria
- Back/US-079 — Informar o nome do produto
- Back/US-085 — Editar produtos
- Back/US-086 — Desativar produtos
- Back/US-089 — Vincular um produto a uma oferta
- Back/US-186 — Produtos cadastrados devem poder ser reutilizados
- Back/US-091 — Informar o preço promocional
- Back/US-092 — Informar a data inicial da oferta
- Back/US-093 — Informar a data final da oferta
- Back/US-094 — Editar uma oferta
- Back/US-096 — Ativar ou desativar uma oferta
- Back/US-097 — Informar condições ou observações da oferta
- Back/US-100 — Ofertas vencidas devem deixar de ser apresentadas como vigentes
- Back/US-108 — O compartilhamento deve levar diretamente ao conteúdo correspondente
- Back/US-115 — Definir a validade dos conteúdos
- Back/US-116 — Ordenar conteúdos promocionais
- Back/US-171 — Restringir ofertas vigentes devem ser apresentadas como atuais
- Back/US-179 — Consultar ofertas sem fornecer dados pessoais
- Back/US-010 — Identificar supermercados com maior utilização da plataforma
- Back/US-011 — Identificar supermercados inativos ou com baixa utilização
- Back/US-047 — Operações críticas devem reduzir o risco de execução acidental
- Back/US-067 — Editar seus dados
- Back/US-082 — Informar a marca
- Back/US-083 — Informar uma descrição
- Back/US-090 — Informar o preço normal
- Front/US-018 — Acessar uma área administrativa própria
- Front/US-118 — Acessar sua área administrativa
- Front/US-021 — Visualizar todos os supermercados cadastrados
- Front/US-022 — Consultar os dados de um supermercado
- Front/US-023 — Visualizar as lojas vinculadas a cada supermercado
- Front/US-024 — Visualizar os usuários administrativos vinculados a cada supermercado
- Front/US-026 — Consultar os limites do plano de cada supermercado
- Front/US-027 — Consultar o estado da assinatura de cada supermercado
- Front/US-028 — Visualizar a quantidade total de supermercados cadastrados
- Front/US-029 — Visualizar a quantidade de supermercados ativos
- Front/US-030 — Visualizar a quantidade de supermercados inativos
- Front/US-039 — Consultar informações necessárias para suporte ao supermercado
- Front/US-054 — Conseguir localizar um supermercado de forma simples
- Front/US-121 — Consultar quem realizou alterações importantes no seu supermercado
- Front/US-130 — Identificar claramente o supermercado
- Front/US-197 — A administração do supermercado deve utilizar linguagem compreensível para usuários do varejo
- Front/US-213 — Receber avisos de vencimento da assinatura
- Front/US-224 — Acessar a interface de gestão de planos
- Front/US-031 — Visualizar a quantidade total de lojas cadastradas
- Front/US-122 — Visualizar suas lojas
- Front/US-151 — Comparar lojas
- Front/US-165 — Visualizar todas as lojas
- Front/US-125 — Filtrar produtos por categoria
- Front/US-132 — Filtrar ofertas por categoria
- Front/US-144 — Visualizar categorias mais acessadas
- Front/US-157 — Apresentar categorias mais visualizadas
- Front/US-196 — A navegação entre categorias deve ser simples
- Front/US-124 — Pesquisar produtos
- Front/US-133 — Pesquisar produtos ou ofertas
- Front/US-145 — Visualizar produtos mais acessados
- Front/US-032 — Visualizar a quantidade total de campanhas criadas
- Front/US-033 — Visualizar a quantidade total de ofertas cadastradas
- Front/US-128 — Conseguir acessar as ofertas sem login
- Front/US-129 — Conseguir acessar as ofertas sem cadastro
- Front/US-131 — Apresentar as ofertas vigentes
- Front/US-138 — Visualizar detalhes da oferta
- Front/US-139 — Informar quando não houver ofertas vigentes
- Front/US-156 — Apresentar ofertas mais visualizadas
- Front/US-190 — Chegar às ofertas com o mínimo possível de etapas
- Front/US-194 — Os preços promocionais devem possuir destaque visual
- Front/US-195 — A validade das promoções deve ser facilmente identificável
- Front/US-198 — O cadastro de ofertas deve ser simples
- Front/US-200 — A página de ofertas deve aparecer rapidamente após a leitura do QR Code
- Front/US-201 — A navegação entre ofertas deve possuir resposta rápida
- Front/US-204 — O conteúdo deve possuir contraste adequado
- Front/US-221 — Acessar a interface de gestão de campanhas
- Front/US-035 — Visualizar a quantidade total de leituras de QR Code
- Front/US-141 — Utilizar o QR Code em materiais físicos e digitais
- Front/US-142 — Visualizar a quantidade de scans de QR Code
- Front/US-146 — Visualizar QR Codes mais utilizados
- Front/US-158 — Apresentar QR Codes mais utilizados
- Front/US-020 — Recuperar seu acesso
- Front/US-034 — Visualizar a quantidade total de acessos às páginas públicas
- Front/US-120 — Recuperar seu acesso
- Front/US-143 — Visualizar acessos por data e horário
- Front/US-154 — Apresentar acessos
- Front/US-155 — Apresentar scans
- Front/US-193 — O acesso deve ser prioritariamente adequado ao uso pelo celular
- Front/US-223 — Solicitar a exportação de relatórios
- Front/US-019 — Encerrar sua sessão
- Front/US-025 — Consultar o status de uma conta
- Front/US-038 — Consultar registros de alterações relevantes
- Front/US-119 — Encerrar sua sessão
- Front/US-126 — Apresentar o percentual de desconto quando aplicável
- Front/US-147 — Selecionar um período de análise
- Front/US-153 — Possuir um painel resumido
- Front/US-159 — Permitir escolher período
- Front/US-160 — Permitir comparar períodos
- Front/US-191 — O consumidor não deve precisar de treinamento
- Front/US-199 — Mensagens de erro devem ser claras
- Front/US-202 — Imagens não devem impedir a visualização das informações essenciais
- Front/US-205 — Informações importantes não devem depender somente de cores
- Front/US-206 — Textos devem ser legíveis em dispositivos móveis
- Front/US-217 — Acessar a interface da biblioteca de imagens
- Front/US-219 — Receber alerta ao atingir noventa por cento da cota
- Front/US-225 — Acessar a interface de ocorrências

## 5 SP

- Back/US-002 — Cadastrar um supermercado manualmente
- Back/US-006 — Bloquear o acesso de um supermercado à plataforma
- Back/US-007 — Reativar o acesso de um supermercado
- Back/US-045 — Dados privados de supermercados não devem ser expostos publicamente
- Back/US-059 — Cadastrar mais de um usuário administrativo
- Back/US-168 — A página pública deve estar disponível durante o funcionamento do supermercado
- Back/US-068 — Cadastrar uma ou mais lojas
- Back/US-062 — Cadastrar seu nome comercial
- Back/US-063 — Cadastrar sua razão social quando necessária
- Back/US-064 — Cadastrar sua logomarca
- Back/US-065 — Cadastrar seus dados de contato
- Back/US-066 — Cadastrar seu endereço
- Back/US-070 — Cadastrar o endereço de cada loja
- Back/US-117 — Administrar várias lojas em uma conta
- Back/US-074 — Cadastrar categorias
- Back/US-077 — Associar produtos a categorias
- Back/US-078 — Cadastrar produtos
- Back/US-098 — Associar uma oferta a uma ou mais lojas
- Back/US-081 — Associar o produto a uma categoria
- Back/US-080 — Cadastrar uma imagem do produto
- Back/US-088 — Cadastrar uma oferta
- Back/US-017 — Administrar conteúdos gerais da plataforma quando necessário
- Back/US-095 — Cancelar uma oferta
- Back/US-099 — Copiar uma oferta existente
- Back/US-109 — Disponibilizar um tabloide já existente
- Back/US-110 — Informar a validade do tabloide
- Back/US-111 — Tabloides expirados não devem aparecer como atuais
- Back/US-112 — Cadastrar mensagens promocionais
- Back/US-113 — Publicar avisos
- Back/US-114 — Cadastrar banners
- Back/US-169 — Ofertas publicadas devem permanecer disponíveis durante sua validade
- Back/US-185 — Conseguir administrar suas ofertas sem depender constantemente do dono da plataforma
- Back/US-173 — As métricas devem representar os eventos registrados corretamente
- Back/US-184 — Dados de analytics devem ser preferencialmente agregados quando identificação pessoal não for necessária
- Back/US-182 — Consentimentos devem ser registrados quando necessários
- Back/US-183 — Revogar consentimentos
- Back/US-172 — Alterações publicadas devem refletir corretamente para o consumidor
- Back/US-177 — A página pública não deve expor informações administrativas
- Back/US-180 — Dados pessoais devem ser coletados somente quando necessários
- Back/US-181 — A finalidade da coleta deve ser informada
- Back/US-215 — Criar o primeiro DONO com senha provisória
- Front/US-163 — Compartilhar a página do supermercado
- Front/US-123 — Visualizar métricas por loja
- Front/US-148 — Visualizar métricas por loja
- Front/US-167 — Visualizar métricas individuais de cada loja
- Front/US-149 — Visualizar métricas por produto
- Front/US-127 — Possuir uma página pública de ofertas
- Front/US-152 — Visualizar ranking de ofertas
- Front/US-161 — Compartilhar uma oferta
- Front/US-162 — Compartilhar uma campanha
- Front/US-164 — Visualizar o tabloide vigente
- Front/US-192 — O consumidor não deve precisar criar conta para visualizar ofertas
- Front/US-226 — Acessar a interface de conteúdos gerais
- Front/US-140 — Obter um QR Code para sua página pública
- Front/US-036 — Visualizar métricas gerais da plataforma
- Front/US-037 — Filtrar métricas por período
- Front/US-055 — As métricas gerais devem ser compreensíveis sem necessidade de conhecimento técnico
- Front/US-166 — Visualizar métricas consolidadas

## 8 SP

- Back/US-056 — Autenticar seus usuários
- Back/US-001 — Autenticar-se no sistema
- Back/US-048 — Proteger contas de supermercados contra acesso indevido
- Back/US-008 — Associar um plano comercial a um supermercado
- Back/US-009 — Alterar o plano de um supermercado
- Back/US-014 — Definir funcionalidades disponíveis por plano
- Back/US-015 — Definir limites de utilização por plano
- Back/US-043 — Alterações de planos, contas e permissões devem registrar responsável e data
- Back/US-044 — O painel administrativo deve continuar utilizável com grande quantidade de supermercados cadastrados
- Back/US-049 — Informações de diferentes supermercados devem permanecer logicamente separadas
- Back/US-053 — Permitir suporte operacional sem comprometer a privacidade dos clientes dos supermercados
- Back/US-060 — Definir permissões diferentes para seus usuários
- Back/US-176 — Usuários devem respeitar suas permissões
- Back/US-208 — Criar planos comerciais
- Back/US-209 — Editar planos para associações futuras
- Back/US-210 — Excluir logicamente um plano
- Back/US-211 — Renovar a assinatura de um supermercado
- Back/US-212 — Processar o vencimento das assinaturas
- Back/US-218 — Controlar a cota de imagens do supermercado
- Back/US-072 — Administrar ofertas separadamente por loja
- Back/US-073 — Compartilhar uma mesma campanha entre várias lojas
- Back/US-103 — Gerar QR Codes diferentes por loja
- Back/US-189 — Conseguir crescer em quantidade de lojas sem alterar o fluxo básico de operação
- Back/US-087 — Reutilizar produtos em futuras campanhas
- Back/US-102 — O QR Code deve continuar válido após a troca de campanhas
- Back/US-170 — O QR Code físico deve continuar válido após mudanças de campanhas
- Back/US-187 — Campanhas anteriores devem poder ser reaproveitadas
- Back/US-188 — Alterações de campanhas não devem exigir troca do QR Code
- Back/US-106 — Contabilizar acessos originados por QR Code
- Back/US-104 — Identificar cada QR Code
- Back/US-105 — Ativar ou desativar QR Codes
- Back/US-107 — Identificar qual QR Code originou os acessos
- Back/US-052 — O acesso administrativo deve respeitar as regras de privacidade aplicáveis
- Back/US-222 — Exportar relatórios em CSV
- Back/US-042 — As ações administrativas relevantes devem possuir rastreabilidade
- Back/US-051 — Registros de auditoria devem ser preservados pelo período definido pela operação
- Back/US-178 — Alterações importantes devem possuir rastreabilidade
- Back/US-013 — Acompanhar problemas ou ocorrências registradas na plataforma
- Back/US-046 — Possuir visão consolidada da operação
- Back/US-214 — Permitir recuperação de senha por link de uso único
- Back/US-216 — Pesquisar e reutilizar sua biblioteca de imagens
- Front/US-150 — Comparar campanhas
- Front/US-203 — Permanecer utilizável mesmo com grande volume de ofertas
- Front/US-040 — Consultar relatórios gerais de utilização

## 13 SP

- Back/US-220 — Criar e administrar campanhas
- Front/US-207 — Os principais fluxos devem considerar acessibilidade digital

## 21 SP / Épicos

- Nenhuma.

# Pendências de refinamento

As 33 histórias marcadas para refinamento compartilham a questão ainda aberta sobre limite de lojas por plano, valor ilimitado e tratamento do excesso após troca de plano. Nenhuma regra foi presumida.

# Critérios da estimativa

Foi utilizada somente a escala Fibonacci. O consenso considera complexidade, esforço relativo, testes, risco, incerteza e dependências; os pontos não representam tempo.
