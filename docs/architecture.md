# Decisões de arquitetura

## Separação por domínio

O código é organizado por capacidade (`project`, `responsible`, `health`) em vez de separar toda a aplicação apenas por tipo técnico. Cada domínio reúne controlador, serviço, persistência e contratos, reduzindo acoplamento entre funcionalidades.

## Fonte de verdade do status

O cliente não envia o status na criação ou edição. O status é derivado das quatro datas por `ProjectMetricsCalculator`. Nas movimentações, o serviço aplica o efeito automático, recalcula e só confirma quando o resultado coincide com o destino solicitado.

## Persistência das métricas

Status, dias de atraso e percentual restante são persistidos e recalculados em toda alteração feita pela API. Isso favorece filtros rápidos do quadro e preserva consistência no caminho oficial de escrita. Uma evolução com múltiplas instâncias pode executar uma rotina diária para atualizar projetos sem edição recente.

## Banco e migrações

PostgreSQL oferece integridade referencial e índices adequados aos filtros. Flyway mantém a evolução reproduzível. A relação entre projeto e responsável usa tabela associativa, permitindo múltiplos responsáveis sem duplicação.

## Tratamento de falhas

Erros de validação retornam 400, recursos ausentes retornam 404, duplicidades retornam 409 e violações de regras do Kanban retornam 422 com orientação para correção.