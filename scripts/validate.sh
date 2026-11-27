#!/usr/bin/env bash
set -euo pipefail
cd dist
for j in Kairo-Fabric.jar Kairo-Standalone.jar; do
  unzip -tq "$j" >/dev/null && echo "OK integridade: $j"
done
python3 - <<'PY'
import json, zipfile, io
f = zipfile.ZipFile('Kairo-Fabric.jar')
m = json.loads(f.read('fabric.mod.json'))
assert m['id'] == 'kairo', 'id invalido'
assert '${version}' not in m['version'], 'versao nao expandida'
for dep in ('fabricloader', 'minecraft', 'fabric-api'):
    assert dep in m['depends'], 'dependencia ausente: ' + dep
entry = m['entrypoints']['client'][0].replace('.', '/') + '.class'
assert entry in f.namelist(), 'entrypoint ausente: ' + entry
print('OK fabric.mod.json, versao', m['version'], 'entrypoint', entry)

s = zipfile.ZipFile('Kairo-Standalone.jar')
names = s.namelist()
for need in ('dev/kairo/installer/Installer.class', 'kairo-mod.jar', 'kairo-installer.properties'):
    assert need in names, 'Standalone sem ' + need
inner = zipfile.ZipFile(io.BytesIO(s.read('kairo-mod.jar')))
assert 'fabric.mod.json' in inner.namelist(), 'mod embutido invalido'
assert 'META-INF/MANIFEST.MF' in names
assert b'dev.kairo.installer.Installer' in s.read('META-INF/MANIFEST.MF'), 'Main-Class ausente'
print('OK Standalone: instalador + mod embutido')
PY
echo "Validacao estatica concluida (NAO substitui o teste dentro do jogo)."
