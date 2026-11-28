#!/usr/bin/env python3
"""Gera dist/Kairo-Fabric.jar e dist/Kairo-Battly.mrpack (formato Modrinth, importavel no Battly)."""
import hashlib, json, os, shutil, sys, urllib.parse, urllib.request, zipfile

def prop(name):
    for line in open("gradle.properties"):
        if line.startswith(name + "="):
            return line.split("=", 1)[1].strip()
    sys.exit("propriedade ausente: " + name)

ver, mc, loader = prop("mod_version"), prop("minecraft_version"), prop("pack_loader_version")
src = f"build/libs/kairo-{ver}.jar"
if not os.path.isfile(src):
    sys.exit("JAR nao encontrado: " + src)
shutil.rmtree("dist", ignore_errors=True)
os.makedirs("dist")
shutil.copy(src, "dist/Kairo-Fabric.jar")

def get(url):
    req = urllib.request.Request(url, headers={"User-Agent": "Kairo-CI/1.0 (github actions)"})
    return urllib.request.urlopen(req, timeout=60).read()

# Fabric API (arquivo oficial do Modrinth, com hashes oficiais)
q = "https://api.modrinth.com/v2/project/fabric-api/version?" + urllib.parse.urlencode(
    {"game_versions": json.dumps([mc]), "loaders": json.dumps(["fabric"])})
versions = json.loads(get(q))
if not versions:
    sys.exit("Fabric API nao encontrada para " + mc)
f = next((x for x in versions[0]["files"] if x.get("primary")), versions[0]["files"][0])
data = get(f["url"])
assert hashlib.sha512(data).hexdigest() == f["hashes"]["sha512"], "hash da Fabric API nao confere"

index = {
    "formatVersion": 1,
    "game": "minecraft",
    "versionId": ver,
    "name": "Kairo Client",
    "summary": "Cliente PvP Kairo (Fabric) com menu proprio, editor de HUD e perfis.",
    "files": [{
        "path": "mods/" + f["filename"],
        "hashes": {"sha1": f["hashes"]["sha1"], "sha512": f["hashes"]["sha512"]},
        "env": {"client": "required", "server": "unsupported"},
        "downloads": [f["url"]],
        "fileSize": len(data),
    }],
    "dependencies": {"minecraft": mc, "fabric-loader": loader},
}
with zipfile.ZipFile("dist/Kairo-Battly.mrpack", "w", zipfile.ZIP_DEFLATED) as z:
    z.writestr("modrinth.index.json", json.dumps(index, indent=2))
    z.write("dist/Kairo-Fabric.jar", "overrides/mods/Kairo-Fabric.jar")

with open("dist/SHA256SUMS.txt", "w") as out:
    for n in ("Kairo-Fabric.jar", "Kairo-Battly.mrpack"):
        out.write(hashlib.sha256(open("dist/" + n, "rb").read()).hexdigest() + "  " + n + "\n")
print("OK:", os.listdir("dist"), "| Fabric API:", f["filename"])
