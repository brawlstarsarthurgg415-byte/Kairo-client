#!/usr/bin/env bash
set -euo pipefail
cd dist
for j in Kairo-Fabric.jar Kairo-Desktop-Installer.jar; do
  unzip -tq "$j" >/dev/null && echo "OK integridade: $j"
done
python3 - <<'PY'
import json, zipfile, io
f = zipfile.ZipFile('Kairo-Fabric.jar')
m = json.loads(f.read('fabric.mod.json'))
assert m['id'] == 'kairo', 'id invalido'
assert '${version}' not in m['version'], 'versao nao expandida'
assert m['depends']['minecraft'] == '1.21.1', 'Minecraft alvo inesperado'
for dep in ('fabricloader', 'minecraft', 'fabric-api'):
    assert dep in m['depends'], 'dependencia ausente: ' + dep
entry = m['entrypoints']['client'][0].replace('.', '/') + '.class'
assert entry in f.namelist(), 'entrypoint ausente: ' + entry
print('OK Fabric mod:', m['version'], 'Minecraft', m['depends']['minecraft'])

s = zipfile.ZipFile('Kairo-Desktop-Installer.jar')
names = s.namelist()
for need in ('dev/kairo/installer/Installer.class', 'kairo-mod.jar', 'kairo-installer.properties'):
    assert need in names, 'Desktop installer sem ' + need
inner = zipfile.ZipFile(io.BytesIO(s.read('kairo-mod.jar')))
assert 'fabric.mod.json' in inner.namelist(), 'mod embutido invalido'
assert 'META-INF/MANIFEST.MF' in names
assert b'dev.kairo.installer.Installer' in s.read('META-INF/MANIFEST.MF'), 'Main-Class ausente'
print('OK desktop installer (Swing/AWT; nao usar no Android)')
PY
echo "Validacao estatica concluida (NAO substitui o teste dentro do jogo)."
