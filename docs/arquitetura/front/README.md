# Arquitetura de front-end do SGTM

Este diretório contém a arquitetura alvo do front-end Flutter multi-package do SGTM. A documentação foi derivada dos requisitos do MVP, do mapa mental do produto, das decisões de back-end e das regras definidas em `arquiteto-frontend.md`.

## Ordem de leitura

1. [Arquitetura recomendada](./01-arquitetura-frontend-recomendada.md) — contexto, limites, módulos, fluxos e estrutura física.
2. [Stack e decisões técnicas](./02-stack-e-decisoes-tecnicas.md) — escolhas de bibliotecas e critérios de uso.
3. [Qualidade, segurança e operação](./03-qualidade-seguranca-e-operacao.md) — testes, desempenho, acessibilidade, observabilidade e CI/CD.
4. [Rastreabilidade do MVP](./04-rastreabilidade-mvp.md) — requisitos agrupados por experiência e feature proprietária.

`arquiteto-frontend.md` define o papel e as regras permanentes de arquitetura. Ele não é uma especificação implementável por si só; os documentos numerados transformam essas regras em decisões específicas para o SGTM.

## Status

- Estado: arquitetura proposta, ainda sem implementação no repositório.
- Escopo: MVP descrito em `docs/mvp.md`.
- Estrutura obrigatória: `frontend/packages/{core,mobile,web}`.
- Regra de dependência: `mobile -> core <- web`.
- Revisão: toda decisão que alterar limites de package, segurança, estado global, contratos públicos ou experiência pública deve gerar um ADR.

