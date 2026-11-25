"""Build a source archive without dependencies, secrets, or environment internals."""
from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED
import json

root = Path(__file__).resolve().parent.parent
files = [
    "pnpm-lock.yaml", "index.html",
    "Dockerfile", "compose.yaml", ".env.example", ".dockerignore", ".gitignore",
    "vite.standalone.config.ts", "README-DEPLOY.md",
]
folders = ["src", "backend", "ai-service", "infrastructure", "scripts", ".github"]
output = root / "public" / "ibs-care-ai-source.zip"
output.parent.mkdir(exist_ok=True)
excluded = {"target", "__pycache__", ".pytest_cache", ".venv", "node_modules"}
with ZipFile(output, "w", ZIP_DEFLATED) as archive:
    for name in files:
        path = root / name
        if path.is_file():
            archive.write(path, f"ibs-care-ai/{name}")
    for folder in folders:
        for path in (root / folder).rglob("*"):
            if path.is_file() and not any(part in excluded for part in path.parts) and not path.name.startswith(".env"):
                archive.write(path, f"ibs-care-ai/{path.relative_to(root)}")
    # Make the extracted project build with the portable configuration.
    package = json.loads((root / "package.json").read_text())
    package["scripts"]["dev"] = "vite --config vite.standalone.config.ts"
    package["scripts"]["build"] = "vite build --config vite.standalone.config.ts"
    archive.writestr("ibs-care-ai/package.json", json.dumps(package, indent=2) + "\n")
    tsconfig = json.loads((root / "tsconfig.json").read_text())
    tsconfig["include"] = ["src", "vite.standalone.config.ts"]
    archive.writestr("ibs-care-ai/tsconfig.json", json.dumps(tsconfig, indent=2) + "\n")
    archive.writestr("ibs-care-ai/PORTABLE-BUILD.txt",
                     "Run pnpm exec vite build --config vite.standalone.config.ts\n"
                     "or use docker compose up --build -d as documented.\n")
print(f"Created {output.relative_to(root)}")
