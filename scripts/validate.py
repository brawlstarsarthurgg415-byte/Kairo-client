#!/usr/bin/env python3
"""Validacao estatica dos pacotes (NAO substitui o teste no Battly)."""
import io, json, sys, zipfile

def check(c, msg):
    if not c:
        sys.exit("FALHOU: " + msg)

jar = zipfile.ZipFile("dist/Kairo-Fabric.jar")
check(jar.testzip() is None, "JAR corrompido")
m = json.loads(jar.read("fabric.mod.json"))
check(m["id"] == "kairo" and "${" not in m["version"], "fabric.mod.json invalido")
check(m["depends"]["minecraft"] == "1.21.1", "versao do Minecraft")
names = set(jar.namelist())
entry = m["entrypoints"]["client"][0].replace(".", "/") + ".class"
check(entry in names, "entrypoint ausente: " + entry)
for mx in m.get("mixins", []):
    check(mx in names, "config de mixin ausente: " + mx)
    cfg = json.loads(jar.read(mx))
    for cls in cfg.get("client", []) + cfg.get("mixins", []):
        path = cfg["package"].replace(".", "/") + "/" + cls + ".class"
        check(path in names, "classe de mixin ausente: " + path)
print("OK Fabric JAR:", len(names), "entradas; mixins:", m.get("mixins"))

pack = zipfile.ZipFile("dist/Kairo-Battly.mrpack")
idx = json.loads(pack.read("modrinth.index.json"))
check(idx["formatVersion"] == 1 and idx["game"] == "minecraft", "index mrpack")
check(idx["dependencies"]["minecraft"] == "1.21.1" and "fabric-loader" in idx["dependencies"], "dependencias do mrpack")
check("overrides/mods/Kairo-Fabric.jar" in pack.namelist(), "mod ausente no mrpack")
inner = zipfile.ZipFile(io.BytesIO(pack.read("overrides/mods/Kairo-Fabric.jar")))
check("fabric.mod.json" in inner.namelist(), "mod embutido invalido")
check(any(f["path"].startswith("mods/fabric-api") for f in idx["files"]), "Fabric API ausente no mrpack")
print("OK MRPack:", [f["path"] for f in idx["files"]], "+ overrides/mods/Kairo-Fabric.jar")
print("Validacao estatica concluida. Teste real no Battly/Android: PENDENTE.")
