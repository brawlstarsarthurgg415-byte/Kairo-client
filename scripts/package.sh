#!/usr/bin/env bash
set -euo pipefail
prop() { grep "^$1=" gradle.properties | cut -d= -f2-; }
VERSION=$(prop mod_version); MC=$(prop minecraft_version); LOADER=$(prop loader_version); API=$(prop fabric_version)
rm -rf dist work && mkdir -p dist work/installer work/mrpack/overrides/mods
MOD="build/libs/kairo-${VERSION}.jar"
[ -f "$MOD" ] || { echo "Mod jar nao encontrado: $MOD"; ls build/libs; exit 1; }
cp "$MOD" dist/Kairo-Fabric.jar
cp dist/Kairo-Fabric.jar work/mrpack/overrides/mods/Kairo-Fabric.jar
API_FILE="fabric-api-${API}.jar"
API_URL="https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/${API}/fabric-api-${API}.jar"
curl -fL --retry 3 "$API_URL" -o "work/mrpack/overrides/mods/$API_FILE"
cat > work/mrpack/index.json <<EOF
{
  "formatVersion": 1,
  "game": "minecraft",
  "versionId": "kairo-${VERSION}-mc-${MC}",
  "name": "Kairo Client",
  "summary": "Kairo Client para Minecraft ${MC}, com Fabric Loader e Fabric API.",
  "files": [],
  "dependencies": {
    "minecraft": "${MC}",
    "fabric-loader": "${LOADER}"
  }
}
EOF
(cd work/mrpack && zip -qr ../../dist/Kairo-Battly.mrpack index.json overrides)
javac --release 21 -d work/installer installer/src/dev/kairo/installer/*.java
cp dist/Kairo-Fabric.jar work/installer/kairo-mod.jar
printf 'mc_version=%s\nloader_version=%s\nmod_version=%s\n' "$MC" "$LOADER" "$VERSION" > work/installer/kairo-installer.properties
printf 'Main-Class: dev.kairo.installer.Installer\n' > work/manifest.txt
jar cfm dist/Kairo-Desktop-Installer.jar work/manifest.txt -C work/installer .
(cd dist && sha256sum *.jar *.mrpack > SHA256SUMS.txt)
ls -lh dist
