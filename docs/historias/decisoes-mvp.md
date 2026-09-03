# Decisões consolidadas do MVP

Este documento consolida as respostas fornecidas pelo Product Owner e as recomendações autorizadas em 24 de agosto de 2026. Ele substitui as pendências genéricas das histórias quando tratar do mesmo assunto. A única decisão deliberadamente aberta é o limite de quantidade de lojas por plano.

## 1. Identidade e acesso

- Haverá um único usuário da plataforma: **Super Admin do SGTM**, com acesso total.
- A conta do Super Admin será criada e recuperada diretamente no banco de dados.
- O login utilizará e-mail e senha.
- Supermercados terão os perfis **DONO** e **OPERADOR**.
- DONO possui acesso integral ao próprio supermercado.
- OPERADOR pode somente cadastrar, editar, ativar, desativar e cancelar promoções; pode selecionar lojas, categorias e produtos existentes nesses fluxos.
- Somente DONO gerencia usuários, lojas, categorias e produtos.
- O Super Admin cria o primeiro DONO com senha provisória; a troca no primeiro acesso não é obrigatória.
- Não há limite de usuários por supermercado.
- E-mail é único em toda a plataforma.
- Senha: mínimo de 6 caracteres, ao menos uma maiúscula, uma minúscula e um número.
- Recuperação de DONO e OPERADOR ocorre por link de uso único, enviado por e-mail e válido por 15 minutos.
- Alterar ou recuperar senha encerra todas as sessões do usuário.
- Desativar usuário encerra suas sessões e bloqueia novos acessos.
- O último DONO ativo do supermercado não pode ser desativado.
- Após cinco tentativas consecutivas de login inválidas, recomenda-se bloqueio temporário de 15 minutos, sem revelar se o e-mail existe.
- Sessão administrativa expira após 8 horas e após 30 minutos de inatividade; esses valores podem ser parametrizados tecnicamente sem mudar a regra visível.

## 2. Supermercados e estados

- Cadastro obrigatório: CNPJ, razão social, nome comercial/fantasia, e-mail comercial, telefone, CEP, logradouro, número, bairro, município e UF.
- Complemento, logomarca e observações internas são opcionais.
- Identificador, situação, datas, responsável e plano são controlados pelo sistema.
- O primeiro DONO possui nome, e-mail, senha provisória e perfil.
- CNPJ é único em toda a plataforma, inclusive para contas bloqueadas ou desativadas.
- CNPJ aceita entrada com ou sem máscara; pontuação e espaços são removidos, letras são convertidas para maiúsculas e são armazenados 14 caracteres alfanuméricos.
- No MVP, a validação do CNPJ é local; não há consulta à Receita Federal.
- Estado inicial: **ATIVO**.
- **ATIVO:** administração e página pública operam normalmente.
- **BLOQUEADO:** usuários podem autenticar e consultar, mas nenhuma alteração é permitida; página pública permanece ativa.
- **DESATIVADO:** usuários não acessam e todas as sessões são encerradas; página pública fica indisponível.
- Bloqueio e desativação exigem confirmação explícita dos efeitos, mas não exigem justificativa.
- Reativação preserva dados e estados, respeitando expirações ocorridas no período.

## 3. Planos e assinaturas

- Super Admin cria planos com nome, validade em dias, valor em reais com centavos e limites.
- Validade mínima: 7 dias. Valor zero é permitido.
- Nome de plano disponível é único sem diferenciar caixa ou espaços externos.
- Não há separação de funcionalidades por plano no MVP.
- Há limite de armazenamento por fotos. O limite de lojas permanece em aberto.
- Associação de plano é obrigatória no cadastro do supermercado.
- Pagamento ocorre fora do SGTM; associação, troca e renovação são manuais pelo Super Admin.
- A validade começa no dia seguinte à associação e termina às 23:59:59 do último dia, no fuso de Brasília.
- Renovação antecipada preserva os dias restantes e enfileira o novo período.
- Renovação vencida desbloqueia imediatamente; a nova contagem inicia no dia seguinte.
- Troca de plano aplica limites imediatamente, descarta dias restantes e inicia nova validade no dia seguinte.
- Vencimento bloqueia automaticamente o supermercado.
- DONOS recebem avisos internos 10, 5 e 1 dia antes do vencimento.
- Planos podem ser editados; mudanças valem apenas para associações e renovações futuras.
- Exclusão de plano é lógica: deixa de ser selecionável, preserva histórico e permite reutilizar o nome.
- Planos associados guardam uma cópia histórica de nome, duração, valor e limites contratados.
- Estados recomendados da assinatura: AGENDADA, VIGENTE, VENCIDA e SUBSTITUÍDA.

## 4. Armazenamento e imagens

- Cota padrão: 1.000 fotos; Super Admin pode elevar para 2.000 e, após acordo externo, informar qualquer limite superior.
- Contam imagens de produtos, banners, logomarcas e páginas/imagens de tabloides.
- Excluir libera a cota; substituir deixa apenas a nova imagem contabilizada.
- Uma imagem reutilizada conta uma vez.
- Há biblioteca pesquisável por nome e data, isolada por supermercado; Super Admin consulta todas para suporte.
- Ao atingir a cota, upload é bloqueado, mas reutilização e exclusão continuam disponíveis.
- DONOS recebem alerta interno aos 90%; OPERADORES não.
- Redução de cota preserva arquivos e bloqueia uploads até o consumo voltar ao limite.
- Formatos: JPG, PNG e WebP; máximo de 5 MB por arquivo.
- O sistema gera versões otimizadas e preserva o original.
- Imagem vinculada não pode ser excluída até que todos os vínculos sejam removidos.
- Recomenda-se validar o conteúdo real do arquivo, remover metadados desnecessários e usar nome seguro gerado pelo sistema.

## 5. Lojas

- Campos obrigatórios: nome identificador, CEP, logradouro, número, bairro, município e UF; complemento opcional.
- Nome é único dentro do supermercado sem diferenciar caixa ou espaços externos; pode repetir em supermercados diferentes.
- Estado inicial: ATIVA.
- Somente DONO cadastra, edita, ativa e desativa.
- Loja não é excluída; apenas desativada.
- Desativar torna página e QR Codes indisponíveis, preservando produtos, ofertas, campanhas, métricas e histórico.
- Reativar restaura página e QR Codes, mostrando apenas conteúdos vigentes.
- Endereço deve aceitar CEP brasileiro com oito dígitos e armazenar componentes separadamente.
- O limite de lojas por plano permanece **em aberto**.

## 6. Categorias

- Cada supermercado mantém lista própria e isolada.
- Categoria possui nome obrigatório; descrição e imagem/ícone opcionais.
- Nomes repetidos são permitidos dentro do mesmo supermercado.
- Produto deve pertencer a uma ou mais categorias.
- Somente DONO cadastra, edita, desativa e reativa.
- Categoria desativada não aceita novas associações, não aparece em filtros e oculta publicamente as ofertas de seus produtos.
- Reativação restaura automaticamente ofertas ainda vigentes.
- Categoria não é removida fisicamente quando possui histórico.

## 7. Produtos

- Cada supermercado mantém catálogo próprio e isolado.
- Nome e ao menos uma categoria são obrigatórios.
- Imagem, marca, descrição, peso, unidade e volume são opcionais.
- Produtos duplicados são permitidos e diferenciados pelo identificador.
- Somente DONO cadastra, edita, desativa e reativa.
- Produto desativado deixa de aparecer e oculta suas ofertas; o histórico é preservado.
- Reativação restaura ofertas ainda vigentes.
- Produtos podem ser reutilizados em qualquer campanha futura do mesmo supermercado.
- Pesquisa considera nome, marca e descrição, sem diferenciar caixa ou acentos.
- Filtro de categoria aceita uma ou mais categorias e pode ser combinado com pesquisa.
- Recomenda-se limite de 150 caracteres para nome e marca, 1.000 para descrição e 50 para apresentação; entradas são aparadas, mas o texto original é preservado.

## 8. Promoções e ofertas

- “Promoção” é o fluxo administrativo; “oferta” é o registro publicado de um produto com preço e validade.
- Campos obrigatórios: produto, preço normal, preço promocional, início, fim e ao menos uma loja.
- Condições/observações são opcionais, com recomendação de até 500 caracteres.
- Valores são em reais, com duas casas decimais, maiores que zero; preço promocional deve ser menor que o normal.
- Percentual de desconto = arredondamento de `(normal - promocional) / normal × 100` para número inteiro; só aparece quando os dois preços são válidos.
- Datas usam horário de Brasília. Início não pode ser posterior ao fim.
- Estados: RASCUNHO, AGENDADA, VIGENTE, DESATIVADA, CANCELADA e EXPIRADA.
- Cadastro inicia como RASCUNHO, salvo quando o usuário confirmar publicação.
- Oferta futura publicada fica AGENDADA; torna-se VIGENTE no início; torna-se EXPIRADA automaticamente após o fim.
- Desativação é reversível; reativação respeita vigência. Cancelamento é definitivo e exige confirmação.
- Edição de oferta vigente reflete imediatamente na página pública e registra auditoria.
- Uma oferta pode atender várias lojas; a mesma campanha pode ser compartilhada entre lojas.
- Recomenda-se permitir ofertas simultâneas para o mesmo produto e loja, desde que sejam registros distintos; a página ordena a de menor preço promocional primeiro.
- Copiar oferta cria novo RASCUNHO sem métricas, identificador ou estado do original; datas precisam ser revistas antes da publicação.
- OPERADOR e DONO executam cadastro, edição, ativação, desativação e cancelamento.
- Ações de publicação, cancelamento e alteração de preço são auditadas.

## 9. Campanhas

- Campanha agrupa ofertas para organização e compartilhamento.
- Campos recomendados: nome obrigatório, descrição opcional, início, fim e uma ou mais lojas.
- Estados seguem RASCUNHO, AGENDADA, VIGENTE, CANCELADA e EXPIRADA.
- Uma oferta pode ser incluída em uma campanha por vez; para reutilização, deve ser copiada.
- Compartilhar campanha abre sua página pública diretamente.
- Comparação usa campanhas do mesmo supermercado e períodos equivalentes, exibindo acessos, scans e visualizações de ofertas.
- A ausência de requisitos explícitos de criação/edição de campanha no MVP original deve permanecer destacada como lacuna de rastreabilidade, embora campanhas sejam exigidas por outros itens.

## 10. Página pública

- Cada supermercado e cada loja ativa possui URL pública estável e sem login ou cadastro.
- Recomenda-se rota estável com identificador não sequencial e slug legível; mudança de nome não quebra a URL.
- Página de loja exibe identidade do supermercado e nome da loja.
- Mostra somente ofertas VIGENTES cujos supermercado, loja, produto e ao menos uma categoria estejam ativos.
- Ofertas são agrupadas por categoria; produto em várias categorias pode aparecer em cada grupo aplicável.
- Filtro e pesquisa podem ser combinados. Pesquisa considera produto, marca, descrição e condições.
- Estado vazio informa claramente que não há ofertas vigentes.
- Exibe preço normal, promocional em destaque, percentual quando aplicável, validade, imagem quando disponível e condições.
- Sem imagem, usa espaço reservado sem impedir preço, nome e validade.
- Detalhe da oferta possui URL compartilhável e apresenta todas as informações públicas.
- Nenhum dado administrativo, usuário, e-mail interno, métrica individual ou observação interna é exposto.
- Prioridade mobile; conteúdo essencial deve aparecer mesmo se imagens falharem.
- Recomenda-se p95 de até 3 segundos para conteúdo essencial em conexão móvel 4G, medido em produção.

## 11. QR Codes

- QR Code aponta para uma URL estável de redirecionamento, nunca diretamente para campanha temporária.
- Pode ser criado por loja e recebe nome identificador obrigatório, único no supermercado.
- DONO obtém, identifica, ativa e desativa QR Codes; OPERADOR não gerencia.
- Formatos para download: PNG e SVG, adequados a material físico e digital.
- QR Code desativado abre página informativa de indisponibilidade e não redireciona ao conteúdo.
- QR Code de loja desativada fica indisponível; reativação restaura o mesmo código.
- Troca de campanha não altera QR Code nem URL.
- Cada acesso válido registra QR Code, loja, data/hora, destino e identificador técnico do evento.
- Reenvio técnico do mesmo evento deve ser idempotente.
- Não se coleta identidade do consumidor para contabilizar scan.
- Recomenda-se contabilizar o scan ao acessar a URL de redirecionamento e o acesso público quando a página de destino carregar.

## 12. Compartilhamento

- Oferta, campanha e página do supermercado possuem ação de compartilhamento.
- Em dispositivos compatíveis usa Web Share; caso contrário, oferece copiar link.
- Link leva diretamente ao conteúdo correspondente e é estável durante a existência do conteúdo.
- Conteúdo indisponível apresenta estado explicativo, sem redirecionar silenciosamente para conteúdo diferente.
- Metadados sociais devem incluir título, identidade do supermercado e imagem pública quando disponível.
- Compartilhamento não exige login nem coleta de dados pessoais.

## 13. Tabloide existente

- DONO pode publicar tabloide; OPERADOR não.
- Formatos recomendados: PDF de até 20 MB ou imagens JPG/PNG/WebP de até 5 MB cada.
- Campos: título obrigatório, arquivo/páginas, início e fim de validade e lojas aplicáveis.
- Estados: RASCUNHO, AGENDADO, VIGENTE, DESATIVADO e EXPIRADO.
- Somente tabloide VIGENTE aparece como atual; expirado permanece no histórico administrativo.
- Consumidor visualiza no navegador com navegação por páginas e opção de download do PDF quando o original for PDF.
- Páginas em imagem contam na cota; PDF conta como um arquivo fora da cota de fotos, mas permanece sujeito ao limite de 20 MB.
- Publicação e substituição são auditadas.

## 14. Conteúdos promocionais

- DONO gerencia mensagens promocionais, avisos e banners; OPERADOR não.
- Mensagem: título e texto obrigatórios.
- Aviso: título, texto e nível informativo obrigatório.
- Banner: título, imagem e destino interno ou URL HTTPS opcional.
- Todos possuem início e fim, lojas aplicáveis, estado e ordem.
- Estados: RASCUNHO, AGENDADO, VIGENTE, DESATIVADO e EXPIRADO.
- Ordenação usa posição inteira, com reordenação automática para evitar posições duplicadas.
- Conteúdo só aparece quando vigente e quando supermercado e loja estão disponíveis.
- Links externos abrem com proteção contra acesso ao contexto da página.

## 15. Multi-loja

- Uma conta de supermercado administra todas as suas lojas.
- DONO enxerga e gerencia todas; OPERADOR seleciona apenas lojas no fluxo de promoções.
- Dados e métricas podem ser consultados por loja ou consolidados no supermercado.
- Consolidação soma contagens e calcula percentuais a partir dos totais, sem fazer média simples de percentuais.
- Nenhum usuário acessa lojas de outro supermercado.
- Interfaces com muitas lojas usam pesquisa, paginação e seleção múltipla.

## 16. Analytics, dashboard e relatórios

- Eventos: acesso à página, scan de QR Code, visualização de oferta, categoria acessada e compartilhamento.
- Cada evento contém identificador único, supermercado, loja quando aplicável, recurso, data/hora UTC e origem; exibição usa horário de Brasília.
- Métricas não identificam o consumidor e são agregadas sempre que possível.
- Acesso conta quando a página pública carrega o conteúdo essencial; atualização da mesma página após 30 minutos conta novo acesso.
- Visualização de oferta conta ao abrir o detalhe; categoria acessada conta ao selecionar filtro/categoria.
- Período inclui início às 00:00:00 e fim às 23:59:59 em Brasília.
- Período padrão: últimos 30 dias; máximo recomendado por consulta: 24 meses.
- Atualização recomendada: até 15 minutos após o evento.
- Rankings ordenam por contagem decrescente, depois por nome e identificador.
- Comparações exibem valor absoluto e variação percentual; quando a base é zero, mostram “não calculável”.
- Dashboard resume acessos, scans, ofertas, categorias e QR Codes mais utilizados, com período e comparação.
- Super Admin vê consolidação de toda a plataforma e identifica alta, baixa ou ausência de uso.
- Baixa utilização recomendada: valor abaixo de 20% da mediana de supermercados ativos no mesmo período; inativo: zero eventos no período. O critério deve ser exibido na interface.
- Relatórios podem ser exportados em CSV, respeitando filtros e escopo.
- Eventos brutos: retenção recomendada de 90 dias; agregados: 24 meses.
- Falhas de processamento devem ser reprocessáveis e não podem duplicar contagens.

## 17. Auditoria

- Auditar login relevante, criação/alteração de usuários, perfis, estados, planos, limites, lojas, categorias, produtos, promoções, campanhas, QR Codes, tabloides e conteúdos.
- Registro contém ator, instante UTC, ação, entidade, identificador, supermercado, valores anteriores e posteriores permitidos, resultado e correlação.
- Senhas, tokens e conteúdo pessoal desnecessário nunca são registrados.
- Registros são imutáveis para usuários da aplicação e consultáveis por período, ator, ação e entidade.
- Super Admin consulta toda a auditoria; DONO consulta apenas eventos do próprio supermercado; OPERADOR não acessa auditoria.
- Retenção recomendada: 24 meses.
- Alterações simultâneas devem registrar cada tentativa e seu resultado.

## 18. Suporte e ocorrências

- Super Admin consulta dados cadastrais, estado, plano, limites, consumo, lojas, usuários, auditoria e saúde técnica necessários ao suporte.
- Não há impersonação de usuário no MVP.
- Consulta de suporte é somente leitura, salvo ações administrativas já autorizadas em histórias próprias.
- Toda consulta a dado privado para suporte deve ser auditada.
- Ocorrência possui título, descrição, severidade, status, supermercado/loja opcional, responsável e datas.
- Status recomendados: ABERTA, EM_ANÁLISE, RESOLVIDA e ENCERRADA.
- Histórico de comentários e transições é preservado.
- Dados pessoais de consumidores não são necessários para suporte do MVP.

## 19. Conteúdo geral da plataforma

- Super Admin pode administrar termos de uso, política de privacidade, contatos de suporte e comunicados gerais.
- Conteúdo possui título, corpo, versão, estado RASCUNHO/PUBLICADO/ARQUIVADO e datas.
- Publicar nova versão preserva versões anteriores e registra auditoria.
- Apenas conteúdo PUBLICADO fica visível ao público correspondente.
- Editor deve aceitar texto estruturado seguro, sem execução de scripts.

## 20. Privacidade e consentimento

- Consumidor consulta ofertas sem conta e sem fornecer dados pessoais.
- Analytics usa identificadores técnicos não diretamente identificáveis e evita fingerprinting.
- Não haverá coleta opcional de dados do consumidor no MVP; por isso, não haverá fluxo de consentimento ou revogação nesta versão.
- Cookies não essenciais não serão utilizados. Se forem introduzidos depois, finalidade, consentimento e revogação deverão ser implementados antes da coleta.
- Dados de supermercados e usuários são usados apenas para operação, segurança, suporte e obrigações contratuais informadas.
- Exportações, logs e mensagens devem respeitar o menor privilégio e minimização.

## 21. Requisitos técnicos e UX recomendados

- Administração: paginação no servidor, pesquisa e filtros para listas grandes; padrão de 25 itens por página, opções 25, 50 e 100.
- Operações críticas usam diálogo de confirmação com ação e impacto explícitos.
- Mensagens indicam o problema e a ação possível, sem códigos internos.
- Requisições mutáveis bloqueiam clique duplo e usam idempotência quando houver risco de repetição.
- Interfaces preservam dados preenchidos após erro recuperável.
- Navegação completa por teclado, foco visível, rótulos programáticos, contraste WCAG AA e mensagens anunciadas.
- Informação nunca depende apenas de cor.
- Compatibilidade recomendada: duas versões estáveis mais recentes de Chrome, Edge, Firefox e Safari.
- APIs validam autorização e isolamento de supermercado em todas as operações.
- Logs estruturados, métricas de latência/erro e correlação são obrigatórios nos fluxos críticos.
- Metas recomendadas: APIs p95 até 2 segundos, página pública p95 até 3 segundos em 4G e disponibilidade mensal de 99,5% para página pública.
- Backups diários, retenção de 30 dias e teste periódico de restauração.
- Datas são persistidas em UTC e apresentadas em America/Sao_Paulo.
- Exclusões de entidades com histórico são lógicas, salvo imagens desvinculadas, cuja exclusão libera cota.

## 22. Única questão deliberadamente aberta

- **Q-ABERTA-001 — Limite de lojas por plano:** definir valor mínimo, possibilidade de ilimitado e comportamento quando uma troca de plano deixar o supermercado acima do limite.

