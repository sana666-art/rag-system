# Changes Required After Docker Configuration

## Overview

This document lists all changes required to make the RAG System run inside Docker.
**No Java code or React component logic is changed.** Only configuration files are modified.

---

## Files Created (Docker - No Changes to Existing Code)

```
D:\rag-system\DockerConfigurations\DockerConfigurationForRAG\
├── rag-system-root\
│   ├── .env                          ← secrets (DO NOT commit)
│   └── docker-compose.yml            ← orchestration
│
├── rag-backend\
│   ├── Dockerfile                    ← multi-stage Maven build
│   ├── .dockerignore
│   └── application.properties        ← updated: env var placeholders
│
└── rag-frontend\
    ├── Dockerfile                    ← multi-stage Node + Nginx
    ├── .dockerignore
    ├── nginx.conf                    ← API proxy + React Router
    └── .env                          ← updated: points to localhost:5173
```

---

## Changes Required (Step by Step)

### Step 1: Update `application.properties`

**File:** `D:\rag-system\rag-backend\src\main\resources\application.properties`

Replace hardcoded sensitive values with environment variable placeholders.
Spring Boot reads `${ENV_VAR:default}` syntax natively.

**Current (hardcoded):**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5434/AlphaPlace2
spring.datasource.username=postgres
spring.datasource.password=12345

jwt.accessExpiration=900000
jwt.refreshExpiration=2592000000
jwt.secret=AlphaPlace2AuthenticationSystem32145

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=sanakhalid21002@gmail.com
spring.mail.password=fjaj ovai vdua azxb

spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.embedding.options.model=nomic-embed-text

spring.ai.google.gemini.api-key=AIzaSyDua__8Wx_hqHwLP51k8fKxPU55atihsxY

app.cors.allowed-origins=http://localhost:5173,http://127.0.0.1:5173
```

**New (environment variables with defaults):**
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5434/AlphaPlace2}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:12345}

jwt.accessExpiration=${JWT_ACCESS_EXPIRATION:900000}
jwt.refreshExpiration=${JWT_REFRESH_EXPIRATION:2592000000}
jwt.secret=${JWT_SECRET:AlphaPlace2AuthenticationSystem32145}

spring.mail.host=${MAIL_HOST:smtp.gmail.com}
spring.mail.port=${MAIL_PORT:587}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

spring.ai.ollama.base-url=${OLLAMA_BASE_URL:http://localhost:11434}
spring.ai.ollama.embedding.options.model=${OLLAMA_EMBEDDING_MODEL:nomic-embed-text}

spring.ai.google.gemini.api-key=${GEMINI_API_KEY}

app.cors.allowed-origins=${APP_CORS_ALLOWED_ORIGINS:http://localhost:5173,http://127.0.0.1:5173}
```

**Why:** When running locally, the defaults kick in (same as before).
When running in Docker, `docker-compose.yml` passes environment variables that override the defaults.

**Impact:** None. Local development works exactly as before.

---

### Step 2: Copy Docker Files to Actual Project

The Docker files are currently in:
```
D:\rag-system\DockerConfigurations\DockerConfigurationForRAG\
```

Copy them to the actual project root:
```
D:\rag-system\
```

**Files to copy:**

| Source | Destination |
|---|---|
| `DockerConfigurationForRAG\rag-system-root\.env` | `D:\rag-system\.env` |
| `DockerConfigurationForRAG\rag-system-root\docker-compose.yml` | `D:\rag-system\docker-compose.yml` |
| `DockerConfigurationForRAG\rag-backend\Dockerfile` | `D:\rag-system\rag-backend\Dockerfile` |
| `DockerConfigurationForRAG\rag-backend\.dockerignore` | `D:\rag-system\rag-backend\.dockerignore` |
| `DockerConfigurationForRAG\rag-backend\application.properties` | `D:\rag-system\rag-backend\src\main\resources\application.properties` |
| `DockerConfigurationForRAG\rag-frontend\Dockerfile` | `D:\rag-system\rag-frontend\Dockerfile` |
| `DockerConfigurationForRAG\rag-frontend\.dockerignore` | `D:\rag-system\rag-frontend\.dockerignore` |
| `DockerConfigurationForRAG\rag-frontend\nginx.conf` | `D:\rag-system\rag-frontend\nginx.conf` |
| `DockerConfigurationForRAG\rag-frontend\.env` | `D:\rag-system\rag-frontend\.env` |

---

### Step 3: Fill in Real Secrets in `.env`

**File:** `D:\rag-system\.env`

Replace `CHANGE_ME` values with real credentials:

```env
POSTGRES_DB=AlphaPlace2
POSTGRES_USER=postgres
POSTGRES_PASSWORD=<your-real-db-password>

JWT_ACCESS_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=2592000000
JWT_SECRET=<your-real-jwt-secret>

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<your-gmail>
MAIL_PASSWORD=<your-gmail-app-password>

GEMINI_API_KEY=<your-real-gemini-key>

APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173

OLLAMA_EMBEDDING_MODEL=nomic-embed-text
```

**Important:** Never commit `.env` to Git.

---

### Step 4: Add `.env` to Root `.gitignore`

**File:** `D:\rag-system\.gitignore`

Add this line:
```
.env
```

This prevents secrets from being committed.

---

### Step 5: Frontend `.env` — Ensure the Value is Empty

**File:** `D:\rag-system\rag-frontend\.env`

`VITE_API_BASE_URL` is intentionally empty.

```env
VITE_API_BASE_URL=
```

This makes the frontend use relative API URLs such as:

```text
/api/login
/api/users/me
/api/chat/ask
/api/chat/stream
```

The browser sends these requests to the same origin:

```text
http://localhost:5173
```

Nginx then proxies `/api/` requests to the Spring Boot backend container.

```text
Browser
   ↓
localhost:5173
   ↓
Nginx
   ↓ /api/*
backend:8082
```

**No change is required to the frontend source code.** Ensure the value stays empty — do not set it to `http://localhost:5173`.

---

### Step 6: CORS Stays As-Is

**File:** `D:\rag-system\rag-backend\src\main\resources\application.properties`

```properties
app.cors.allowed-origins=http://localhost:5173,http://127.0.0.1:5173
```

**No change needed.** The browser URL is still `http://localhost:5173`.
Docker networking is internal — the browser doesn't see it.

---

### Step 7: Migrate Existing PostgreSQL Database

Your current database `AlphaPlace2` runs on `localhost:5434`.
Docker creates a new PostgreSQL container on internal port `5432` (mapped to `5434`).

**Before starting Docker containers:**

1. Dump your existing database:
```bash
pg_dump -h localhost -p 5434 -U postgres AlphaPlace2 > alphaplace2_backup.sql
```

2. Start Docker containers (db only):
```bash
docker-compose up -d db
```

3. Restore into Docker container:
```bash
docker exec -i rag-db psql -U postgres -d AlphaPlace2 < alphaplace2_backup.sql
```

4. Verify the pgvector extension is enabled:
```bash
docker exec -i rag-db psql -U postgres -d AlphaPlace2 -c "CREATE EXTENSION IF NOT EXISTS vector;"
```

5. Run the index script:
```bash
docker exec -i rag-db psql -U postgres -d AlphaPlace2 < src/main/resources/db/index.sql
```

**Do NOT stop your existing PostgreSQL until Docker is fully verified.**

---

### Step 8: Ollama Model Pull

When Docker starts, the `ollama` container automatically pulls `nomic-embed-text`.
This happens via the `entrypoint` command in `docker-compose.yml`.

First startup takes ~2-5 minutes for the model download.
Subsequent starts use the cached volume.

---

### Step 9: Backend Build Context

The `docker-compose.yml` references:
```yaml
backend:
  build:
    context: ./rag-backend
    dockerfile: Dockerfile
```

This means Docker expects the `rag-backend/` folder (with `Dockerfile`, `pom.xml`, `src/`) to be a sibling of `docker-compose.yml`.

**Final directory structure at `D:\rag-system\`:**
```
D:\rag-system\
├── .env
├── docker-compose.yml
│
├── rag-backend\
│   ├── Dockerfile
│   ├── .dockerignore
│   ├── pom.xml
│   └── src\
│
├── rag-frontend\
│   ├── Dockerfile
│   ├── .dockerignore
│   ├── nginx.conf
│   ├── package.json
│   └── src\
│
├── DockerConfigurations\    ← reference files (can be deleted later)
├── docs\
└── scripts\
```

---

### Step 10: Start Docker

```bash
cd D:\rag-system
docker-compose up -d --build
```

**Expected output:**
- `rag-db` starts (PostgreSQL 17 + pgvector)
- `rag-ollama` starts + pulls `nomic-embed-text`
- `rag-backend` builds Maven + starts Spring Boot
- `rag-frontend` builds React + starts Nginx

**Verify:**
```bash
docker-compose ps
```

All 4 services should show `Up` or `running`.

---

### Step 11: Verify Endpoints

| URL | Expected |
|---|---|
| `http://localhost:5173` | React app loads |
| `http://localhost:5173/login` | Login page |
| `http://localhost:5173/api/guest/ask` | Backend responds |
| `http://localhost:8082/api/guest/ask` | Direct backend access |

---

### Step 12: Document Generation in Docker

After database migration, regenerate all documents inside Docker:

```bash
curl -X POST http://localhost:8082/api/documents/generate/all
```

Or regenerate only options positions:
```bash
curl -X POST http://localhost:8082/api/documents/regenerate/options-positions
```

---

## Summary of All Changes

| Step | File | Change | Destroys Anything? |
|---|---|---|---|
| 1 | `application.properties` | Hardcoded values → `${ENV_VAR:default}` | No |
| 2 | Copy Docker files | Add 9 new files | No |
| 3 | `.env` | Fill in real secrets | No |
| 4 | `.gitignore` | Add `.env` | No |
| 5 | Frontend `.env` | Updated: `localhost:5173` for Docker | No |
| 6 | CORS config | No change | No |
| 7 | PostgreSQL | Dump + restore | No |
| 8 | Ollama | Auto-pulled by Docker | No |
| 9 | Directory structure | Verify layout | No |
| 10 | Docker | `docker-compose up` | No |
| 11 | Endpoints | Verify | No |
| 12 | Documents | Regenerate if needed | No |

**Total files changed:** 2 (`application.properties`, frontend `.env`)
**Total files added:** 9 (Docker files)
**Total files destroyed:** 0
**Total features lost:** 0

---

## Docker Commands Reference

### First Time Setup

```bash
# 1. Navigate to project root
cd D:\rag-system

# 2. Build and start all containers (first time)
docker-compose up -d --build
```

### Database Migration (First Time Only)

```bash
# 3. Dump existing PostgreSQL database
pg_dump -h localhost -p 5434 -U postgres AlphaPlace2 > alphaplace2_backup.sql

# 4. Start only the database container first
docker-compose up -d db

# 5. Wait for db to be healthy, then restore
docker exec -i rag-db psql -U postgres -d AlphaPlace2 < alphaplace2_backup.sql

# 6. Enable pgvector extension
docker exec -i rag-db psql -U postgres -d AlphaPlace2 -c "CREATE EXTENSION IF NOT EXISTS vector;"

# 7. Run index script
docker exec -i rag-db psql -U postgres -d AlphaPlace2 < src/main/resources/db/index.sql
```

### Start All Services

```bash
# 8. Start all 4 containers
docker-compose up -d --build

# 9. Verify all services are running
docker-compose ps
```

### Check Logs

```bash
# 10. Check all logs
docker-compose logs

# 11. Check specific service logs
docker-compose logs backend
docker-compose logs frontend
docker-compose logs db
docker-compose logs ollama

# 12. Follow logs in real-time
docker-compose logs -f backend
```

### Document Generation (After Migration)

```bash
# 13. Generate all missing documents
curl -X POST http://localhost:8082/api/documents/generate/all

# 14. Regenerate options positions (fixes wording)
curl -X POST http://localhost:8082/api/documents/regenerate/options-positions

# 15. Generate specific type
curl -X POST http://localhost:8082/api/documents/generate/options-positions
curl -X POST http://localhost:8082/api/documents/generate/stock-transactions
curl -X POST http://localhost:8082/api/documents/generate/deposit
curl -X POST http://localhost:8082/api/documents/generate/withdrawal
```

### Stop Services

```bash
# 16. Stop all containers
docker-compose down

# 17. Stop and remove volumes (DELETES ALL DATA)
docker-compose down -v
```

### Rebuild

```bash
# 18. Rebuild without cache (full rebuild)
docker-compose build --no-cache

# 19. Rebuild and restart
docker-compose up -d --build
```

### Individual Service Management

```bash
# 20. Restart only backend
docker-compose restart backend

# 21. Restart only frontend
docker-compose restart frontend

# 22. Stop only backend
docker-compose stop backend

# 23. Start only backend
docker-compose start backend
```

### Database Access

```bash
# 24. Connect to PostgreSQL inside Docker
docker exec -it rag-db psql -U postgres -d AlphaPlace2

# 25. Run SQL query
docker exec -i rag-db psql -U postgres -d AlphaPlace2 -c "SELECT COUNT(*) FROM \"portfolioDocuments\";"

# 26. Check document versions
docker exec -i rag-db psql -U postgres -d AlphaPlace2 -c "SELECT id, \"documentVersion\", \"documentSource\" FROM \"portfolioDocuments\" WHERE \"documentSource\" = 'SimulatedOptionsPosition' ORDER BY id;"
```

### Ollama Management

```bash
# 27. Check if model is downloaded
docker exec -it rag-ollama ollama list

# 28. Pull model manually (if not auto-pulled)
docker exec -it rag-ollama ollama pull nomic-embed-text
```

### Cleanup

```bash
# 29. Remove stopped containers
docker-compose rm

# 30. Remove all images for this project
docker-compose down --rmi all

# 31. Prune all unused Docker resources
docker system prune
```
