# Kairo Client

Cliente PvP para Minecraft Java (Fabric). **Sem cheats**: só informação, personalização e utilidades legítimas.

- Minecraft **1.21.11** · Fabric Loader **0.18.4** · Fabric API **0.141.1+1.21.11** · Java **21**

## Status honesto (v1.0.0, primeira rodada)

| Recurso | Estado |
|---|---|
| Mira personalizada (cruz, ponto, círculo, cor, tamanho, espaçamento, opacidade, contorno) | implementado, **não testado no jogo** |
| HUD: FPS, Ping, CPS (esq/dir), Coordenadas, Keystrokes, Armor HUD (durabilidade), Potion HUD | implementado, **não testado no jogo** |
| Sprint automático (usa a tecla de sprint do próprio jogo) | implementado, não testado |
| Central de mods (tecla **Right Shift**): ligar/desligar, configurar, restaurar padrão | implementado, não testado |
| Perfis salvos em JSON, troca rápida (tecla configurável), backup automático (10 últimos) | implementado, não testado |
| Instalador Standalone | implementado, não testado |
| GitHub Actions + Releases | implementado, não testado |
| Editor de HUD por arrastar, editor visual de mira, importar/exportar, Replay, Analytics, launcher próprio, Config Cloud etc. | **não implementado** (próximas rodadas) |

Posições do HUD são em % da tela (funcionam em qualquer resolução). O CPS é informativo, lido do estado do mouse a cada frame.

## Instalação

**Opção A, Fabric Mod** (já tem Fabric instalado): coloque `Kairo-Fabric.jar` e o Fabric API em `.minecraft/mods/`.

**Opção B, Standalone**: dê dois cliques em `Kairo-Standalone.jar` (ou `java -jar Kairo-Standalone.jar --cli`). Ele cria a instalação "Kairo Client" no Launcher oficial, com pasta própria (`.minecraft/kairo-client`), Fabric Loader e Fabric API. Abra o Launcher uma vez antes. Para outros launchers (Prism, etc.) use a Opção A.

## Compilar (GitHub Actions)

Dê push no `main` ou crie uma tag `v1.0.0`. Os JARs saem em *Actions → Artifacts* e, nas tags, em *Releases*. Em caso de falha, o artifact `build-logs` traz o log.
Não precisa de `gradlew`: o workflow instala o Gradle 9.2.1.

## Fair play

Não há KillAura, AimAssist, AutoClicker, reach, velocity, ESP, X-Ray, macros nem manipulação de pacotes, e não haverá.
