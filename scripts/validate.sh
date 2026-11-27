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
assert 'kairo.mixins.json' in f.namelist(), 'config de mixin ausente'
assert any(n.endswith('-refmap.json') for n in f.namelist()), 'refmap ausente'
assert 'dev/kairo/mixin/InGameHudMixin.class' in f.namelist(), 'classe mixin ausente'
print('OK fabric.mod.json, versao', m['version'], 'entrypoint', entry)
print('OK mixins + refmap')

s = zipfile.ZipFile('Kairo-Standalone.jar')
names = s.namelist()
for need in ('dev/kairo/installer/Installer.class', 'kairo-mod.jar', 'kairo-installer.properties'):
    assert need in names, 'Standalone sem ' + need
inner = zipfile.ZipFile(io.BytesIO(s.read('kairo-mod.jar')))
assert 'fabric.mod.json' in inner.namelist(), 'mod embutido invalido'
assert 'META-INF/MANIFEST.MF' in names
assert b'dev.kairo.installer.Installer' in s.read('META-INF/MANIFEST.MF'), 'Main-Class ausente'
print('OK Standalone: instalador + mod embutido')

# --- MRPack (formato Modrinth) ---
import zipfile as zf
m = zf.ZipFile('Kairo-Battly.mrpack')
names = m.namelist()
assert 'modrinth.index.json' in names, 'index do mrpack ausente'
idx = json.loads(m.read('modrinth.index.json'))
assert idx['formatVersion'] == 1, 'formatVersion invalido'
assert idx['game'] == 'minecraft', 'game invalido'
assert idx['dependencies']['minecraft'], 'versao do minecraft ausente'
assert 'fabric-loader' in idx['dependencies'], 'loader ausente'
mods = [n for n in names if n.startswith('overrides/mods/') and n.endswith('.jar')]
assert mods, 'mod nao embutido no mrpack'
inner = zf.ZipFile(io.BytesIO(m.read(mods[0])))
assert 'fabric.mod.json' in inner.namelist(), 'jar do mrpack invalido'
assert 'kairo.mixins.json' in inner.namelist(), 'mixins ausentes no jar do mrpack'
assert any(n.endswith('-refmap.json') for n in inner.namelist()), 'refmap do mixin ausente'
print('OK MRPack: index valido + mod + mixins + refmap ->', mods[0])
PY
echo "Validacao estatica concluida (NAO substitui o teste dentro do jogo)."
