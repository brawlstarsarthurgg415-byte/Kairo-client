# Kairo Client

Cliente PvP visual e modular para Minecraft Java. **Sem cheats**: apenas personalização, informações e utilidades legítimas.

## Ambiente-alvo desta versão

- Minecraft Java **1.21.1**
- Fabric Loader **>= 0.16.10** (compatível com 0.16.x e superiores, incluindo o 0.20.1 do Battly)
- Fabric API **0.110.0+1.21.1**
- Java **21**
- **Prioridade de execução: Android ARM64** (Samsung Galaxy S23+ / Snapdragon 8 Gen 2 / Battly Launcher), com suporte desktop quando possível

O cliente é puro Java 21 sobre Fabric: nenhuma biblioteca x86-only, nenhum componente de Windows, nenhum inicializador desktop obrigatório. Se uma integração opcional falhar, o jogo continua iniciando.

## Sobre o Dawn Client

O arquivo `dawn-standalone.jar` (mantido no repositório apenas como referência) é o **Dawn Loader** (`net.digitalingot.loader`), um carregador em cadeia para Minecraft 1.20.1 que valida plataforma e **rejeita ARM** ("Unsupported platform! Tried to launch on linux-arm"). Por isso:

- O Kairo **não depende** do Dawn Loader nem de bibliotecas que o exijam;
- o Dawn é usado apenas como **referência visual/UX**, sem cópia de código;
- todos os recursos do Kairo foram implementados em arquitetura própria.

## Recursos

| Recurso | Estado |
|---|---|
| Menu principal com abas de categoria, busca, lista rolável e cartões de módulos | Implementado; requer teste em resoluções diferentes |
| Painel de configurações por módulo (toque amplo, −/+ , ◀/▶) | Implementado; requer teste no jogo |
| Editor visual de HUD (tocar, arrastar, escala, cor, fundo, restaurar) | Implementado; requer teste no jogo |
| Mira personalizada (cruz, ponto, círculo, cruz+ponto, espessura, tamanho, espaçamento, cor, opacidade, contorno) | Implementado; a mira original é **removida por mixin** (nunca duas miras) |
| Módulos: FPS, ping, CPS, coordenadas, direção, bioma, relógio, keystrokes, armadura, poções, indicador de sprint | Implementados; requerem teste no jogo |
| Sprint automático usando a tecla nativa do jogo | Implementado; requer teste no jogo |
| Otimizações (partículas, nuvens, sombras — opções nativas, restauradas ao desativar) | Implementado; requer teste no jogo |
| Perfis em JSON com troca rápida, exportação e importação | Implementado; requer teste no jogo |
| Aparência global (cor de destaque, animações do menu) | Implementado |
| Pacote `Kairo-Battly.mrpack` para importação direta no Battly | Gerado no CI |

## Instalação no Android (Battly)

1. Abra o **GitHub Actions** deste repositório → workflow "Build Kairo Client" → última execução bem-sucedida.
2. Baixe o artefato e use o arquivo **`Kairo-Battly.mrpack`**: no Battly, importe o pacote como instância/modpack.
3. Alternativa manual: coloque `Kairo-Fabric.jar` na pasta `mods` de uma instância Fabric 1.21.1 com Fabric API 0.110.0+1.21.1.

O `Kairo-Standalone.jar` é um instalador opcional para launchers desktop; **não é necessário no Android**.

## Compilar

O workflow do GitHub Actions compila com Java 21, executa os testes automatizados, valida metadados/mixins/refmap, empacota `Kairo-Fabric.jar`, `Kairo-Standalone.jar` e `Kairo-Battly.mrpack` e publica os artefatos.

## Compatibilidade Android

A compilação bem-sucedida não garante funcionamento em launchers Android. O teste final deve ser feito no Minecraft 1.21.1 com Fabric Loader e Fabric API nas versões acima. Renderizadores GL do Android (MobileGlues etc.) podem ter limitações próprias.

## Fair play

Não contém KillAura, AimAssist, AutoClicker, reach, velocity, ESP, X-Ray, macros nem manipulação de pacotes.
