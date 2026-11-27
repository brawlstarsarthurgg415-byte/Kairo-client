# Kairo Client

Cliente PvP visual e modular para Minecraft Java. **Sem cheats**: apenas personalizacao, informacoes e utilidades legitimas.

## Ambiente-alvo
- Minecraft Java **1.21.1**
- Fabric Loader **0.16.10**
- Fabric API **0.110.0+1.21.1**
- Java **21**

## Qual arquivo devo usar?

### Android (Battly Mobile)
**Primeira opcao: Kairo-Battly.mrpack.** E um modpack Modrinth com Kairo e Fabric API incluidos em overrides/mods, e com Minecraft/Fabric Loader declarados.

1. Baixe Kairo-Battly.mrpack nos artefatos do GitHub Actions.
2. No Battly, abra o fluxo de importar/instalar modpack e selecione o arquivo.
3. Selecione a instancia criada e inicie o jogo.

Se a versao do Battly instalada nao oferecer importacao de .mrpack, use o modo manual: copie Kairo-Fabric.jar e Fabric API 0.110.0+1.21.1 para a pasta mods da instancia Fabric 1.21.1.

**Nao use "Executar .jar"** para nenhum dos JARs. O mod e carregado pelo Fabric dentro do Minecraft.

### Desktop
Kairo-Desktop-Installer.jar e um instalador Swing/AWT para launchers desktop tradicionais. Nao e o arquivo para Battly Mobile.

## Interface
O painel Kairo tem tema escuro, categorias PvP/HUD/Visual/FPS/Utilidades, lista de modulos, alternancia de estado, controles de configuracao e perfis. A interface foi desenhada para se adaptar a telas estreitas; ainda precisa de teste real no Battly e no toque.

## Recursos atuais
| Recurso | Estado |
|---|---|
| Mira configuravel | Implementado; requer teste no jogo |
| HUD: FPS, ping, CPS, coordenadas, keystrokes, armadura e pocoes | Implementado; requer teste no jogo |
| Sprint automatico usando tecla nativa | Implementado; requer teste no jogo |
| Painel responsivo e categorias | Implementado; requer teste em resolucoes diferentes |
| Perfis salvos e troca rapida | Implementado; requer teste no jogo |

## Compilar
O GitHub Actions compila, empacota e valida o mod, o modpack .mrpack e o instalador desktop. Os arquivos aparecem em Actions → Artifacts apos a execucao.

## Compatibilidade Android
A compilacao bem-sucedida nao garante funcionamento em todos os launchers Android. O teste final precisa ser feito no Minecraft 1.21.1 com Fabric Loader 0.16.10, Fabric API 0.110.0+1.21.1 e Java 21. Renderizadores como MobileGlues podem ter limitacoes proprias.

## Fair play
Nao contem KillAura, AimAssist, AutoClicker, reach, velocity, ESP, X-Ray, macros nem manipulacao de pacotes.
