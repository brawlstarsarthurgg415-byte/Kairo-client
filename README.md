# Kairo Client

Cliente PvP visual e modular para Minecraft Java. **Sem cheats**: apenas personalizacao, informacoes e utilidades legitimas.

## Ambiente-alvo

- Minecraft Java **1.21.1**
- Fabric Loader **0.16.10**
- Fabric API **0.110.0+1.21.1**
- Java **21**

## Qual arquivo devo usar?

### Android (Battly Launcher)

**Use somente `Kairo-Fabric.jar`. Nao tente abrir/executar esse arquivo pelo recurso "Executar .jar".**

1. Abra o Battly e confirme que a instancia e Minecraft **1.21.1 + Fabric Loader**.
2. Baixe o arquivo `Kairo-Fabric.jar` nos artefatos do GitHub Actions.
3. Coloque o arquivo na pasta `mods` da instancia usada pelo Battly.
4. Instale tambem o **Fabric API 0.110.0+1.21.1** na mesma pasta `mods`.
5. Inicie o Minecraft pela instancia Fabric normalmente. O Kairo e carregado pelo Fabric; nao e um aplicativo Java separado.

Se o Battly nao permitir importar mods diretamente, use o gerenciador de arquivos do Android para copiar os dois JARs para a pasta `mods` correta da instancia.

### Windows/macOS/Linux desktop

`Kairo-Desktop-Installer.jar` e um instalador grafico Swing/AWT para o Minecraft Launcher desktop. Ele exige Java 21 e **nao e compativel com o modo "Executar .jar" do Battly/Android**.

## Recursos

| Recurso | Estado |
|---|---|
| Mira configuravel | Implementado; requer teste no jogo |
| HUD: FPS, ping, CPS, coordenadas, keystrokes, armadura e pocoes | Implementado; requer teste no jogo |
| Sprint automatico usando tecla nativa | Implementado; requer teste no jogo |
| Painel de modulos com tema escuro | Implementado; requer teste em resolucoes diferentes |
| Perfis salvos e troca rapida | Implementado; requer teste no jogo |

## Compilar

O GitHub Actions compila, executa testes automatizados, empacota e valida os JARs. Os arquivos aparecem em **Actions → Artifacts** apos a execucao.

## Compatibilidade Android

Compilar com sucesso nao garante funcionamento em launchers Android. O teste final precisa ser feito no Minecraft 1.21.1 com Fabric Loader 0.16.10, Fabric API 0.110.0+1.21.1 e Java 21. Renderizadores como MobileGlues podem ter limitacoes proprias.

## Fair play

Nao contem KillAura, AimAssist, AutoClicker, reach, velocity, ESP, X-Ray, macros nem manipulacao de pacotes.
