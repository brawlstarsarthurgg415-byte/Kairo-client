# Changelog

## 1.1.0
- Mira personalizada: a mira original do Minecraft agora é suprimida por mixin (`InGameHud.renderCrosshair`) — nunca mais duas miras.
- Editor visual de HUD: toque/arraste elementos, escala, cor, fundo, redefinir posição; posições em % da tela.
- Menu principal redesenhado: abas de categoria, busca, lista rolável com cartões, painel de configurações.
- Módulos novos: Direção, Bioma, Relógio, Indicador de sprint, Otimizações (partículas/nuvens/sombras nativas).
- HUD com escala configurável (50–200%) e geometria centralizada (renderização = editor).
- Perfis: exportação e importação; aparência global (cor de destaque, animações do menu) persistida.
- Empacotamento: `Kairo-Battly.mrpack` (formato Modrinth) para importação direta no Battly.
- Testes: matemática de posicionamento do HUD (`HudLayout`).
- CI: validação de metadados, mixins e refmap; artefatos Fabric + MRPack + Standalone.

## 1.0.0
- Núcleo: módulos, configurações tipadas, perfis JSON com backup.
- Módulos: mira, FPS, Ping, CPS, Coordenadas, Keystrokes, Armor HUD, Potion HUD, Sprint automático.
- Central de mods (Right Shift) com troca e criação de perfis.
- Distribuição: Fabric Mod + instalador Standalone via GitHub Actions.
