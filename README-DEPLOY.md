# IBS Care AI — runnable synthetic-data demonstration

This repository contains real React/Vite source, a Java 21 Spring Boot REST API,
MySQL 8 migrations, and a private Python/FastAPI clustering service. It is a
deployable demonstration, **not the completed production healthcare platform**.
Never enter real patient, personal, or health data into this release.

## What works

- Responsive overview, symptom/food/lifestyle/bowel diaries, time-filtered charts.
- Validated add/delete entry forms, CSV export and browser print-to-PDF.
- Browser-local country, timezone and unit preferences.
- Educational library and deterministic educational chat (not an LLM).
- In the Figma/static preview, diary entries persist in browser localStorage.
- In Docker, the frontend calls Spring Boot and entries persist in MySQL.
- Java DTO/controller/service/repository layers, Flyway schema validation.
- Bounded private Python clustering endpoint with repeatable K-Means.
- Java unit tests and Python tests run when their Docker images build.

## Important limitations

The Docker diary is PUBLIC AND SHARED. All visitors can list, add, and delete
synthetic entries. There is no registration, password reset, patient ownership,
doctor sharing, admin role, consent workflow, durable report generation, audit
trail, live LLM integration, medical record encryption layer, or compliance claim.
Those are later phases, not secretly implemented placeholders. Lifestyle uses a
sleep value plus notes; food uses title plus notes rather than normalized ingredients.
The Python API is available inside Docker only; the frontend does not call it yet.
Bootstrap reboot is included; the preview retains the scaffold's Tailwind utility
system. A complete Bootstrap-only migration and JavaScript-only frontend remain
outstanding (the current frontend is TSX).

## Prerequisites

Recommended: Docker Engine/Desktop with Compose v2 and at least 4 GB free memory.
Docker supplies Java 21, Maven, Node 22, MySQL 8, and Python 3.12.
For frontend-only local work: Node 22, Corepack/pnpm.

## Run all four services

```sh
cp .env.example .env
# Generate TWO separate passwords, and paste one into each password field in .env:
openssl rand -hex 32
openssl rand -hex 32
docker compose up --build -d
docker compose logs -f backend
```

Open **http://localhost:8088** once the backend logs say the application started.
The first build downloads dependencies and runs service tests, so it can take
several minutes. The database starts empty: use **Log entry** to create records.
The API is under the same origin at `/api/v1/demo/entries`.
MySQL, Spring Boot, and FastAPI have no publicly published host ports.

```sh
docker compose ps
curl --fail http://localhost:8088/api/v1/demo/entries
docker compose exec backend sh -c 'id'
docker compose exec ai python -c \
  "import urllib.request; print(urllib.request.urlopen('http://localhost:8000/internal/health').read().decode())"
docker compose down
# WARNING: this permanently deletes the demo database:
# docker compose down -v
```

## Verify persistence and validation

1. Add a symptom with severity 4; refresh and confirm it remains.
2. Open a second browser: Docker entries are shared, preview entries are not.
3. Delete an entry, refresh, and confirm it is gone.
4. Add different diary types; verify chart totals and filtered pages.
5. Export CSV and check timestamps and values against the diary.
6. Use Reports → Print / save as PDF for a browser-generated copy.
7. Change timezone in settings and check displayed entry times.
8. Submit a Bristol value of 8 through the API and expect HTTP 400.
9. Restart Docker and confirm entries remain (without deleting the volume).

## Frontend-only preview

```sh
corepack enable
pnpm install --frozen-lockfile
pnpm exec vite --config vite.standalone.config.ts
```

This uses localStorage and synthetic sample records. For a portable static build:

```sh
pnpm exec vite build --config vite.standalone.config.ts
```

Deploy `dist/` to a static host, configuring all application routes to fall back
to `index.html`. This is browser-local mode, not the MySQL mode. Set
`VITE_DATA_MODE=server` only where `/api` routes to the Java backend.
There are no frontend secrets.

## Tests

```sh
# With Java 21 and Maven installed:
cd backend && mvn --batch-mode test
# With Python 3.12 and a virtual environment:
cd ai-service && pip install -r requirements.txt && pytest -q
```

From the repository root, Docker builds also execute these tests:

```sh
docker compose build backend ai
```

The Java tests currently cover kind-specific numeric validation, not a complete
integration/security suite. Python tests cover health, validation, minimum-data
handling, and repeatability. No claim is made that unexecuted checks passed.

## Host a public synthetic-data demo

1. Provision a Linux VM with Docker and Compose.
2. Copy this source and create `.env` with fresh strong passwords.
3. Run `docker compose up --build -d`.
4. Put Caddy or an equivalent HTTPS reverse proxy on the host, forwarding your
   domain to `127.0.0.1:8088`. For Caddy, a minimal Caddyfile is:

   ```text
   demo.your-domain.example {
       reverse_proxy 127.0.0.1:8088
   }
   ```

5. Point your domain's A/AAAA records at the host, allow inbound TCP 80/443,
   and let the reverse proxy obtain TLS certificates.
6. Do not expose MySQL or the Python service. Keep the synthetic-demo notice.
7. Apply OS/container updates, log rotation, resource limits, and monitoring.

This is not a high-availability deployment. Real healthcare use requires the
remaining authentication, ownership, privacy, clinical-safety, legal assessment,
backup/restore, monitoring, and security-review phases. Session authentication
and CSRF policies must be designed before any patient endpoints are introduced.

## Demo database backup

```sh
docker compose exec -T mysql sh -c \
  'MYSQL_PWD="$MYSQL_PASSWORD" mysqldump -u "$MYSQL_USER" --single-transaction --no-tablespaces "$MYSQL_DATABASE"' \
  > demo-backup.sql
```

Test restoring into a separate disposable database before relying on backups.
Never commit dumps or `.env` files.

## Source archive

```sh
python3 scripts/package-source.py
```

The sidebar downloads `/ibs-care-ai-source.zip`. The archive excludes secrets,
Figma internals, dependency folders, and database volumes.
