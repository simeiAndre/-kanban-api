# Uso de Inteligência Artificial

## Ferramentas utilizadas

O projeto foi desenvolvido com apoio do Lovable, utilizando modelos de linguagem e agentes auxiliares para análise do enunciado, planejamento, geração inicial de código, revisão e documentação.

## Organização do trabalho

Foi aplicada uma abordagem orientada por especificação:

1. leitura do PDF e extração dos requisitos obrigatórios;
2. divisão em marcos pequenos e verificáveis;
3. implementação da base, ambiente Docker, responsáveis, projetos, regras Kanban, documentação e testes;
4. revisão das regras contra a tabela de transições original;
5. geração de um pacote limpo para execução local.

Cada marco recebeu uma mensagem de commit sugerida. O controle de versão permaneceu sob decisão humana, sem fornecer credenciais pessoais à IA.

## Decisão humana que corrigiu a sugestão da IA

Uma sugestão inicial genérica considerava um fluxo Kanban tradicional e uma relação de apenas um responsável por projeto. Ela foi rejeitada porque o enunciado exige exatamente quatro estados (`A iniciar`, `Em andamento`, `Atrasado`, `Concluído`), regras baseadas em datas e um ou mais responsáveis. A implementação foi corrigida para usar a tabela do desafio como fonte de verdade e uma relação muitos-para-muitos.

Também foi decidido persistir as métricas recalculadas, em vez de aceitar status ou percentuais livres enviados pelo cliente. Isso reduz inconsistências e centraliza as regras na aplicação.

## Trecho representativo da especificação

> Em toda transição, aplicar os efeitos automáticos definidos, recalcular o status pelas datas e bloquear a operação com orientação clara quando o estado calculado não corresponder ao estado solicitado. Ao criar ou editar um projeto, recalcular status, dias de atraso e percentual de tempo restante.

## Limites e validação

Todo código sugerido por IA deve ser revisado, testado e confrontado com o enunciado antes da entrega. A IA não recebeu senhas do GitHub, credenciais do banco ou outros segredos.