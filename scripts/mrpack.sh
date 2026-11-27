#!/usr/bin/env bash
# Gera o Kairo-Battly.mrpack (formato Modrinth) pronto para importar no Battly.
# O pacote carrega o mod via overrides/mods, sem instaladores e sem dependencia
# de bibliotecas nativas: puro Java 21, compativel com ARM64 (Android).
set -euo pipefail

prop() { grep "^$1=" gradle.properties | cut -d= -f2-; }
VERSION=$(prop mod_version); MC=$(prop minecraft_version)

[ -f dist/Kairo-Fabric.jar ] || { echo "dist/Kairo-Fabric.jar nao encontrado"; exit 1; }

STAGE="work/mrpack"
rm -rf "$STAGE" && mkdir -p "$STAGE/overrides/mods"
cp dist/Kairo-Fabric.jar "$STAGE/overrides/mods/kairo-${VERSION}.jar"

export KAIRO_VERSION="$VERSION" KAIRO_MC="$MC" KAIRO_STAGE="$STAGE"
python3 - <<'PY'
import json, os, pathlib

stage = pathlib.Path(os.environ["KAIRO_STAGE"])
jar_rel = f"overrides/mods/kairo-{os.environ['KAIRO_VERSION']}.jar"
jar = stage / jar_rel
size = jar.stat().st_size

# O mod e embutido em overrides/mods (copiado na importacao); arquivos locais
# nao precisam de entrada em "files" (que e reservada a downloads remotos).
index = {
    "formatVersion": 1,
    "game": "minecraft",
    "versionId": f"kairo-{os.environ['KAIRO_VERSION']}",
    "name": "Kairo Client",
    "summary": "Cliente PvP modular: HUD, mira personalizada, perfis, editor de HUD e otimizacoes. Sem cheats.",
    "files": [],
    "dependencies": {
        "minecraft": os.environ["KAIRO_MC"],
        "fabric-loader": ">=0.16.10"
    }
}
(stage / "modrinth.index.json").write_text(json.dumps(index, indent=2))
print("modrinth.index.json gerado")
PY

( cd "$STAGE" && zip -q -r -X "../../dist/Kairo-Battly.mrpack" modrinth.index.json overrides )
ls -l dist/Kairo-Battly.mrpack
