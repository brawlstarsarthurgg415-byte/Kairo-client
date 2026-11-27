# Kairo Client

Cliente PvP visual e modular para Minecraft Java. **Sem cheats**: apenas personalização, informações e utilidades legítimas.

## Ambiente-alvo desta versão

- Minecraft Java **1.21.1**
- Fabric Loader **0.16.10**
- Fabric API **0.110.0+1.21.1**
- Java **21**
- Teste de compilação: GitHub Actions
- Teste dentro do Minecraft/Android: pendente

## Recursos implementados

| Recurso | Estado |
|---|---|
| Mira configurável (cruz, ponto, círculo, cor, tamanho, espaçamento e opacidade) | Implementado; requer teste no jogo |
| HUD: FPS, ping, CPS, coordenadas, keystrokes, armadura e poções | Implementado; requer teste no jogo |
| Sprint automático usando a tecla nativa do jogo | Implementado; requer teste no jogo |
| Painel de módulos com tema escuro, navegação e opções | Implementado; requer teste em resoluções diferentes |
| Perfis salvos em JSON e troca rápida | Implementado; requer teste no jogo |
| Empacotamento Fabric e instalador Standalone | Implementado; requer teste em desktop |

## Instalação

Para usar o mod em uma instalação Fabric 1.21.1, coloque Kairo-Fabric.jar e o Fabric API correspondente na pasta mods.

O arquivo Kairo-Standalone.jar é um instalador Java voltado a launchers desktop. Ele não é um APK e não substitui o Fabric no Android.

## Compilar

O workflow do GitHub Actions compila, executa testes automatizados, empacota e valida os JARs. Os arquivos aparecem na seção Actions → Artifacts quando a execução termina com sucesso.

## Compatibilidade Android

A compilação bem-sucedida não garante funcionamento em launchers Android. O teste final deve ser feito no Minecraft 1.21.1 com Fabric Loader 0.16.10, Fabric API 0.110.0+1.21.1 e Java 21. MobileGlues e outros renderizadores podem ter limitações próprias.

## Fair play

Não contém KillAura, AimAssist, AutoClicker, reach, velocity, ESP, X-Ray, macros nem manipulação de pacotes.
