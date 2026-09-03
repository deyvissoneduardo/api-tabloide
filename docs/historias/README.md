# Índice das histórias do MVP

Este índice registra as histórias derivadas de `docs/mvp.md` e as histórias complementares necessárias para implementar as decisões aprovadas em `docs/historias/decisoes-mvp.md`.

| ID | História | Tipo | Arquivo | Dependências |
| -- | -------- | ---- | ------- | ------------ |
| US-001 | Autenticar-se no sistema | BACKEND | [US-001-back-autenticar-se-no-sistema.md](./back/002-US-001-back-autenticar-se-no-sistema.md) | — |
| US-002 | Cadastrar um supermercado manualmente | BACKEND | [US-002-back-cadastrar-supermercado-manualmente.md](./back/006-US-002-back-cadastrar-supermercado-manualmente.md) | US-001 |
| US-003 | Editar os dados de um supermercado | BACKEND | [US-003-back-editar-dados-supermercado.md](./back/007-US-003-back-editar-dados-supermercado.md) | US-001 |
| US-004 | Ativar um supermercado | BACKEND | [US-004-back-ativar-supermercado.md](./back/008-US-004-back-ativar-supermercado.md) | US-001 |
| US-005 | Desativar um supermercado | BACKEND | [US-005-back-desativar-supermercado.md](./back/009-US-005-back-desativar-supermercado.md) | US-001 |
| US-006 | Bloquear o acesso de um supermercado à plataforma | BACKEND | [US-006-back-bloquear-acesso-supermercado-plataforma.md](./back/010-US-006-back-bloquear-acesso-supermercado-plataforma.md) | US-001 |
| US-007 | Reativar o acesso de um supermercado | BACKEND | [US-007-back-reativar-acesso-supermercado.md](./back/011-US-007-back-reativar-acesso-supermercado.md) | US-001 |
| US-008 | Associar um plano comercial a um supermercado | BACKEND | [US-008-back-associar-plano-comercial-supermercado.md](./back/012-US-008-back-associar-plano-comercial-supermercado.md) | US-001 |
| US-009 | Alterar o plano de um supermercado | BACKEND | [US-009-back-alterar-plano-supermercado.md](./back/013-US-009-back-alterar-plano-supermercado.md) | US-001 |
| US-010 | Identificar supermercados com maior utilização da plataforma | BACKEND | [US-010-back-identificar-supermercados-com-maior-utilizacao-plataforma.md](./back/097-US-010-back-identificar-supermercados-com-maior-utilizacao-plataforma.md) | US-001, US-106 |
| US-011 | Identificar supermercados inativos ou com baixa utilização | BACKEND | [US-011-back-identificar-supermercados-inativos-ou-com-baixa-utilizacao.md](./back/098-US-011-back-identificar-supermercados-inativos-ou-com-baixa-utilizacao.md) | US-001, US-106 |
| US-012 | Identificar qual usuário realizou determinada alteração administrativa | BACKEND | [US-012-back-identificar-qual-usuario-realizou-determinada-alteracao-administrativa.md](./back/014-US-012-back-identificar-qual-usuario-realizou-determinada-alteracao-administrativa.md) | US-001 |
| US-013 | Acompanhar problemas ou ocorrências registradas na plataforma | BACKEND | [US-013-back-acompanhar-problemas-ou-ocorrencias-registradas-na-plataforma.md](./back/111-US-013-back-acompanhar-problemas-ou-ocorrencias-registradas-na-plataforma.md) | US-001 |
| US-014 | Definir funcionalidades disponíveis por plano | BACKEND | [US-014-back-definir-funcionalidades-disponiveis-plano.md](./back/015-US-014-back-definir-funcionalidades-disponiveis-plano.md) | US-001 |
| US-015 | Definir limites de utilização por plano | BACKEND | [US-015-back-definir-limites-utilizacao-plano.md](./back/016-US-015-back-definir-limites-utilizacao-plano.md) | US-001 |
| US-016 | Disponibilizar funcionalidades para determinados supermercados | BACKEND | [US-016-back-disponibilizar-funcionalidades-determinados-supermercados.md](./back/017-US-016-back-disponibilizar-funcionalidades-determinados-supermercados.md) | US-001 |
| US-017 | Administrar conteúdos gerais da plataforma quando necessário | BACKEND | [US-017-back-administrar-conteudos-gerais-plataforma-quando-necessario.md](./back/069-US-017-back-administrar-conteudos-gerais-plataforma-quando-necessario.md) | US-001 |
| US-018 | Acessar uma área administrativa própria | FRONTEND | [US-018-front-acessar-area-administrativa-propria.md](./front/001-US-018-front-acessar-area-administrativa-propria.md) | US-001 |
| US-019 | Encerrar sua sessão | FRONTEND | [US-019-front-encerrar-sessao.md](./front/083-US-019-front-encerrar-sessao.md) | US-001 |
| US-020 | Recuperar seu acesso | FRONTEND | [US-020-front-recuperar-acesso.md](./front/070-US-020-front-recuperar-acesso.md) | US-001 |
| US-021 | Visualizar todos os supermercados cadastrados | FRONTEND | [US-021-front-visualizar-todos-supermercados-cadastrados.md](./front/003-US-021-front-visualizar-todos-supermercados-cadastrados.md) | US-001 |
| US-022 | Consultar os dados de um supermercado | FRONTEND | [US-022-front-consultar-dados-supermercado.md](./front/004-US-022-front-consultar-dados-supermercado.md) | US-001 |
| US-023 | Visualizar as lojas vinculadas a cada supermercado | FRONTEND | [US-023-front-visualizar-lojas-vinculadas-cada-supermercado.md](./front/005-US-023-front-visualizar-lojas-vinculadas-cada-supermercado.md) | US-001 |
| US-024 | Visualizar os usuários administrativos vinculados a cada supermercado | FRONTEND | [US-024-front-visualizar-usuarios-administrativos-vinculados-cada-supermercado.md](./front/006-US-024-front-visualizar-usuarios-administrativos-vinculados-cada-supermercado.md) | US-001 |
| US-025 | Consultar o status de uma conta | FRONTEND | [US-025-front-consultar-status-conta.md](./front/084-US-025-front-consultar-status-conta.md) | US-001 |
| US-026 | Consultar os limites do plano de cada supermercado | FRONTEND | [US-026-front-consultar-limites-plano-cada-supermercado.md](./front/007-US-026-front-consultar-limites-plano-cada-supermercado.md) | US-001 |
| US-027 | Consultar o estado da assinatura de cada supermercado | FRONTEND | [US-027-front-consultar-estado-assinatura-cada-supermercado.md](./front/008-US-027-front-consultar-estado-assinatura-cada-supermercado.md) | US-001 |
| US-028 | Visualizar a quantidade total de supermercados cadastrados | FRONTEND | [US-028-front-visualizar-quantidade-total-supermercados-cadastrados.md](./front/009-US-028-front-visualizar-quantidade-total-supermercados-cadastrados.md) | US-001, US-106 |
| US-029 | Visualizar a quantidade de supermercados ativos | FRONTEND | [US-029-front-visualizar-quantidade-supermercados-ativos.md](./front/010-US-029-front-visualizar-quantidade-supermercados-ativos.md) | US-001, US-106 |
| US-030 | Visualizar a quantidade de supermercados inativos | FRONTEND | [US-030-front-visualizar-quantidade-supermercados-inativos.md](./front/011-US-030-front-visualizar-quantidade-supermercados-inativos.md) | US-001, US-106 |
| US-031 | Visualizar a quantidade total de lojas cadastradas | FRONTEND | [US-031-front-visualizar-quantidade-total-lojas-cadastradas.md](./front/020-US-031-front-visualizar-quantidade-total-lojas-cadastradas.md) | US-001, US-106 |
| US-032 | Visualizar a quantidade total de campanhas criadas | FRONTEND | [US-032-front-visualizar-quantidade-total-campanhas-criadas.md](./front/037-US-032-front-visualizar-quantidade-total-campanhas-criadas.md) | US-001, US-106 |
| US-033 | Visualizar a quantidade total de ofertas cadastradas | FRONTEND | [US-033-front-visualizar-quantidade-total-ofertas-cadastradas.md](./front/038-US-033-front-visualizar-quantidade-total-ofertas-cadastradas.md) | US-001, US-106 |
| US-034 | Visualizar a quantidade total de acessos às páginas públicas | FRONTEND | [US-034-front-visualizar-quantidade-total-acessos-paginas-publicas.md](./front/071-US-034-front-visualizar-quantidade-total-acessos-paginas-publicas.md) | US-001, US-106 |
| US-035 | Visualizar a quantidade total de leituras de QR Code | FRONTEND | [US-035-front-visualizar-quantidade-total-leituras-qr-code.md](./front/065-US-035-front-visualizar-quantidade-total-leituras-qr-code.md) | US-001, US-106 |
| US-036 | Visualizar métricas gerais da plataforma | FRONTEND | [US-036-front-visualizar-metricas-gerais-plataforma.md](./front/072-US-036-front-visualizar-metricas-gerais-plataforma.md) | US-001, US-106 |
| US-037 | Filtrar métricas por período | FRONTEND | [US-037-front-filtrar-metricas-periodo.md](./front/073-US-037-front-filtrar-metricas-periodo.md) | US-001, US-106 |
| US-038 | Consultar registros de alterações relevantes | FRONTEND | [US-038-front-consultar-registros-alteracoes-relevantes.md](./front/085-US-038-front-consultar-registros-alteracoes-relevantes.md) | US-001 |
| US-039 | Consultar informações necessárias para suporte ao supermercado | FRONTEND | [US-039-front-consultar-informacoes-necessarias-suporte-ao-supermercado.md](./front/012-US-039-front-consultar-informacoes-necessarias-suporte-ao-supermercado.md) | US-001 |
| US-040 | Consultar relatórios gerais de utilização | FRONTEND | [US-040-front-consultar-relatorios-gerais-utilizacao.md](./front/074-US-040-front-consultar-relatorios-gerais-utilizacao.md) | US-001, US-106 |
| US-041 | Restringir usuários autorizados devem acessar a administração geral da plataforma | BACKEND | [US-041-back-restringir-usuarios-autorizados-devem-acessar-administracao-geral-plat.md](./back/003-US-041-back-restringir-usuarios-autorizados-devem-acessar-administracao-geral-plat.md) | US-001 |
| US-042 | As ações administrativas relevantes devem possuir rastreabilidade | BACKEND | [US-042-back-acoes-administrativas-relevantes-devem-possuir-rastreabilidade.md](./back/106-US-042-back-acoes-administrativas-relevantes-devem-possuir-rastreabilidade.md) | US-001 |
| US-043 | Alterações de planos, contas e permissões devem registrar responsável e data | BACKEND | [US-043-back-alteracoes-planos-contas-permissoes-devem-registrar-responsavel-data.md](./back/018-US-043-back-alteracoes-planos-contas-permissoes-devem-registrar-responsavel-data.md) | US-001 |
| US-044 | O painel administrativo deve continuar utilizável com grande quantidade de supermercados cadastrados | BACKEND | [US-044-back-painel-administrativo-deve-continuar-utilizavel-com-grande-quantidade.md](./back/019-US-044-back-painel-administrativo-deve-continuar-utilizavel-com-grande-quantidade.md) | US-001 |
| US-045 | Dados privados de supermercados não devem ser expostos publicamente | BACKEND | [US-045-back-dados-privados-supermercados-nao-devem-ser-expostos-publicamente.md](./back/020-US-045-back-dados-privados-supermercados-nao-devem-ser-expostos-publicamente.md) | US-001 |
| US-046 | Possuir visão consolidada da operação | BACKEND | [US-046-back-possuir-visao-consolidada-operacao.md](./back/112-US-046-back-possuir-visao-consolidada-operacao.md) | US-001, US-106 |
| US-047 | Operações críticas devem reduzir o risco de execução acidental | BACKEND | [US-047-back-operacoes-criticas-devem-reduzir-risco-execucao-acidental.md](./back/113-US-047-back-operacoes-criticas-devem-reduzir-risco-execucao-acidental.md) | US-001 |
| US-048 | Proteger contas de supermercados contra acesso indevido | BACKEND | [US-048-back-proteger-contas-supermercados-contra-acesso-indevido.md](./back/004-US-048-back-proteger-contas-supermercados-contra-acesso-indevido.md) | US-001 |
| US-049 | Informações de diferentes supermercados devem permanecer logicamente separadas | BACKEND | [US-049-back-informacoes-diferentes-supermercados-devem-permanecer-logicamente-sepa.md](./back/021-US-049-back-informacoes-diferentes-supermercados-devem-permanecer-logicamente-sepa.md) | US-001 |
| US-050 | Permitir crescimento do número de supermercados sem alterar o modelo de administração | BACKEND | [US-050-back-permitir-crescimento-numero-supermercados-sem-alterar-modelo-administr.md](./back/022-US-050-back-permitir-crescimento-numero-supermercados-sem-alterar-modelo-administr.md) | US-001 |
| US-051 | Registros de auditoria devem ser preservados pelo período definido pela operação | BACKEND | [US-051-back-registros-auditoria-devem-ser-preservados-pelo-periodo-definido-pela-o.md](./back/107-US-051-back-registros-auditoria-devem-ser-preservados-pelo-periodo-definido-pela-o.md) | US-001 |
| US-052 | O acesso administrativo deve respeitar as regras de privacidade aplicáveis | BACKEND | [US-052-back-acesso-administrativo-deve-respeitar-regras-privacidade-aplicaveis.md](./back/102-US-052-back-acesso-administrativo-deve-respeitar-regras-privacidade-aplicaveis.md) | US-001 |
| US-053 | Permitir suporte operacional sem comprometer a privacidade dos clientes dos supermercados | BACKEND | [US-053-back-permitir-suporte-operacional-sem-comprometer-privacidade-clientes-supe.md](./back/023-US-053-back-permitir-suporte-operacional-sem-comprometer-privacidade-clientes-supe.md) | US-001 |
| US-054 | Conseguir localizar um supermercado de forma simples | FRONTEND | [US-054-front-conseguir-localizar-supermercado-forma-simples.md](./front/013-US-054-front-conseguir-localizar-supermercado-forma-simples.md) | US-001 |
| US-055 | As métricas gerais devem ser compreensíveis sem necessidade de conhecimento técnico | FRONTEND | [US-055-front-metricas-gerais-devem-ser-compreensiveis-sem-necessidade-conhecimento.md](./front/075-US-055-front-metricas-gerais-devem-ser-compreensiveis-sem-necessidade-conhecimento.md) | US-001, US-106 |
| US-056 | Autenticar seus usuários | BACKEND | [US-056-back-autenticar-usuarios.md](./back/001-US-056-back-autenticar-usuarios.md) | — |
| US-057 | Alterar seus dados cadastrais | BACKEND | [US-057-back-alterar-dados-cadastrais.md](./back/038-US-057-back-alterar-dados-cadastrais.md) | US-056 |
| US-058 | Alterar suas credenciais de acesso | BACKEND | [US-058-back-alterar-credenciais-acesso.md](./back/024-US-058-back-alterar-credenciais-acesso.md) | US-056 |
| US-059 | Cadastrar mais de um usuário administrativo | BACKEND | [US-059-back-cadastrar-mais-usuario-administrativo.md](./back/025-US-059-back-cadastrar-mais-usuario-administrativo.md) | US-056 |
| US-060 | Definir permissões diferentes para seus usuários | BACKEND | [US-060-back-definir-permissoes-diferentes-usuarios.md](./back/026-US-060-back-definir-permissoes-diferentes-usuarios.md) | US-056 |
| US-061 | Ativar ou desativar seus usuários | BACKEND | [US-061-back-ativar-ou-desativar-usuarios.md](./back/027-US-061-back-ativar-ou-desativar-usuarios.md) | US-056 |
| US-062 | Cadastrar seu nome comercial | BACKEND | [US-062-back-cadastrar-nome-comercial.md](./back/039-US-062-back-cadastrar-nome-comercial.md) | US-056 |
| US-063 | Cadastrar sua razão social quando necessária | BACKEND | [US-063-back-cadastrar-razao-social-quando-necessaria.md](./back/040-US-063-back-cadastrar-razao-social-quando-necessaria.md) | US-056 |
| US-064 | Cadastrar sua logomarca | BACKEND | [US-064-back-cadastrar-logomarca.md](./back/041-US-064-back-cadastrar-logomarca.md) | US-056 |
| US-065 | Cadastrar seus dados de contato | BACKEND | [US-065-back-cadastrar-dados-contato.md](./back/042-US-065-back-cadastrar-dados-contato.md) | US-056 |
| US-066 | Cadastrar seu endereço | BACKEND | [US-066-back-cadastrar-endereco.md](./back/043-US-066-back-cadastrar-endereco.md) | US-056 |
| US-067 | Editar seus dados | BACKEND | [US-067-back-editar-dados.md](./back/114-US-067-back-editar-dados.md) | US-056 |
| US-068 | Cadastrar uma ou mais lojas | BACKEND | [US-068-back-cadastrar-ou-mais-lojas.md](./back/037-US-068-back-cadastrar-ou-mais-lojas.md) | US-056 |
| US-069 | Identificar cada loja individualmente | BACKEND | [US-069-back-identificar-cada-loja-individualmente.md](./back/044-US-069-back-identificar-cada-loja-individualmente.md) | US-056, US-068 |
| US-070 | Cadastrar o endereço de cada loja | BACKEND | [US-070-back-cadastrar-endereco-cada-loja.md](./back/045-US-070-back-cadastrar-endereco-cada-loja.md) | US-056, US-068 |
| US-071 | Ativar ou desativar uma loja | BACKEND | [US-071-back-ativar-ou-desativar-loja.md](./back/046-US-071-back-ativar-ou-desativar-loja.md) | US-056, US-068 |
| US-072 | Administrar ofertas separadamente por loja | BACKEND | [US-072-back-administrar-ofertas-separadamente-loja.md](./back/047-US-072-back-administrar-ofertas-separadamente-loja.md) | US-056, US-068 |
| US-073 | Compartilhar uma mesma campanha entre várias lojas | BACKEND | [US-073-back-compartilhar-mesma-campanha-entre-varias-lojas.md](./back/048-US-073-back-compartilhar-mesma-campanha-entre-varias-lojas.md) | US-056, US-068 |
| US-074 | Cadastrar categorias | BACKEND | [US-074-back-cadastrar-categorias.md](./back/052-US-074-back-cadastrar-categorias.md) | US-056 |
| US-075 | Editar categorias | BACKEND | [US-075-back-editar-categorias.md](./back/053-US-075-back-editar-categorias.md) | US-056, US-074 |
| US-076 | Desativar categorias | BACKEND | [US-076-back-desativar-categorias.md](./back/054-US-076-back-desativar-categorias.md) | US-056, US-074 |
| US-077 | Associar produtos a categorias | BACKEND | [US-077-back-associar-produtos-categorias.md](./back/055-US-077-back-associar-produtos-categorias.md) | US-056, US-074 |
| US-078 | Cadastrar produtos | BACKEND | [US-078-back-cadastrar-produtos.md](./back/057-US-078-back-cadastrar-produtos.md) | US-056, US-074 |
| US-079 | Informar o nome do produto | BACKEND | [US-079-back-informar-nome-produto.md](./back/060-US-079-back-informar-nome-produto.md) | US-056, US-078, US-074 |
| US-080 | Cadastrar uma imagem do produto | BACKEND | [US-080-back-cadastrar-imagem-produto.md](./back/061-US-080-back-cadastrar-imagem-produto.md) | US-056, US-078, US-074 |
| US-081 | Associar o produto a uma categoria | BACKEND | [US-081-back-associar-produto-categoria.md](./back/059-US-081-back-associar-produto-categoria.md) | US-056, US-078, US-074 |
| US-082 | Informar a marca | BACKEND | [US-082-back-informar-marca.md](./back/115-US-082-back-informar-marca.md) | US-056, US-078, US-074 |
| US-083 | Informar uma descrição | BACKEND | [US-083-back-informar-descricao.md](./back/116-US-083-back-informar-descricao.md) | US-056, US-078, US-074 |
| US-084 | Informar peso, unidade ou volume | BACKEND | [US-084-back-informar-peso-unidade-ou-volume.md](./back/117-US-084-back-informar-peso-unidade-ou-volume.md) | US-056, US-078, US-074 |
| US-085 | Editar produtos | BACKEND | [US-085-back-editar-produtos.md](./back/062-US-085-back-editar-produtos.md) | US-056, US-078, US-074 |
| US-086 | Desativar produtos | BACKEND | [US-086-back-desativar-produtos.md](./back/063-US-086-back-desativar-produtos.md) | US-056, US-078, US-074 |
| US-087 | Reutilizar produtos em futuras campanhas | BACKEND | [US-087-back-reutilizar-produtos-em-futuras-campanhas.md](./back/064-US-087-back-reutilizar-produtos-em-futuras-campanhas.md) | US-056, US-078, US-074 |
| US-088 | Cadastrar uma oferta | BACKEND | [US-088-back-cadastrar-oferta.md](./back/067-US-088-back-cadastrar-oferta.md) | US-056, US-078, US-068 |
| US-089 | Vincular um produto a uma oferta | BACKEND | [US-089-back-vincular-produto-oferta.md](./back/065-US-089-back-vincular-produto-oferta.md) | US-056, US-078, US-068 |
| US-090 | Informar o preço normal | BACKEND | [US-090-back-informar-preco-normal.md](./back/118-US-090-back-informar-preco-normal.md) | US-056, US-078, US-068 |
| US-091 | Informar o preço promocional | BACKEND | [US-091-back-informar-preco-promocional.md](./back/070-US-091-back-informar-preco-promocional.md) | US-056, US-078, US-068 |
| US-092 | Informar a data inicial da oferta | BACKEND | [US-092-back-informar-data-inicial-oferta.md](./back/071-US-092-back-informar-data-inicial-oferta.md) | US-056, US-078, US-068 |
| US-093 | Informar a data final da oferta | BACKEND | [US-093-back-informar-data-final-oferta.md](./back/072-US-093-back-informar-data-final-oferta.md) | US-056, US-078, US-068 |
| US-094 | Editar uma oferta | BACKEND | [US-094-back-editar-oferta.md](./back/073-US-094-back-editar-oferta.md) | US-056, US-078, US-068 |
| US-095 | Cancelar uma oferta | BACKEND | [US-095-back-cancelar-oferta.md](./back/074-US-095-back-cancelar-oferta.md) | US-056, US-078, US-068 |
| US-096 | Ativar ou desativar uma oferta | BACKEND | [US-096-back-ativar-ou-desativar-oferta.md](./back/075-US-096-back-ativar-ou-desativar-oferta.md) | US-056, US-078, US-068 |
| US-097 | Informar condições ou observações da oferta | BACKEND | [US-097-back-informar-condicoes-ou-observacoes-oferta.md](./back/076-US-097-back-informar-condicoes-ou-observacoes-oferta.md) | US-056, US-078, US-068 |
| US-098 | Associar uma oferta a uma ou mais lojas | BACKEND | [US-098-back-associar-oferta-ou-mais-lojas.md](./back/058-US-098-back-associar-oferta-ou-mais-lojas.md) | US-056, US-078, US-068 |
| US-099 | Copiar uma oferta existente | BACKEND | [US-099-back-copiar-oferta-existente.md](./back/077-US-099-back-copiar-oferta-existente.md) | US-056, US-078, US-068 |
| US-100 | Ofertas vencidas devem deixar de ser apresentadas como vigentes | BACKEND | [US-100-back-ofertas-vencidas-devem-deixar-ser-apresentadas-como-vigentes.md](./back/078-US-100-back-ofertas-vencidas-devem-deixar-ser-apresentadas-como-vigentes.md) | US-056, US-078, US-068 |
| US-101 | Organizar ofertas por categoria | BACKEND | [US-101-back-organizar-ofertas-categoria.md](./back/056-US-101-back-organizar-ofertas-categoria.md) | — |
| US-102 | O QR Code deve continuar válido após a troca de campanhas | BACKEND | [US-102-back-qr-code-deve-continuar-valido-apos-troca-campanhas.md](./back/079-US-102-back-qr-code-deve-continuar-valido-apos-troca-campanhas.md) | US-056, US-140, US-068 |
| US-103 | Gerar QR Codes diferentes por loja | BACKEND | [US-103-back-gerar-qr-codes-diferentes-loja.md](./back/049-US-103-back-gerar-qr-codes-diferentes-loja.md) | US-056, US-140, US-068 |
| US-104 | Identificar cada QR Code | BACKEND | [US-104-back-identificar-cada-qr-code.md](./back/099-US-104-back-identificar-cada-qr-code.md) | US-056, US-140, US-068 |
| US-105 | Ativar ou desativar QR Codes | BACKEND | [US-105-back-ativar-ou-desativar-qr-codes.md](./back/100-US-105-back-ativar-ou-desativar-qr-codes.md) | US-056, US-140, US-068 |
| US-106 | Contabilizar acessos originados por QR Code | BACKEND | [US-106-back-contabilizar-acessos-originados-qr-code.md](./back/096-US-106-back-contabilizar-acessos-originados-qr-code.md) | US-056, US-140, US-068 |
| US-107 | Identificar qual QR Code originou os acessos | BACKEND | [US-107-back-identificar-qual-qr-code-originou-acessos.md](./back/101-US-107-back-identificar-qual-qr-code-originou-acessos.md) | US-056, US-140, US-068 |
| US-108 | O compartilhamento deve levar diretamente ao conteúdo correspondente | BACKEND | [US-108-back-compartilhamento-deve-levar-diretamente-ao-conteudo-correspondente.md](./back/080-US-108-back-compartilhamento-deve-levar-diretamente-ao-conteudo-correspondente.md) | — |
| US-109 | Disponibilizar um tabloide já existente | BACKEND | [US-109-back-disponibilizar-tabloide-ja-existente.md](./back/081-US-109-back-disponibilizar-tabloide-ja-existente.md) | US-056 |
| US-110 | Informar a validade do tabloide | BACKEND | [US-110-back-informar-validade-tabloide.md](./back/082-US-110-back-informar-validade-tabloide.md) | US-056 |
| US-111 | Tabloides expirados não devem aparecer como atuais | BACKEND | [US-111-back-tabloides-expirados-nao-devem-aparecer-como-atuais.md](./back/083-US-111-back-tabloides-expirados-nao-devem-aparecer-como-atuais.md) | US-056 |
| US-112 | Cadastrar mensagens promocionais | BACKEND | [US-112-back-cadastrar-mensagens-promocionais.md](./back/084-US-112-back-cadastrar-mensagens-promocionais.md) | US-056 |
| US-113 | Publicar avisos | BACKEND | [US-113-back-publicar-avisos.md](./back/085-US-113-back-publicar-avisos.md) | US-056 |
| US-114 | Cadastrar banners | BACKEND | [US-114-back-cadastrar-banners.md](./back/086-US-114-back-cadastrar-banners.md) | US-056 |
| US-115 | Definir a validade dos conteúdos | BACKEND | [US-115-back-definir-validade-conteudos.md](./back/087-US-115-back-definir-validade-conteudos.md) | US-056 |
| US-116 | Ordenar conteúdos promocionais | BACKEND | [US-116-back-ordenar-conteudos-promocionais.md](./back/088-US-116-back-ordenar-conteudos-promocionais.md) | US-056 |
| US-117 | Administrar várias lojas em uma conta | BACKEND | [US-117-back-administrar-varias-lojas-em-conta.md](./back/050-US-117-back-administrar-varias-lojas-em-conta.md) | US-056 |
| US-118 | Acessar sua área administrativa | FRONTEND | [US-118-front-acessar-area-administrativa.md](./front/002-US-118-front-acessar-area-administrativa.md) | US-056 |
| US-119 | Encerrar sua sessão | FRONTEND | [US-119-front-encerrar-sessao.md](./front/086-US-119-front-encerrar-sessao.md) | US-056 |
| US-120 | Recuperar seu acesso | FRONTEND | [US-120-front-recuperar-acesso.md](./front/076-US-120-front-recuperar-acesso.md) | US-056 |
| US-121 | Consultar quem realizou alterações importantes no seu supermercado | FRONTEND | [US-121-front-consultar-quem-realizou-alteracoes-importantes-no-supermercado.md](./front/014-US-121-front-consultar-quem-realizou-alteracoes-importantes-no-supermercado.md) | US-056 |
| US-122 | Visualizar suas lojas | FRONTEND | [US-122-front-visualizar-lojas.md](./front/021-US-122-front-visualizar-lojas.md) | US-056, US-068 |
| US-123 | Visualizar métricas por loja | FRONTEND | [US-123-front-visualizar-metricas-loja.md](./front/022-US-123-front-visualizar-metricas-loja.md) | US-056, US-068 |
| US-124 | Pesquisar produtos | FRONTEND | [US-124-front-pesquisar-produtos.md](./front/032-US-124-front-pesquisar-produtos.md) | US-056, US-078, US-074 |
| US-125 | Filtrar produtos por categoria | FRONTEND | [US-125-front-filtrar-produtos-categoria.md](./front/027-US-125-front-filtrar-produtos-categoria.md) | US-056, US-078, US-074 |
| US-126 | Apresentar o percentual de desconto quando aplicável | FRONTEND | [US-126-front-apresentar-percentual-desconto-quando-aplicavel.md](./front/087-US-126-front-apresentar-percentual-desconto-quando-aplicavel.md) | US-056, US-078, US-068 |
| US-127 | Possuir uma página pública de ofertas | FRONTEND | [US-127-front-possuir-pagina-publica-ofertas.md](./front/039-US-127-front-possuir-pagina-publica-ofertas.md) | — |
| US-128 | Conseguir acessar as ofertas sem login | FRONTEND | [US-128-front-conseguir-acessar-ofertas-sem-login.md](./front/040-US-128-front-conseguir-acessar-ofertas-sem-login.md) | — |
| US-129 | Conseguir acessar as ofertas sem cadastro | FRONTEND | [US-129-front-conseguir-acessar-ofertas-sem-cadastro.md](./front/041-US-129-front-conseguir-acessar-ofertas-sem-cadastro.md) | — |
| US-130 | Identificar claramente o supermercado | FRONTEND | [US-130-front-identificar-claramente-supermercado.md](./front/015-US-130-front-identificar-claramente-supermercado.md) | — |
| US-131 | Apresentar as ofertas vigentes | FRONTEND | [US-131-front-apresentar-ofertas-vigentes.md](./front/042-US-131-front-apresentar-ofertas-vigentes.md) | — |
| US-132 | Filtrar ofertas por categoria | FRONTEND | [US-132-front-filtrar-ofertas-categoria.md](./front/028-US-132-front-filtrar-ofertas-categoria.md) | — |
| US-133 | Pesquisar produtos ou ofertas | FRONTEND | [US-133-front-pesquisar-produtos-ou-ofertas.md](./front/033-US-133-front-pesquisar-produtos-ou-ofertas.md) | — |
| US-134 | Apresentar preços | FRONTEND | [US-134-front-apresentar-precos.md](./front/088-US-134-front-apresentar-precos.md) | — |
| US-135 | Apresentar validade da promoção | FRONTEND | [US-135-front-apresentar-validade-promocao.md](./front/043-US-135-front-apresentar-validade-promocao.md) | — |
| US-136 | Apresentar imagem do produto quando disponível | FRONTEND | [US-136-front-apresentar-imagem-produto-quando-disponivel.md](./front/034-US-136-front-apresentar-imagem-produto-quando-disponivel.md) | — |
| US-137 | Apresentar condições da oferta | FRONTEND | [US-137-front-apresentar-condicoes-oferta.md](./front/044-US-137-front-apresentar-condicoes-oferta.md) | — |
| US-138 | Visualizar detalhes da oferta | FRONTEND | [US-138-front-visualizar-detalhes-oferta.md](./front/045-US-138-front-visualizar-detalhes-oferta.md) | — |
| US-139 | Informar quando não houver ofertas vigentes | FRONTEND | [US-139-front-informar-quando-nao-houver-ofertas-vigentes.md](./front/046-US-139-front-informar-quando-nao-houver-ofertas-vigentes.md) | — |
| US-140 | Obter um QR Code para sua página pública | FRONTEND | [US-140-front-obter-qr-code-pagina-publica.md](./front/064-US-140-front-obter-qr-code-pagina-publica.md) | US-056, US-068 |
| US-141 | Utilizar o QR Code em materiais físicos e digitais | FRONTEND | [US-141-front-utilizar-qr-code-em-materiais-fisicos-digitais.md](./front/066-US-141-front-utilizar-qr-code-em-materiais-fisicos-digitais.md) | US-056, US-140, US-068 |
| US-142 | Visualizar a quantidade de scans de QR Code | FRONTEND | [US-142-front-visualizar-quantidade-scans-qr-code.md](./front/067-US-142-front-visualizar-quantidade-scans-qr-code.md) | US-056, US-106 |
| US-143 | Visualizar acessos por data e horário | FRONTEND | [US-143-front-visualizar-acessos-data-horario.md](./front/077-US-143-front-visualizar-acessos-data-horario.md) | US-056, US-106 |
| US-144 | Visualizar categorias mais acessadas | FRONTEND | [US-144-front-visualizar-categorias-mais-acessadas.md](./front/029-US-144-front-visualizar-categorias-mais-acessadas.md) | US-056, US-106 |
| US-145 | Visualizar produtos mais acessados | FRONTEND | [US-145-front-visualizar-produtos-mais-acessados.md](./front/035-US-145-front-visualizar-produtos-mais-acessados.md) | US-056, US-106 |
| US-146 | Visualizar QR Codes mais utilizados | FRONTEND | [US-146-front-visualizar-qr-codes-mais-utilizados.md](./front/068-US-146-front-visualizar-qr-codes-mais-utilizados.md) | US-056, US-106 |
| US-147 | Selecionar um período de análise | FRONTEND | [US-147-front-selecionar-periodo-analise.md](./front/089-US-147-front-selecionar-periodo-analise.md) | US-056, US-106 |
| US-148 | Visualizar métricas por loja | FRONTEND | [US-148-front-visualizar-metricas-loja.md](./front/023-US-148-front-visualizar-metricas-loja.md) | US-056, US-106 |
| US-149 | Visualizar métricas por produto | FRONTEND | [US-149-front-visualizar-metricas-produto.md](./front/036-US-149-front-visualizar-metricas-produto.md) | US-056, US-106 |
| US-150 | Comparar campanhas | FRONTEND | [US-150-front-comparar-campanhas.md](./front/047-US-150-front-comparar-campanhas.md) | US-056, US-106 |
| US-151 | Comparar lojas | FRONTEND | [US-151-front-comparar-lojas.md](./front/024-US-151-front-comparar-lojas.md) | US-056, US-106 |
| US-152 | Visualizar ranking de ofertas | FRONTEND | [US-152-front-visualizar-ranking-ofertas.md](./front/048-US-152-front-visualizar-ranking-ofertas.md) | US-056, US-106 |
| US-153 | Possuir um painel resumido | FRONTEND | [US-153-front-possuir-painel-resumido.md](./front/090-US-153-front-possuir-painel-resumido.md) | US-056, US-106 |
| US-154 | Apresentar acessos | FRONTEND | [US-154-front-apresentar-acessos.md](./front/078-US-154-front-apresentar-acessos.md) | US-056, US-106 |
| US-155 | Apresentar scans | FRONTEND | [US-155-front-apresentar-scans.md](./front/079-US-155-front-apresentar-scans.md) | US-056, US-106 |
| US-156 | Apresentar ofertas mais visualizadas | FRONTEND | [US-156-front-apresentar-ofertas-mais-visualizadas.md](./front/049-US-156-front-apresentar-ofertas-mais-visualizadas.md) | US-056, US-106 |
| US-157 | Apresentar categorias mais visualizadas | FRONTEND | [US-157-front-apresentar-categorias-mais-visualizadas.md](./front/030-US-157-front-apresentar-categorias-mais-visualizadas.md) | US-056, US-106 |
| US-158 | Apresentar QR Codes mais utilizados | FRONTEND | [US-158-front-apresentar-qr-codes-mais-utilizados.md](./front/069-US-158-front-apresentar-qr-codes-mais-utilizados.md) | US-056, US-106 |
| US-159 | Permitir escolher período | FRONTEND | [US-159-front-permitir-escolher-periodo.md](./front/091-US-159-front-permitir-escolher-periodo.md) | US-056, US-106 |
| US-160 | Permitir comparar períodos | FRONTEND | [US-160-front-permitir-comparar-periodos.md](./front/092-US-160-front-permitir-comparar-periodos.md) | US-056, US-106 |
| US-161 | Compartilhar uma oferta | FRONTEND | [US-161-front-compartilhar-oferta.md](./front/050-US-161-front-compartilhar-oferta.md) | — |
| US-162 | Compartilhar uma campanha | FRONTEND | [US-162-front-compartilhar-campanha.md](./front/051-US-162-front-compartilhar-campanha.md) | — |
| US-163 | Compartilhar a página do supermercado | FRONTEND | [US-163-front-compartilhar-pagina-supermercado.md](./front/016-US-163-front-compartilhar-pagina-supermercado.md) | — |
| US-164 | Visualizar o tabloide vigente | FRONTEND | [US-164-front-visualizar-tabloide-vigente.md](./front/052-US-164-front-visualizar-tabloide-vigente.md) | US-056 |
| US-165 | Visualizar todas as lojas | FRONTEND | [US-165-front-visualizar-todas-lojas.md](./front/025-US-165-front-visualizar-todas-lojas.md) | US-056 |
| US-166 | Visualizar métricas consolidadas | FRONTEND | [US-166-front-visualizar-metricas-consolidadas.md](./front/080-US-166-front-visualizar-metricas-consolidadas.md) | US-056 |
| US-167 | Visualizar métricas individuais de cada loja | FRONTEND | [US-167-front-visualizar-metricas-individuais-cada-loja.md](./front/026-US-167-front-visualizar-metricas-individuais-cada-loja.md) | US-056 |
| US-168 | A página pública deve estar disponível durante o funcionamento do supermercado | BACKEND | [US-168-back-pagina-publica-deve-estar-disponivel-durante-funcionamento-supermercad.md](./back/028-US-168-back-pagina-publica-deve-estar-disponivel-durante-funcionamento-supermercad.md) | — |
| US-169 | Ofertas publicadas devem permanecer disponíveis durante sua validade | BACKEND | [US-169-back-ofertas-publicadas-devem-permanecer-disponiveis-durante-validade.md](./back/089-US-169-back-ofertas-publicadas-devem-permanecer-disponiveis-durante-validade.md) | — |
| US-170 | O QR Code físico deve continuar válido após mudanças de campanhas | BACKEND | [US-170-back-qr-code-fisico-deve-continuar-valido-apos-mudancas-campanhas.md](./back/090-US-170-back-qr-code-fisico-deve-continuar-valido-apos-mudancas-campanhas.md) | US-056, US-140, US-068 |
| US-171 | Restringir ofertas vigentes devem ser apresentadas como atuais | BACKEND | [US-171-back-restringir-ofertas-vigentes-devem-ser-apresentadas-como-atuais.md](./back/091-US-171-back-restringir-ofertas-vigentes-devem-ser-apresentadas-como-atuais.md) | — |
| US-172 | Alterações publicadas devem refletir corretamente para o consumidor | BACKEND | [US-172-back-alteracoes-publicadas-devem-refletir-corretamente-consumidor.md](./back/119-US-172-back-alteracoes-publicadas-devem-refletir-corretamente-consumidor.md) | — |
| US-173 | As métricas devem representar os eventos registrados corretamente | BACKEND | [US-173-back-metricas-devem-representar-eventos-registrados-corretamente.md](./back/103-US-173-back-metricas-devem-representar-eventos-registrados-corretamente.md) | US-056, US-106 |
| US-174 | Restringir usuários autorizados devem acessar a administração do supermercado | BACKEND | [US-174-back-restringir-usuarios-autorizados-devem-acessar-administracao-supermerca.md](./back/005-US-174-back-restringir-usuarios-autorizados-devem-acessar-administracao-supermerca.md) | US-056 |
| US-175 | Um supermercado não deve acessar os dados privados de outro | BACKEND | [US-175-back-supermercado-nao-deve-acessar-dados-privados-outro.md](./back/029-US-175-back-supermercado-nao-deve-acessar-dados-privados-outro.md) | US-056 |
| US-176 | Usuários devem respeitar suas permissões | BACKEND | [US-176-back-usuarios-devem-respeitar-permissoes.md](./back/030-US-176-back-usuarios-devem-respeitar-permissoes.md) | US-056 |
| US-177 | A página pública não deve expor informações administrativas | BACKEND | [US-177-back-pagina-publica-nao-deve-expor-informacoes-administrativas.md](./back/120-US-177-back-pagina-publica-nao-deve-expor-informacoes-administrativas.md) | US-056 |
| US-178 | Alterações importantes devem possuir rastreabilidade | BACKEND | [US-178-back-alteracoes-importantes-devem-possuir-rastreabilidade.md](./back/108-US-178-back-alteracoes-importantes-devem-possuir-rastreabilidade.md) | US-056 |
| US-179 | Consultar ofertas sem fornecer dados pessoais | BACKEND | [US-179-back-consultar-ofertas-sem-fornecer-dados-pessoais.md](./back/092-US-179-back-consultar-ofertas-sem-fornecer-dados-pessoais.md) | — |
| US-180 | Dados pessoais devem ser coletados somente quando necessários | BACKEND | [US-180-back-dados-pessoais-devem-ser-coletados-somente-quando-necessarios.md](./back/121-US-180-back-dados-pessoais-devem-ser-coletados-somente-quando-necessarios.md) | — |
| US-181 | A finalidade da coleta deve ser informada | BACKEND | [US-181-back-finalidade-coleta-deve-ser-informada.md](./back/122-US-181-back-finalidade-coleta-deve-ser-informada.md) | — |
| US-182 | Consentimentos devem ser registrados quando necessários | BACKEND | [US-182-back-consentimentos-devem-ser-registrados-quando-necessarios.md](./back/109-US-182-back-consentimentos-devem-ser-registrados-quando-necessarios.md) | — |
| US-183 | Revogar consentimentos | BACKEND | [US-183-back-revogar-consentimentos.md](./back/110-US-183-back-revogar-consentimentos.md) | — |
| US-184 | Dados de analytics devem ser preferencialmente agregados quando identificação pessoal não for necessária | BACKEND | [US-184-back-dados-analytics-devem-ser-preferencialmente-agregados-quando-identific.md](./back/104-US-184-back-dados-analytics-devem-ser-preferencialmente-agregados-quando-identific.md) | — |
| US-185 | Conseguir administrar suas ofertas sem depender constantemente do dono da plataforma | BACKEND | [US-185-back-conseguir-administrar-ofertas-sem-depender-constantemente-dono-platafo.md](./back/093-US-185-back-conseguir-administrar-ofertas-sem-depender-constantemente-dono-platafo.md) | US-056, US-220, US-088 |
| US-186 | Produtos cadastrados devem poder ser reutilizados | BACKEND | [US-186-back-produtos-cadastrados-devem-poder-ser-reutilizados.md](./back/066-US-186-back-produtos-cadastrados-devem-poder-ser-reutilizados.md) | US-056, US-078, US-074 |
| US-187 | Campanhas anteriores devem poder ser reaproveitadas | BACKEND | [US-187-back-campanhas-anteriores-devem-poder-ser-reaproveitadas.md](./back/094-US-187-back-campanhas-anteriores-devem-poder-ser-reaproveitadas.md) | US-056, US-220, US-088 |
| US-188 | Alterações de campanhas não devem exigir troca do QR Code | BACKEND | [US-188-back-alteracoes-campanhas-nao-devem-exigir-troca-qr-code.md](./back/095-US-188-back-alteracoes-campanhas-nao-devem-exigir-troca-qr-code.md) | US-056, US-140, US-068 |
| US-189 | Conseguir crescer em quantidade de lojas sem alterar o fluxo básico de operação | BACKEND | [US-189-back-conseguir-crescer-em-quantidade-lojas-sem-alterar-fluxo-basico-operaca.md](./back/051-US-189-back-conseguir-crescer-em-quantidade-lojas-sem-alterar-fluxo-basico-operaca.md) | US-056 |
| US-190 | Chegar às ofertas com o mínimo possível de etapas | FRONTEND | [US-190-front-chegar-ofertas-com-minimo-possivel-etapas.md](./front/053-US-190-front-chegar-ofertas-com-minimo-possivel-etapas.md) | US-056 |
| US-191 | O consumidor não deve precisar de treinamento | FRONTEND | [US-191-front-consumidor-nao-deve-precisar-treinamento.md](./front/093-US-191-front-consumidor-nao-deve-precisar-treinamento.md) | US-056 |
| US-192 | O consumidor não deve precisar criar conta para visualizar ofertas | FRONTEND | [US-192-front-consumidor-nao-deve-precisar-criar-conta-visualizar-ofertas.md](./front/054-US-192-front-consumidor-nao-deve-precisar-criar-conta-visualizar-ofertas.md) | US-056 |
| US-193 | O acesso deve ser prioritariamente adequado ao uso pelo celular | FRONTEND | [US-193-front-acesso-deve-ser-prioritariamente-adequado-ao-uso-pelo-celular.md](./front/081-US-193-front-acesso-deve-ser-prioritariamente-adequado-ao-uso-pelo-celular.md) | US-056 |
| US-194 | Os preços promocionais devem possuir destaque visual | FRONTEND | [US-194-front-precos-promocionais-devem-possuir-destaque-visual.md](./front/055-US-194-front-precos-promocionais-devem-possuir-destaque-visual.md) | US-056 |
| US-195 | A validade das promoções deve ser facilmente identificável | FRONTEND | [US-195-front-validade-promocoes-deve-ser-facilmente-identificavel.md](./front/056-US-195-front-validade-promocoes-deve-ser-facilmente-identificavel.md) | US-056 |
| US-196 | A navegação entre categorias deve ser simples | FRONTEND | [US-196-front-navegacao-entre-categorias-deve-ser-simples.md](./front/031-US-196-front-navegacao-entre-categorias-deve-ser-simples.md) | US-056 |
| US-197 | A administração do supermercado deve utilizar linguagem compreensível para usuários do varejo | FRONTEND | [US-197-front-administracao-supermercado-deve-utilizar-linguagem-compreensivel-usuar.md](./front/017-US-197-front-administracao-supermercado-deve-utilizar-linguagem-compreensivel-usuar.md) | US-056 |
| US-198 | O cadastro de ofertas deve ser simples | FRONTEND | [US-198-front-cadastro-ofertas-deve-ser-simples.md](./front/057-US-198-front-cadastro-ofertas-deve-ser-simples.md) | US-056 |
| US-199 | Mensagens de erro devem ser claras | FRONTEND | [US-199-front-mensagens-erro-devem-ser-claras.md](./front/094-US-199-front-mensagens-erro-devem-ser-claras.md) | US-056 |
| US-200 | A página de ofertas deve aparecer rapidamente após a leitura do QR Code | FRONTEND | [US-200-front-pagina-ofertas-deve-aparecer-rapidamente-apos-leitura-qr-code.md](./front/058-US-200-front-pagina-ofertas-deve-aparecer-rapidamente-apos-leitura-qr-code.md) | US-056 |
| US-201 | A navegação entre ofertas deve possuir resposta rápida | FRONTEND | [US-201-front-navegacao-entre-ofertas-deve-possuir-resposta-rapida.md](./front/059-US-201-front-navegacao-entre-ofertas-deve-possuir-resposta-rapida.md) | US-056 |
| US-202 | Imagens não devem impedir a visualização das informações essenciais | FRONTEND | [US-202-front-imagens-nao-devem-impedir-visualizacao-informacoes-essenciais.md](./front/095-US-202-front-imagens-nao-devem-impedir-visualizacao-informacoes-essenciais.md) | US-056 |
| US-203 | Permanecer utilizável mesmo com grande volume de ofertas | FRONTEND | [US-203-front-permanecer-utilizavel-mesmo-com-grande-volume-ofertas.md](./front/060-US-203-front-permanecer-utilizavel-mesmo-com-grande-volume-ofertas.md) | US-056 |
| US-204 | O conteúdo deve possuir contraste adequado | FRONTEND | [US-204-front-conteudo-deve-possuir-contraste-adequado.md](./front/061-US-204-front-conteudo-deve-possuir-contraste-adequado.md) | US-056, US-220, US-088 |
| US-205 | Informações importantes não devem depender somente de cores | FRONTEND | [US-205-front-informacoes-importantes-nao-devem-depender-somente-cores.md](./front/096-US-205-front-informacoes-importantes-nao-devem-depender-somente-cores.md) | US-056, US-220, US-088 |
| US-206 | Textos devem ser legíveis em dispositivos móveis | FRONTEND | [US-206-front-textos-devem-ser-legiveis-em-dispositivos-moveis.md](./front/097-US-206-front-textos-devem-ser-legiveis-em-dispositivos-moveis.md) | US-056, US-220, US-088 |
| US-207 | Os principais fluxos devem considerar acessibilidade digital | FRONTEND | [US-207-front-principais-fluxos-devem-considerar-acessibilidade-digital.md](./front/098-US-207-front-principais-fluxos-devem-considerar-acessibilidade-digital.md) | US-056, US-220, US-088 |
| US-208 | Criar planos comerciais | BACKEND | [US-208-back-criar-planos-comerciais.md](./back/031-US-208-back-criar-planos-comerciais.md) | US-056 |
| US-209 | Editar planos para associações futuras | BACKEND | [US-209-back-editar-planos-associacoes-futuras.md](./back/032-US-209-back-editar-planos-associacoes-futuras.md) | US-056 |
| US-210 | Excluir logicamente um plano | BACKEND | [US-210-back-excluir-logicamente-plano.md](./back/033-US-210-back-excluir-logicamente-plano.md) | US-056 |
| US-211 | Renovar a assinatura de um supermercado | BACKEND | [US-211-back-renovar-assinatura-supermercado.md](./back/034-US-211-back-renovar-assinatura-supermercado.md) | US-056 |
| US-212 | Processar o vencimento das assinaturas | BACKEND | [US-212-back-processar-vencimento-assinaturas.md](./back/035-US-212-back-processar-vencimento-assinaturas.md) | US-056 |
| US-213 | Receber avisos de vencimento da assinatura | FRONTEND | [US-213-front-receber-avisos-vencimento-assinatura.md](./front/018-US-213-front-receber-avisos-vencimento-assinatura.md) | US-056 |
| US-214 | Permitir recuperação de senha por link de uso único | BACKEND | [US-214-back-permitir-recuperacao-senha-link-uso-unico.md](./back/123-US-214-back-permitir-recuperacao-senha-link-uso-unico.md) | US-056 |
| US-215 | Criar o primeiro DONO com senha provisória | BACKEND | [US-215-back-criar-primeiro-dono-com-senha-provisoria.md](./back/124-US-215-back-criar-primeiro-dono-com-senha-provisoria.md) | US-056 |
| US-216 | Pesquisar e reutilizar sua biblioteca de imagens | BACKEND | [US-216-back-pesquisar-reutilizar-biblioteca-imagens.md](./back/125-US-216-back-pesquisar-reutilizar-biblioteca-imagens.md) | US-056 |
| US-217 | Acessar a interface da biblioteca de imagens | FRONTEND | [US-217-front-acessar-interface-biblioteca-imagens.md](./front/099-US-217-front-acessar-interface-biblioteca-imagens.md) | US-056 |
| US-218 | Controlar a cota de imagens do supermercado | BACKEND | [US-218-back-controlar-cota-imagens-supermercado.md](./back/036-US-218-back-controlar-cota-imagens-supermercado.md) | US-056 |
| US-219 | Receber alerta ao atingir noventa por cento da cota | FRONTEND | [US-219-front-receber-alerta-ao-atingir-noventa-cento-cota.md](./front/100-US-219-front-receber-alerta-ao-atingir-noventa-cento-cota.md) | US-056 |
| US-220 | Criar e administrar campanhas | BACKEND | [US-220-back-criar-administrar-campanhas.md](./back/068-US-220-back-criar-administrar-campanhas.md) | US-056, US-088 |
| US-221 | Acessar a interface de gestão de campanhas | FRONTEND | [US-221-front-acessar-interface-gestao-campanhas.md](./front/062-US-221-front-acessar-interface-gestao-campanhas.md) | US-056 |
| US-222 | Exportar relatórios em CSV | BACKEND | [US-222-back-exportar-relatorios-em-csv.md](./back/105-US-222-back-exportar-relatorios-em-csv.md) | US-056, US-106 |
| US-223 | Solicitar a exportação de relatórios | FRONTEND | [US-223-front-solicitar-exportacao-relatorios.md](./front/082-US-223-front-solicitar-exportacao-relatorios.md) | US-056, US-106 |
| US-224 | Acessar a interface de gestão de planos | FRONTEND | [US-224-front-acessar-interface-gestao-planos.md](./front/019-US-224-front-acessar-interface-gestao-planos.md) | US-056 |
| US-225 | Acessar a interface de ocorrências | FRONTEND | [US-225-front-acessar-interface-ocorrencias.md](./front/101-US-225-front-acessar-interface-ocorrencias.md) | US-056 |
| US-226 | Acessar a interface de conteúdos gerais | FRONTEND | [US-226-front-acessar-interface-conteudos-gerais.md](./front/063-US-226-front-acessar-interface-conteudos-gerais.md) | US-056 |

## Totais

- Histórias: 226
- Frontend: 101
- Backend: 125
- Requisitos originais cobertos: 207 de 207
- Histórias complementares das decisões: 19
- Itens originais que geraram mais de uma história: nenhum; as histórias adicionais usam origem `DEC-RF`.
- Histórias retiradas do comportamento ativo, mantidas para rastreabilidade: ADM-RF-004, ADM-RF-036, ADM-RF-038, SUP-RNF-029 e SUP-RNF-030.

## Questão em aberto

- Limite de lojas por plano, possibilidade de valor ilimitado e comportamento quando uma troca de plano deixar o supermercado acima do limite.

## Lacuna rastreável resolvida

O documento original utiliza campanhas em comparação, compartilhamento e reutilização, mas não seleciona requisitos de criação e edição. As histórias complementares DEC-RF-013 e DEC-RF-014 cobrem backend e frontend dessa gestão.

