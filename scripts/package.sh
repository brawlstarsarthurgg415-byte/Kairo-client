#!/usr/bin/env bash
set -euo pipefail
prop() { grep "^$1=" gradle.properties | cut -d= -f2-; }
VERSION=$(prop mod_version); MC=$(prop minecraft_version); LOADER=$(prop loader_version)

rm -rf dist work && mkdir -p dist work/installer

MOD="build/libs/kairo-${VERSION}.jar"
[ -f "$MOD" ] || { echo "Mod jar nao encontrado: $MOD"; ls build/libs; exit 1; }

# Este e o arquivo para a pasta mods do Minecraft Fabric, inclusive em launchers Android.
cp "$MOD" dist/Kairo-Fabric.jar

# Instalador Swing/AWT para Minecraft Launcher desktop. Nao executar no Battly/Android.
javac --release 21 -d work/installer installer/src/dev/kairo/installer/*.java
cp dist/Kairo-Fabric.jar work/installer/kairo-mod.jar
printf 'mc_version=%s\nloader_version=%s\nmod_version=%s\n' "$MC" "$LOADER" "$VERSION" > work/installer/kairo-installer.properties
printf 'Main-Class: dev.kairo.installer.Installer\n' > work/manifest.txt
jar cfm dist/Kairo-Desktop-Installer.jar work/manifest.txt -C work/installer .

(cd dist && sha256sum *.jar > SHA256SUMS.txt)
ls -l dist
