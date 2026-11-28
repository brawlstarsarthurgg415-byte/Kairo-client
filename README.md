# Kairo Client

Cliente PvP para Minecraft Java **1.21.1** (Fabric), pensado para Android (Battly) e desktop. **Sem cheats.**

- Fabric Loader 0.20.1 (compila contra 0.16.10) · Fabric API 0.110.0+1.21.1 · Java 21

## Novidades da 1.1.0 (reconstrução da interface)

- **Menu principal próprio** (substitui a tela inicial via mixin): Um jogador, Multijogador, Opções, Kairo, Editor de HUD, Sair. Botões chamam as telas reais do jogo. Dá para desligar em *Utilidades → Interface*.
- **Central do Kairo** (tecla Right Shift): trilho lateral, busca, cartões de módulos, detalhe com **prévia em tempo real** + controles (interruptor, slider, opções, cor).
- **Editor de HUD** (Layout Editor): toque e arraste; canto para redimensionar; guias de centro/bordas; Frente/Trás; Restaurar; Ocultar. Posições em fração da tela (não saem da tela).
- **Biblioteca de elementos** (tela separada): prévias, Adicionar/Remover.
- **Keystrokes** novo: WASD, espaço, LMB/RMB com CPS; formato, tamanho, espaçamento, cores, opacidade, borda, fonte, escala. Na prévia as teclas respondem ao toque (entrada simulada, separada da real).
- **Mira**: agora *substitui* a mira original (mixin cancela a vanilla). Nunca desenha duas.
- Novos elementos: Direção, Bioma, Relógio. Novo módulo de desempenho: Modo leve (partículas, sombras, nuvens).
- Perfis: recuperação de arquivo inválido (`.corrupt`), backup, exportar/importar, migração das posições antigas.

## Status honesto

| Item | Estado |
|---|---|
| Compilação no GitHub Actions | verificar a cada push |
| Testes automatizados (unitários) | CPS e Setting |
| Validação estática dos pacotes (mod, mixins, mrpack) | no workflow |
| Teste real no Battly / Android | **PENDENTE** (nunca foi feito pelo autor do código) |
| Dependência do Dawn Loader | nenhuma |

Limitações conhecidas: sem editor visual de mira com arrasto (a prévia é em tempo real, os controles são os padrões); o Modo leve altera opções do jogo e as restaura ao desligar/fechar.

## Instalação (Battly)

1. Baixe `Kairo-Battly.mrpack` em *Actions → Artifacts* (ou em *Releases*).
2. No Battly, importe o `.mrpack` como instância (Fabric 1.21.1). Ele traz o Kairo e a Fabric API.
3. Alternativa: coloque `Kairo-Fabric.jar` + Fabric API em `mods/`.

## Fair play

Sem KillAura, AimAssist, AutoClicker, Reach, Velocity, ESP, X-Ray, Scaffold, macros ou manipulação de pacotes.
